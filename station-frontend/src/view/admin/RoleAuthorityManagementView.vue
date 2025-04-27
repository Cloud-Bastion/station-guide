<template>
  <div :class="$style['top-level-container']">
    <SidebarComponent site="role-authority-management"/>
    <div :class="$style['main-content']">
      <div :class="$style['management-container']">
        <h1 :class="$style['title']">Rollen- & Berechtigungsverwaltung</h1>

        <div :class="$style['layout-container']">
          <!-- Roles List -->
          <div :class="$style['roles-list-section']">
            <div :class="$style['section-header']">
              <h2>Rollen</h2>
              <button v-if="canCreateRole" @click="openCreateRoleModal" :class="$style['add-button']">
                <FontAwesomeIcon icon="plus"/>
                Neue Rolle
              </button>
            </div>
            <div v-if="isLoadingRoles" :class="$style['loading-message']">Lade Rollen...</div>
            <div v-else-if="rolesError" :class="$style['error-message']">{{ rolesError }}</div>
            <ul v-else :class="$style['roles-list']">
              <li
                  v-for="role in roles"
                  :key="role.id"
                  :class="[$style['role-item'], selectedRole?.id === role.id ? $style['selected'] : '']"
                  @click="selectRole(role)"
              >
                {{ role.displayName }}
              </li>
            </ul>
          </div>

          <!-- Authorities Detail View -->
          <div :class="$style['authorities-detail-section']">
            <div :class="$style['section-header']">
              <h2>Berechtigungen für {{ selectedRole?.displayName || 'Keine Rolle ausgewählt' }}</h2>
            </div>
            <div v-if="!selectedRole" :class="$style['placeholder']">
              Wählen Sie eine Rolle aus der Liste aus, um deren Berechtigungen anzuzeigen und zu bearbeiten.
            </div>
            <div v-else>
              <div v-if="isLoadingAuthorities" :class="$style['loading-message']">Lade Berechtigungen...</div>
              <div v-else-if="authoritiesError" :class="$style['error-message']">{{ authoritiesError }}</div>
              <div v-else>
                <div :class="$style['current-authorities']">
                  <h3>Aktuelle Berechtigungen:</h3>
                  <ul v-if="selectedRole.authorities.length > 0" :class="$style['authorities-list']">
                    <li v-for="auth in selectedRole.authorities" :key="auth.name" :class="$style['authority-item']">
                      <span>{{ auth.name }}</span>
                      <button v-if="canUpdateRole" @click="removeAuthority(auth.name)" :class="$style['remove-button']"
                              title="Berechtigung entfernen">
                        <FontAwesomeIcon icon="times"/>
                      </button>
                    </li>
                  </ul>
                  <p v-else :class="$style['no-authorities']">Diese Rolle hat keine zugewiesenen Berechtigungen.</p>
                </div>

                <div v-if="canUpdateRole" :class="$style['add-authority-section']">
                  <h3>Berechtigung hinzufügen:</h3>
                  <div :class="$style['authority-input-group']">
                    <input
                        type="text"
                        v-model="newAuthorityName"
                        placeholder="Berechtigungsname eingeben..."
                        :class="$style['authority-input']"
                        list="available-authorities-list"
                    />
                    <!-- Datalist for suggestions -->
                    <datalist id="available-authorities-list">
                      <option v-for="auth in availableAuthorities" :key="auth.name" :value="auth.name"></option>
                    </datalist>
                    <button @click="addAuthority" :disabled="!newAuthorityName.trim()"
                            :class="$style['add-button-small']">
                      <FontAwesomeIcon icon="plus"/>
                      Hinzufügen
                    </button>
                  </div>
                  <div v-if="addAuthorityError" :class="$style['error-message-small']">{{ addAuthorityError }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Role Modal -->
    <div v-if="showCreateModal" :class="$style['modal-overlay']">
      <div :class="$style['modal-content']">
        <div :class="$style['modal-header']">
          <h2>Neue Rolle erstellen</h2>
          <button @click="closeCreateRoleModal" :class="$style['close-button']">
            <FontAwesomeIcon icon="times"/>
          </button>
        </div>
        <div :class="$style['modal-body']">
          <div :class="$style['form-group']">
            <label for="new-role-name">Rollenname:</label>
            <input type="text" id="new-role-name" v-model="newRole.name" :class="$style['input']" required/>
          </div>
          <div :class="$style['form-group']">
            <label for="new-role-displayname">Displayname:</label>
            <input type="text" id="new-role-displayname" v-model="newRole.displayName" :class="$style['input']"
                   required/>
          </div>
          <div :class="$style['form-group']">
            <label for="initial-authorities">Initiale Berechtigungen (kommagetrennt, optional):</label>
            <input type="text" id="initial-authorities" v-model="newRole.initialAuthoritiesString"
                   :class="$style['input']" placeholder="z.B. task:read,task:create"/>
          </div>
          <div v-if="createRoleError" :class="$style['error-message']">{{ createRoleError }}</div>
          <button @click="submitCreateRole" :disabled="!newRole.name.trim() || isCreatingRole"
                  :class="$style['create-button']">
            <FontAwesomeIcon v-if="isCreatingRole" icon="spinner" spin/>
            {{ isCreatingRole ? 'Wird erstellt...' : 'Rolle erstellen' }}
          </button>
        </div>
      </div>
    </div>
    <!-- End Create Role Modal -->

  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, computed, reactive} from 'vue';
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import RoleAuthorityService, {UserRole, UserAuthority, UserRoleCreateRequest} from "@/service/RoleAuthorityService";
import {useAuthStore} from "@/storage/AuthUserStore";

const authStore = useAuthStore();

// State
const roles = ref<UserRole[]>([]);
const selectedRole = ref<UserRole | null>(null);
const availableAuthorities = ref<UserAuthority[]>([]); // For suggestions
const newAuthorityName = ref('');
const isLoadingRoles = ref(false);
const isLoadingAuthorities = ref(false); // Separate loading for details
const rolesError = ref('');
const authoritiesError = ref(''); // Error for detail view
const addAuthorityError = ref(''); // Error specific to adding an authority

// Create Role Modal State
const showCreateModal = ref(false);
const isCreatingRole = ref(false);
const createRoleError = ref('');
const newRole = reactive({
  name: '',
  displayName: '',
  initialAuthoritiesString: '', // Input as comma-separated string
});

// Permissions (using placeholders)
const canReadRole = computed(() => true)
//authStore.hasPermission('role:read'));
const canCreateRole = computed(() => true)
//computed(() =>
//authStore.hasPermission('role:create'));
const canUpdateRole = computed(() => true)
//authStore.hasPermission('role:update'));

// --- Lifecycle ---
onMounted(async () => {
  if (canReadRole.value) {
    await loadRoles();
  } else {
    rolesError.value = "Keine Berechtigung zum Anzeigen von Rollen.";
  }
});

// --- Methods ---
async function loadRoles() {
  isLoadingRoles.value = true;
  rolesError.value = '';
  selectedRole.value = null; // Deselect role when reloading list
  try {
    roles.value = await RoleAuthorityService.getAllRoles();
    availableAuthorities.value = RoleAuthorityService.extractUniqueAuthorities(roles.value);
  } catch (error: any) {
    console.error("Error loading roles:", error);
    rolesError.value = `Fehler beim Laden der Rollen: ${error.message || 'Unbekannter Fehler'}`;
  } finally {
    isLoadingRoles.value = false;
  }
}

function selectRole(role: UserRole) {
  selectedRole.value = role;
  newAuthorityName.value = ''; // Clear input when selecting a new role
  authoritiesError.value = ''; // Clear errors for the detail view
  addAuthorityError.value = ''; // Clear add error
}

async function addAuthority() {
  if (!selectedRole.value || !newAuthorityName.value.trim() || !canUpdateRole.value) {
    return;
  }

  isLoadingAuthorities.value = true;
  addAuthorityError.value = '';
  const roleName = selectedRole.value.name;
  const authToAdd = newAuthorityName.value.trim();

  try {
    const updatedRole = await RoleAuthorityService.addAuthorityToRole(roleName, authToAdd);
    // Update the selected role and the main roles list
    updateRoleInList(updatedRole);
    selectedRole.value = updatedRole; // Update the detailed view
    newAuthorityName.value = ''; // Clear input
    // Refresh available authorities if the added one was new
    if (!availableAuthorities.value.some(auth => auth.name === authToAdd)) {
      availableAuthorities.value = RoleAuthorityService.extractUniqueAuthorities(roles.value);
    }
  } catch (error: any) {
    console.error(`Error adding authority ${authToAdd} to role ${roleName}:`, error);
    addAuthorityError.value = `Fehler beim Hinzufügen: ${error.response?.data?.message || error.message || 'Unbekannt'}`;
  } finally {
    isLoadingAuthorities.value = false;
  }
}

async function removeAuthority(authorityName: string) {
  if (!selectedRole.value || !canUpdateRole.value) {
    return;
  }
  if (!confirm(`Sind Sie sicher, dass Sie die Berechtigung "${authorityName}" von der Rolle "${selectedRole.value.name}" entfernen möchten?`)) {
    return;
  }

  isLoadingAuthorities.value = true;
  authoritiesError.value = ''; // Clear general detail error
  const roleName = selectedRole.value.name;

  try {
    const updatedRole = await RoleAuthorityService.removeAuthorityFromRole(roleName, authorityName);
    // Update the selected role and the main roles list
    updateRoleInList(updatedRole);
    selectedRole.value = updatedRole; // Update the detailed view
  } catch (error: any) {
    console.error(`Error removing authority ${authorityName} from role ${roleName}:`, error);
    authoritiesError.value = `Fehler beim Entfernen: ${error.response?.data?.message || error.message || 'Unbekannt'}`;
  } finally {
    isLoadingAuthorities.value = false;
  }
}

// Helper to update the role in the main list
function updateRoleInList(updatedRole: UserRole) {
  const index = roles.value.findIndex(r => r.id === updatedRole.id);
  if (index !== -1) {
    roles.value[index] = updatedRole;
  }
}

// --- Create Role Modal Methods ---
function openCreateRoleModal() {
  if (!canCreateRole.value) return;
  newRole.name = '';
  newRole.displayName = '';
  newRole.initialAuthoritiesString = '';
  createRoleError.value = '';
  showCreateModal.value = true;
}

function closeCreateRoleModal() {
  showCreateModal.value = false;
}

async function submitCreateRole() {
  if (!newRole.name.trim() || !canCreateRole.value) {
    return;
  }

  isCreatingRole.value = true;
  createRoleError.value = '';

  const initialAuthorities = newRole.initialAuthoritiesString
      .split(',')
      .map(auth => auth.trim())
      .filter(auth => auth.length > 0);

  const createRequest: UserRoleCreateRequest = {
    name: newRole.name.trim(),
    displayName: newRole.displayName,
    initialAuthorities: initialAuthorities,
  };

  try {
    const createdRole = await RoleAuthorityService.createRole(createRequest);
    roles.value.push(createdRole); // Add to the list
    roles.value.sort((a, b) => a.name.localeCompare(b.name)); // Keep sorted
    availableAuthorities.value = RoleAuthorityService.extractUniqueAuthorities(roles.value); // Update suggestions
    closeCreateRoleModal();
    selectRole(createdRole); // Select the newly created role
  } catch (error: any) {
    console.error("Error creating role:", error);
    createRoleError.value = `Fehler beim Erstellen der Rolle: ${error.response?.data?.message || error.message || 'Unbekannter Fehler'}`;
  } finally {
    isCreatingRole.value = false;
  }
}

</script>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0;
$accent: #ff4500;
$accent-hover: #b83200;
$border-radius: 6px;
$transition-speed: 0.2s;
$input-bg: #333;
$input-border: #555;
$input-focus-border: $accent;
$error-color: $accent;
$selected-bg: lighten($bg-light, 5%);
$button-padding: 8px 12px;
$button-padding-small: 6px 10px;

// --- Base Styles ---
.top-level-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow-y: auto;
}

.management-container {
  display: flex;
  flex-direction: column;
  margin: 25px;
  padding: 25px;
  background-color: $bg-medium;
  border-radius: $border-radius;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  flex-grow: 1;
}

.title {
  font-size: 1.8rem;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid $bg-light;
}

.layout-container {
  display: flex;
  flex-direction: row;
  gap: 30px;
  flex-grow: 1;
  min-height: 0; // Prevent flex items from overflowing container
}

// --- Section Styles ---
.roles-list-section,
.authorities-detail-section {
  display: flex;
  flex-direction: column;
  background-color: $bg-light;
  border-radius: $border-radius;
  padding: 20px;
  overflow-y: auto; // Allow scrolling within sections if needed
}

.roles-list-section {
  flex: 1; // Adjust flex basis as needed (e.g., flex: 1 0 300px;)
  min-width: 250px;
}

.authorities-detail-section {
  flex: 2; // Takes more space
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid $input-border;

  h2 {
    font-size: 1.3rem;
    font-weight: 500;
    color: $text-color;
    margin: 0;
  }
}

// --- List Styles ---
.roles-list,
.authorities-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 60vh; // Limit height for scrolling
  overflow-y: auto;
}

.role-item,
.authority-item {
  padding: 10px 12px;
  border-bottom: 1px solid $input-bg;
  transition: background-color $transition-speed ease;
  cursor: default; // Default cursor for authority items

  &:last-child {
    border-bottom: none;
  }
}

.role-item {
  cursor: pointer;
  color: $text-color-light;
  font-weight: 500;

  &:hover {
    background-color: $input-bg;
    color: $text-color;
  }

  &.selected {
    background-color: $selected-bg;
    color: $accent;
    font-weight: 600;
  }
}

.authority-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: $text-color;
  font-size: 0.95rem;
}

// --- Detail View Styles ---
.placeholder,
.no-authorities {
  color: $text-color-light;
  font-style: italic;
  text-align: center;
  padding: 20px;
}

.current-authorities,
.add-authority-section {
  margin-bottom: 25px;

  h3 {
    font-size: 1.1rem;
    font-weight: 500;
    color: $text-color;
    margin-bottom: 10px;
  }
}

.authority-input-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.authority-input {
  flex-grow: 1;
  padding: $button-padding-small;
  border: 1px solid $input-border;
  border-radius: $border-radius;
  background-color: $input-bg;
  color: $text-color;
  font-size: 0.9rem;
  transition: border-color $transition-speed ease;

  &:focus {
    border-color: $input-focus-border;
    outline: none;
  }
}

// --- Buttons ---
.add-button,
.create-button,
.add-button-small {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background-color: $accent;
  color: $text-color;
  border: none;
  border-radius: $border-radius;
  cursor: pointer;
  transition: background-color $transition-speed ease;
  font-weight: 500;
  white-space: nowrap;

  &:hover:not(:disabled) {
    background-color: $accent-hover;
  }

  &:disabled {
    background-color: darken($accent, 20%);
    cursor: not-allowed;
    opacity: 0.7;
  }
}

.add-button {
  padding: $button-padding;
  font-size: 0.9rem;
}

.create-button {
  padding: 10px 15px;
  font-size: 1rem;
  width: 100%;
  justify-content: center;
  margin-top: 15px;
}

.add-button-small {
  padding: $button-padding-small;
  font-size: 0.9rem;
}


.remove-button {
  background: none;
  border: none;
  color: $text-color-light;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
  border-radius: 50%;
  transition: color $transition-speed ease, background-color $transition-speed ease;

  &:hover {
    color: $error-color;
    background-color: rgba($error-color, 0.1);
  }
}

// --- Messages ---
.loading-message,
.error-message,
.error-message-small {
  padding: 10px;
  border-radius: $border-radius;
  text-align: center;
  margin-top: 10px;
}

.loading-message {
  color: $text-color-light;
}

.error-message {
  color: $error-color;
  background-color: rgba($error-color, 0.1);
  border: 1px solid rgba($error-color, 0.3);
  font-size: 0.9rem;
}

.error-message-small {
  color: $error-color;
  font-size: 0.85rem;
  margin-top: 5px;
}


// --- Modal Styles ---
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050; // Ensure modal is on top
  padding: 20px;
}

.modal-content {
  background-color: $bg-medium;
  border-radius: $border-radius;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  padding: 0; // Handled by header/body
  width: 100%;
  max-width: 500px;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid $bg-light;

  h2 {
    color: $text-color;
    font-size: 1.4rem;
    margin: 0;
  }

  .close-button {
    background: none;
    border: none;
    color: $text-color-light;
    cursor: pointer;
    font-size: 1.5rem;
    padding: 5px;
    line-height: 1;
    transition: color $transition-speed ease;

    &:hover {
      color: $text-color;
    }
  }
}

.modal-body {
  padding: 20px 25px;
  display: flex;
  flex-direction: column;
  gap: 15px;

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 5px;

    label {
      color: $text-color;
      font-size: 0.9rem;
      font-weight: 500;
    }

    .input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid $input-border;
      border-radius: $border-radius;
      background-color: $input-bg;
      color: $text-color;
      box-sizing: border-box;
      font-size: 0.95rem;
      transition: border-color $transition-speed ease, box-shadow $transition-speed ease;

      &:focus {
        border-color: $input-focus-border;
        outline: none;
        box-shadow: 0 0 0 2px rgba($input-focus-border, 0.3);
      }
    }
  }
}

// --- Responsive ---
@media (max-width: 768px) {
  .layout-container {
    flex-direction: column;
    gap: 20px;
  }
  .roles-list-section,
  .authorities-detail-section {
    max-height: 45vh; // Adjust height limits for smaller screens
  }
  .management-container {
    margin: 15px;
    padding: 15px;
  }
  .title {
    font-size: 1.5rem;
    margin-bottom: 15px;
  }
  .section-header h2 {
    font-size: 1.1rem;
  }
}

</style>
