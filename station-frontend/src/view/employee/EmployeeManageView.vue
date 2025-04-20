<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import Ref from "@/components/util/Ref";
import EmployeeAdminView from "@/view/employee/EmployeeAdminView.vue"; // This component seems unused now?
import EmployeeSchedulerView from "@/view/employee/EmployeeSchedulerView.vue";
import CreateEmployeeDialog from "@/components/employee/CreateEmployeeDialog.vue"; // Import the new dialog
import EmployeeService, { Employee } from "@/service/EmployeeService"; // Import service and Employee interface
import { ref, computed, onMounted } from 'vue';
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

let currentSiteMode: Ref<string> = new Ref("schedule"); // schedule, vacation, illness, employee_admin

const settingsMenuOpen = ref(false); // Keep for potential future settings
const showCreateEmployeeDialog = ref(false); // Control the create dialog visibility
const selectedSetting = ref('employees'); // Keep for potential future settings tabs

const employees = ref<Employee[]>([]); // Use the Employee interface from EmployeeService
const isLoadingEmployees = ref(false);
const loadingError = ref('');

// Fetch employees when the component mounts
onMounted(async () => {
  if (currentSiteMode.value === 'employee_admin') {
    await loadEmployees();
  }
});

// Function to load employees from the service
async function loadEmployees() {
  isLoadingEmployees.value = true;
  loadingError.value = '';
  try {
    employees.value = await EmployeeService.getAllEmployees();
  } catch (error: any) {
    console.error("Error loading employees:", error);
    loadingError.value = `Fehler beim Laden der Mitarbeiter: ${error.message || 'Unbekannter Fehler'}`;
    employees.value = []; // Clear list on error
  } finally {
    isLoadingEmployees.value = false;
  }
}

// Computed property for displayed employees (can add search/filter later)
const displayedEmployees = computed(() => {
  return employees.value;
});

function changeSiteMode(mode: string) {
  console.log("CHANGED SITE TO " + mode)
  currentSiteMode.value = mode;
  // Load employees if switching to the admin view
  if (mode === 'employee_admin' && employees.value.length === 0) {
      loadEmployees();
  }
}

// --- Dialog Handling ---
function openCreateEmployeeDialog() {
  showCreateEmployeeDialog.value = true;
}

function closeCreateEmployeeDialog() {
  showCreateEmployeeDialog.value = false;
}

async function handleEmployeeCreated() {
  closeCreateEmployeeDialog();
  await loadEmployees(); // Refresh the list after creation
}

// --- Placeholder functions (implement if needed) ---
function clearEmployeeSearch() {
  // Implement search clearing logic
}

function updateEmployee(employee: Employee) {
  // Implement logic to open an edit dialog or similar
  console.log("Update employee:", employee.id);
}

function openEmployeeDetails(employee: Employee) {
    // Implement logic to show detailed view/modal for the employee
    console.log("Show details for employee:", employee.id);
    // Could potentially reuse/adapt the settingsMenu logic or create a new details modal
}

</script>

<template>
  <div :class="$style['top-level-container']">
    <SidebarComponent site="employee-management"/>
    <div :class="$style['main-content']">
      <div :class="$style['submenu-container']">
        <button
            :class="[$style['submenu-button'], currentSiteMode.value === 'schedule' ? $style['submenu-button-active'] : '']"
            @click="changeSiteMode('schedule')">
          Mitarbeiterplanung
        </button>
        <button
            :class="[$style['submenu-button'], currentSiteMode.value === 'vacation' ? $style['submenu-button-active'] : '']"
            @click="changeSiteMode('vacation')">
          Urlaubsplanung
        </button>
        <button
            :class="[$style['submenu-button'], currentSiteMode.value === 'illness' ? $style['submenu-button-active'] : '']"
            @click="changeSiteMode('illness')">
          Krankmeldungen
        </button>
        <button
            :class="[$style['submenu-button'], currentSiteMode.value === 'employee_admin' ? $style['submenu-button-active'] : '']"
            @click="changeSiteMode('employee_admin')">
          Mitarbeiterpflege
        </button>
      </div>

      <div :class="$style['content-wrapper']">
        <EmployeeSchedulerView v-if="currentSiteMode.value === 'schedule'"/>
        <!-- <VacationView v-if="currentSiteMode.value === 'vacation'" /> -->
        <!-- <IllnessView v-if="currentSiteMode.value === 'illness'" /> -->

        <!-- Employee Admin View Content -->
        <div v-if="currentSiteMode.value === 'employee_admin'" :class="$style['employee-admin-view']">
          <div :class="$style['settings-container']">
             <!-- Search Bar (Optional) -->
             <div :class="$style['search-bar-container']">
                <FontAwesomeIcon icon="search" :class="$style['search-icon']"/>
                <input
                    type="text"
                    placeholder="Mitarbeiter suchen..."
                    :class="$style['search-input']"
                    />
                <button @click="clearEmployeeSearch" :class="$style['clear-search-button']" v-if="false"> <!-- Hide clear button for now -->
                    <FontAwesomeIcon icon="times"/>
                </button>
            </div>
            <div :class="$style['employee-count']">
              {{ displayedEmployees.length }} Mitarbeiter
            </div>
            <!-- Changed button to open the create dialog -->
            <button :class="$style['settings-button']" @click="openCreateEmployeeDialog">
              <FontAwesomeIcon icon="plus"/>
              <span>Mitarbeiter anlegen</span>
            </button>
          </div>

          <div v-if="isLoadingEmployees" :class="$style['loading-message']">
            Lade Mitarbeiter... <FontAwesomeIcon icon="spinner" spin />
          </div>
          <div v-if="loadingError" :class="$style['error-message']">
            {{ loadingError }}
          </div>

          <div v-if="!isLoadingEmployees && !loadingError" :class="$style['employee-parent']">
            <table :class="$style['employee-container']">
              <thead>
                <tr>
                  <th>Badge ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Status/Typ</th>
                  <th>Aktionen</th>
                </tr>
              </thead>
              <transition-group name="employee-list" tag="tbody">
                <tr :class="$style['employee-entry-container']"
                    v-for="employee in displayedEmployees"
                    :key="employee.id"
                >
                  <td :class="$style['employee-id']">#{{ employee.badgeNumber || 'N/A' }}</td>
                  <td :class="$style['employee-name']">{{ employee.firstName }} {{ employee.lastName }}</td>
                  <td :class="$style['employee-email']">{{ employee.email }}</td>
                  <td :class="$style['employee-wage']">
                    <!-- Display Minijob/Hourly Wage if available -->
                    {{ employee.minijob ? "Minijob" : (employee.hourlyWage ? employee.hourlyWage + " Std / Woche" : "Vollzeit/Teilzeit") }}
                  </td>
                  <td :class="$style['employee-actions']">
                    <button @click="openEmployeeDetails(employee)" :class="$style['action-button']" title="Details">
                        <FontAwesomeIcon icon="fa-eye" />
                    </button>
                     <button @click="updateEmployee(employee)" :class="$style['action-button']" title="Bearbeiten">
                        <FontAwesomeIcon icon="fa-edit" />
                    </button>
                    <!-- Add more actions like deactivate/archive -->
                  </td>
                </tr>
              </transition-group>
            </table>
             <div v-if="displayedEmployees.length === 0" :class="$style['no-employees-message']">
                Keine Mitarbeiter gefunden.
            </div>
          </div>

          <!-- Settings Menu (kept for potential future use, but hidden for now) -->
          <transition name="settings-menu">
            <div v-if="settingsMenuOpen" :class="$style['settings-menu']">
              <!-- Settings content can be added here later -->
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>

  <!-- Create Employee Dialog -->
  <CreateEmployeeDialog
    v-if="showCreateEmployeeDialog"
    @close="closeCreateEmployeeDialog"
    @employee-created="handleEmployeeCreated"
  />
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;
$input-focus: #ff4500; // Red focus
$border-design: 0.1vh solid #555; // Use a fixed unit like px or rem instead of vh for borders

.top-level-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1; // Allow main content to fill space
  overflow: hidden; // Prevent main content itself from scrolling, allow content-wrapper to scroll
}

.submenu-container {
  display: flex;
  width: calc(100% - 50px); // Adjust width considering margin
  flex-direction: row;
  margin: 25px 25px 0 25px; // Add top margin, remove bottom
  padding: 10px;
  border-radius: $border-radius;
  background-color: $bg-medium;
  white-space: nowrap;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  flex-shrink: 0; // Prevent shrinking

  .submenu-button {
    font-size: small;
    color: $text-color-light;
    margin: 0 5px;
    padding: 10px 15px;
    flex-shrink: 0;
    background: none;
    border: none;
    border-radius: $border-radius;
    cursor: pointer;
    transition: background-color $transition-speed ease, color $transition-speed ease;

    &:hover {
      background-color: $bg-light;
      color: $text-color;
    }
  }

  .submenu-button-active {
    color: $text-color;
    background-color: $accent;

    &:hover {
      background-color: $accent-hover;
    }
  }
}

.content-wrapper {
  padding: 20px 25px 25px 25px; // Add padding, especially bottom
  flex-grow: 1; // Allow content to grow
  overflow-y: auto; // Enable scrolling for the content area ONLY
}

// --- Employee Admin View Styles ---

.employee-admin-view {
  display: flex;
  flex-direction: column;
  gap: 20px; // Add gap between elements

  .settings-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    border-radius: $border-radius;
    // margin: 25px; // Remove margin, handled by parent gap/padding
    padding: 15px 20px; // Adjusted padding
    background-color: $bg-medium;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3); // Slightly stronger shadow

    .search-bar-container { // Style for search bar
        display: flex;
        align-items: center;
        position: relative;
        flex-grow: 1; // Allow search to take space
        margin-right: 20px; // Space before count

        .search-icon {
            color: #777;
            position: absolute;
            left: 12px;
            z-index: 1;
        }

        .search-input {
            padding: 8px 12px 8px 35px; // Adjust padding for icon
            border: 1px solid $input-border;
            border-radius: $border-radius;
            background-color: $input-bg;
            color: $text-color;
            transition: border-color $transition-speed ease;
            width: 100%; // Take full width of container
            min-width: 200px; // Minimum width

            &:focus {
                border-color: $input-focus;
                outline: none;
            }
        }

        .clear-search-button { // Keep styles if needed later
            position: absolute;
            right: 10px;
            background: none;
            border: none;
            color: #aaa;
            cursor: pointer;
            transition: color $transition-speed ease;
            padding: 0;
            z-index: 1;

            &:hover {
                color: $text-color;
            }
        }
    }


    .employee-count {
      color: $text-color;
      font-size: 1rem;
      white-space: nowrap; // Prevent wrapping
      margin-right: 20px; // Space before button
    }

    .settings-button { // This is now the "Anlegen" button
      display: flex;
      align-items: center;
      padding: 10px 15px;
      background-color: $accent;
      color: $text-color;
      border: none;
      border-radius: $border-radius;
      cursor: pointer;
      transition: background-color $transition-speed ease;
      white-space: nowrap; // Prevent wrapping

      &:hover {
        background-color: $accent-hover;
      }

      span {
        margin-left: 8px;
      }
    }
  }

  .loading-message, .error-message, .no-employees-message {
      text-align: center;
      color: $text-color-light;
      padding: 20px;
      background-color: $bg-medium;
      border-radius: $border-radius;
      margin-top: 10px; // Add some space
  }
  .error-message {
      color: $accent;
      background-color: rgba($accent, 0.1);
      border: 1px solid rgba($accent, 0.3);
  }


  .employee-parent {
    display: flex;
    flex-direction: column;
    // margin: 25px; // Remove margin
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
    background-color: $bg-medium;
    overflow-x: auto; // Allow horizontal scrolling if table is too wide

    .employee-container {
      width: 100%;
      border-collapse: collapse; // Use collapse for cleaner borders

      thead tr {
         background-color: $bg-light; // Header background
         border-bottom: 2px solid $input-border; // Stronger header border
      }

      th, td {
        padding: 12px 15px; // Consistent padding
        text-align: left;
        border-bottom: 1px solid $input-border; // Separator lines
        color: $text-color;
        font-size: 0.9rem;
        white-space: nowrap; // Prevent wrapping in cells initially
      }

      th {
          font-weight: 600;
          color: $text-color-light;
      }

      tbody tr {
        transition: background-color $transition-speed ease;
        &:hover {
          background-color: $bg-light;
        }
      }

      .employee-entry-container {
        // background-color: transparent; // Already default

        .employee-id {
          color: #aaa;
          font-family: monospace; // Monospace for IDs
        }

        .employee-name {
          font-weight: 500;
        }
        .employee-email {
            color: $text-color-light;
        }
        .employee-wage {
            font-style: italic;
            color: $text-color-light;
        }

        .employee-actions {
            text-align: right; // Align actions to the right

            .action-button {
                background: none;
                border: none;
                color: $text-color-light;
                cursor: pointer;
                padding: 5px 8px;
                margin-left: 5px;
                border-radius: $border-radius;
                transition: color $transition-speed ease, background-color $transition-speed ease;

                &:hover {
                    color: $accent;
                    background-color: darken($bg-light, 5%);
                }
            }
        }
      }
    }
  }

  // Settings Menu (keep styles if needed later)
  .settings-menu {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    padding: 20px;
    width: 400px;
    z-index: 1000;

    // ... (rest of settings menu styles remain the same) ...
    .settings-menu-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      h2 {
        color: $text-color;
        font-size: 1.5rem;
        margin: 0;
      }

      .close-button {
        background: none;
        border: none;
        color: #aaa;
        cursor: pointer;
        font-size: 1.2rem;
        transition: color $transition-speed ease;

        &:hover {
          color: $text-color;
        }
      }
    }

    .settings-menu-tabs {
      display: flex;
      justify-content: space-around;
      margin-bottom: 10px;

      .tab-button {
        padding: 8px 12px;
        background: none;
        border: none;
        color: $text-color;
        cursor: pointer;
        transition: background-color $transition-speed ease, color $transition-speed ease;
        border-radius: $border-radius;

        &:hover {
          background-color: $bg-light;
        }
      }

      .active-tab {
        background-color: $accent;
        color: white;

        &:hover {
          background-color: $accent-hover;
        }
      }
    }

    .settings-menu-content {
      color: $text-color;

      .add-employee-button {
        display: flex;
        align-items: center;
        padding: 10px 15px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        margin-bottom: 10px;

        &:hover {
          background-color: $accent-hover;
        }

        .add-employee-icon {
          margin-right: 8px;
        }
      }

      .search-bar-container {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        position: relative;

        .search-icon {
          color: #777;
          position: absolute;
          left: 10px;
          z-index: 1;
        }

        .search-input {
          padding: 10px;
          padding-left: 35px;
          border: 1px solid $input-border;
          border-radius: $border-radius;
          background-color: $input-bg;
          color: $text-color;
          transition: border-color $transition-speed ease;
          flex-grow: 1;

          &:focus {
            border-color: $input-focus;
            outline: none;
          }
        }

        .clear-search-button {
          position: absolute;
          right: 10px;
          background: none;
          border: none;
          color: #aaa;
          cursor: pointer;
          transition: color $transition-speed ease;
          padding: 0;
          z-index: 1;

          &:hover {
            color: $text-color;
          }
        }
      }

      .employees-table {
        width: 100%;
        border-collapse: collapse;

        th, td {
          padding: 8px 12px;
          border-bottom: 1px solid $input-border;
          text-align: left;
        }

        th {
          background-color: $bg-light;
          color: $text-color;
        }

        .editable-input, .editable-select {
          padding: 6px;
          border: 1px solid $input-border;
          border-radius: $border-radius;
          background-color: $input-bg;
          color: $text-color;
          transition: border-color $transition-speed ease;
          width: 90%;

          &:focus {
            border-color: $input-focus;
            outline: none;
          }
        }
      }
    }
  }
}

// --- Animations ---
.settings-menu-enter-active,
.settings-menu-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.settings-menu-enter-from,
.settings-menu-leave-to {
  opacity: 0;
  transform: translate(-50%, -40%);
}

.employee-list-enter-active,
.employee-list-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.employee-list-enter-from,
.employee-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.employee-list-move {
  transition: transform 0.3s ease;
}

// Media query for smaller screens
@media (max-width: 768px) {
  .submenu-container {
    margin: 15px 15px 0 15px; // Adjust margin
    padding: 8px;
    overflow-x: auto; // Allow horizontal scroll for buttons

    .submenu-button {
      padding: 8px 12px;
      margin: 0 2px;
    }
  }
  .content-wrapper {
    padding: 15px; // Reduce padding
  }
  .employee-admin-view {
      .settings-container {
          flex-direction: column;
          align-items: stretch; // Stretch items
          gap: 10px;
          padding: 10px 15px;
          .search-bar-container { margin-right: 0; }
          .employee-count { text-align: center; margin-right: 0; }
          .settings-button { justify-content: center; } // Center button content
      }
      .employee-parent {
          .employee-container {
              th, td {
                  padding: 8px 10px;
                  font-size: 0.8rem; // Smaller font on mobile
              }
              .employee-actions {
                  .action-button {
                      padding: 4px 6px;
                      margin-left: 3px;
                  }
              }
          }
      }
  }
}
</style>
