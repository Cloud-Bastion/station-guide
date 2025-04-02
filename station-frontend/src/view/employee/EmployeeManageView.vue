<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import Ref from "@/components/util/Ref";
import EmployeeAdminView from "@/view/employee/EmployeeAdminView.vue";
import EmployeeSchedulerView from "@/view/employee/EmployeeSchedulerView.vue";
import {ref, computed, onMounted} from 'vue';
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {useUserSession} from "@/service/OAuthUserService";

let currentSiteMode: Ref<string> = new Ref("schedule"); //schedule_admin, vacation, vacation_admin, illness, illness_admin, employee_admin

const settingsMenuOpen = ref(false);
const addEmployeeDialogOpen = ref(false); // Placeholder for add employee dialog
const selectedSetting = ref('employees'); // Add selected setting

// Placeholder data - replace with actual API calls
interface Employee {
  id: number;
  name: string;
  email: string;
  // Add other employee properties as needed
}

const employees = ref<Employee[]>([]);
const displayedEmployees = ref<Employee[]>([]);

// Computed property for filtered employees (placeholder for search functionality)
const filteredEmployees = computed(() => {
  // Implement filtering logic here based on search input
  return employees.value;
});

// Placeholder for fetching employees - replace with actual API call
onMounted(async () => {
  // Fetch employees from backend and populate 'employees' ref
  // Example:
  // employees.value = await EmployeeService.getAllEmployees();
  // displayedEmployees.value = employees.value;

  // Placeholder data for demonstration
    employees.value = [
        { id: 1, name: 'Max Mustermann', email: 'max.mustermann@example.com' },
        { id: 2, name: 'Erika Mustermann', email: 'erika.mustermann@example.com' },
    ];
    displayedEmployees.value = employees.value;
});

function changeSiteMode(mode: string) {
  console.log("CHANGED SITE TO " + currentSiteMode.value)
  currentSiteMode.value = mode;
}

function clearEmployeeSearch() {
    //Implement
}

function updateEmployee(employee: Employee) {
    //Implement
}

const userManager = useUserSession.value
</script>

<template>
  <div :class="$style['top-level-container']">
    <SidebarComponent site="employee-management"/>
<!--    <h1>{{userManager.user.value.profile}}</h1>-->
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

        <!-- Employee Admin View Content -->
        <div v-if="currentSiteMode.value === 'employee_admin'" :class="$style['employee-admin-view']">
          <div :class="$style['settings-container']">
            <div :class="$style['employee-count']">
              {{ displayedEmployees.length }} Mitarbeiter
            </div>

            <button :class="$style['settings-button']" @click="settingsMenuOpen = !settingsMenuOpen">
              <FontAwesomeIcon icon="fa-gear"/>
              <span>Einstellungen</span>
            </button>
          </div>

          <div :class="$style['employee-parent']">
            <table :class="$style['employee-container']">
              <transition-group name="employee-list" tag="tbody">
                <tr :class="$style['employee-entry-container']"
                    v-for="employee in displayedEmployees"
                    :key="employee.id"
                >
                  <td :class="$style['employee-id']">#{{ employee.id }}</td>
                  <td :class="$style['employee-name']">{{ employee.name }}</td>
                  <td :class="$style['employee-email']">{{ employee.email }}</td>
                  <td>
                    <FontAwesomeIcon :icon="['fas', 'info-circle']" :class="$style['employee-info-icon']"/>
                  </td>
                </tr>
              </transition-group>
            </table>
          </div>

          <!-- Settings Menu -->
          <transition name="settings-menu">
            <div v-if="settingsMenuOpen" :class="$style['settings-menu']">
              <div :class="$style['settings-menu-header']">
                <h2>Einstellungen</h2>
                <button @click="settingsMenuOpen = false" :class="$style['close-button']">
                  <FontAwesomeIcon icon="times"/>
                </button>
              </div>

              <div :class="$style['settings-menu-tabs']">
                <button
                    :class="[$style['tab-button'], selectedSetting === 'employees' ? $style['active-tab'] : '']"
                    @click="selectedSetting = 'employees'">
                  Mitarbeiter
                </button>
              </div>

              <div :class="$style['settings-menu-content']">
                <!-- Employees Section -->
                <div v-if="selectedSetting === 'employees'">
                  <button @click="addEmployeeDialogOpen = true" :class="$style['add-employee-button']">
                    <FontAwesomeIcon icon="plus" :class="$style['add-employee-icon']"/>
                    <span>Mitarbeiter hinzufügen</span>
                  </button>

                  <div :class="$style['search-bar-container']">
                    <FontAwesomeIcon icon="search" :class="$style['search-icon']"/>
                    <input
                        type="text"
                        :placeholder="'Mitarbeiter suchen...'"
                        :class="$style['search-input']"
                    />
                    <button @click="clearEmployeeSearch" :class="$style['clear-search-button']">
                      <FontAwesomeIcon icon="times"/>
                    </button>
                  </div>

                  <!-- Table for Employees -->
                  <table :class="$style['employees-table']" v-if="displayedEmployees.length > 0">
                    <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Email</th>
                    </tr>
                    </thead>
                    <transition-group name="employee-list" tag="tbody">
                      <tr v-for="employee in displayedEmployees" :key="employee.id">
                        <td>{{ employee.id }}</td>
                        <td>
                          <input type="text" v-model="employee.name" @change="updateEmployee(employee)"
                                 :class="$style['editable-input']"/>
                        </td>
                        <td>
                          <input type="text" v-model="employee.email" @change="updateEmployee(employee)" :class="$style['editable-input']" />
                        </td>
                      </tr>
                    </transition-group>
                  </table>
                  <div>
                    Keine passenden Mitarbeiter gefunden.
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;
$input-focus: #ff4500; // Red focus
$border-design: 0.1vh solid #555;

.top-level-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.page-container {
  // Removed display: flex and height
}

.main-content {
    // Removed flex-grow: 1
  display: flex;
  flex-direction: column;
}

.submenu-container {
  display: flex;
  width: 100%;
  flex-direction: row;
  margin: 0 25px;
  padding: 10px; // More padding
  border-radius: $border-radius;
  background-color: $bg-medium; // Consistent background
  white-space: nowrap;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); // Add shadow

  .submenu-button {
    font-size: small;
    color: #b0b0b0;
    margin: 0 5px; // Consistent margin
    padding: 10px 15px; // More padding
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
    background-color: $accent; // Use accent color for active state

    &:hover {
      background-color: $accent-hover; // Darker accent on hover
    }
  }
}

.content-wrapper {
  padding: 20px; // Add padding to the content area
  flex-grow: 1; // Allow content to grow
}

// --- Employee Admin View Styles ---

.employee-admin-view {
  .settings-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    border-radius: $border-radius;
    margin: 25px;
    padding: 10px 20px;
    background-color: $bg-medium;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);

    .employee-count {
      color: $text-color;
      font-size: 1rem;
    }

    .settings-button {
      display: flex;
      align-items: center;
      padding: 10px 15px;
      background-color: $accent;
      color: $text-color;
      border: none;
      border-radius: $border-radius;
      cursor: pointer;
      transition: background-color $transition-speed ease;

      &:hover {
        background-color: $accent-hover;
      }

      span {
        margin-left: 8px;
      }
    }
  }

  .employee-parent {
    display: flex;
    flex-direction: column;
    margin: 25px;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    background-color: $bg-medium;

    .employee-container {
      margin: 0;
      border: none;
      box-shadow: none;
      background-color: transparent;

      .employee-entry-container {
        background-color: transparent;
        transition: background-color $transition-speed ease;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); // Added shadow

        &:hover {
          background-color: $bg-light;
        }

        td {
          padding: 12px 20px;
          color: $text-color;
          font-size: 0.9rem;
        }

        .employee-id {
          color: #aaa;
        }

        .employee-name {
          flex-grow: 1;
        }
        .employee-info-icon{
          color: $accent;
        }
      }
    }
  }

    .settings-menu {
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background-color: $bg-medium;
      border-radius: $border-radius;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
      padding: 20px;
      width: 400px; /* Increased width */
      z-index: 1000; /* Ensure it's above other content */

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

          .clear-search-button{
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
.employee-list-leave-active
{
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.employee-list-enter-from,
.employee-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.employee-list-move{
  transition: transform 0.3s ease;
}

// Media query for smaller screens
@media (max-width: 768px) {
  .submenu-container {
    margin: 0 10px; // Reduce margin on smaller screens
    padding: 8px; // Reduce padding

    .submenu-button {
      padding: 8px 12px; // Reduce padding
      margin: 0 2px;
    }
  }
  .content-wrapper{
    padding: 10px;
  }
}
</style>
