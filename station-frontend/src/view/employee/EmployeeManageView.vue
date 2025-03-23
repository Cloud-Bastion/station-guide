<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import Ref from "@/components/util/Ref";
import EmployeeAdminView from "@/view/employee/EmployeeAdminView.vue";
import EmployeeSchedulerView from "@/view/employee/EmployeeSchedulerView.vue";

let currentSiteMode: Ref<string> = new Ref("schedule"); //schedule_admin, vacation, vacation_admin, illness, illness_admin, employee_admin

function changeSiteMode(mode: string) {
  console.log("CHANGED SITE TO " + currentSiteMode.value)
  currentSiteMode.value = mode;
}
</script>

<template>
  <div :class="$style['page-container']">
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
        <EmployeeAdminView v-if="currentSiteMode.value === 'employee_admin'"/>
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

.page-container {
  display: flex;
  height: 100vh; // Full viewport height
}

.main-content {
  flex-grow: 1; // Take up remaining space
  display: flex;
  flex-direction: column;
  overflow-y: auto; // Add vertical scroll if needed
}

.submenu-container {
  display: flex;
  width: 100%;
  flex-direction: row;
  margin: 0 25px;
  padding: 10px; // More padding
  border-radius: $border-radius;
  background-color: $bg-medium; // Consistent background
  overflow-x: auto;
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
