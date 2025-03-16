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
  <SidebarComponent site="employee-management"/>
  <div :class="$style['submenu-container']">
    <div
        :class="[$style['submenu-entity'], currentSiteMode.value === 'schedule' ? $style['submenu-entity-active'] : '']"
        @click="changeSiteMode('schedule');">
      Mitarbeiterplanung
    </div>
    <div
        :class="[$style['submenu-entity'], currentSiteMode.value === 'vacation' ? $style['submenu-entity-active'] : '']"
        @click="changeSiteMode('vacation')">Urlaubsplanung
    </div>
    <div
        :class="[$style['submenu-entity'], currentSiteMode.value === 'illness' ? $style['submenu-entity-active'] : '']"
        @click="changeSiteMode('illness')">Krankmeldungen
    </div>
    <div
        :class="[$style['submenu-entity'], currentSiteMode.value === 'employee_admin' ? $style['submenu-entity-active'] : '']"
        @click="changeSiteMode('employee_admin')">Mitarbeiterpflege
    </div>
  </div>
  <EmployeeSchedulerView v-if="currentSiteMode.value === 'schedule'"/>
  <EmployeeAdminView v-if="currentSiteMode.value === 'employee_admin'"/>
</template>

<style lang="scss" module>
.submenu-container {
  display: flex;
  width: fit-content;
  flex-direction: row;
  margin: 0 25px;
  padding: 3px 3px 3px 3px;
  border-radius: 5px 5px 5px 5px;
  background-color: #222222;

  .submenu-entity {
    font-size: small;
    color: #b0b0b0;
    margin: 0 2px;
    padding: 8px 8px 8px 8px;
  }

  .submenu-entity-active {
    color: white;
    border-radius: 5px 5px 5px 5px;
    background-color: black;
  }
}
</style>