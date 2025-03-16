<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

const props = defineProps<{
  site: string;
}>()
</script>

<template>
  <div :class="$style['menu-parent']">
    <div :class="$style['menu-container']">
      <img src="/logo.png" alt="circle k logo" :class="$style['logo']">
      <div :class="$style['workprogramms-container']">
        <router-link to="employee-management"
                     :class="[$style['workprogramms-entity'], this.site === 'employee-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'id-badge']" size="lg"/>
          Mitarbeiter-Management
        </router-link>
        <router-link to="mhd-management"
                     :class="[$style['workprogramms-entity'], this.site === 'mhd-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'lemon']" size="lg"/>
          MHD-Tool
        </router-link>
        <router-link to="task-management"
                     :class="[$style['workprogramms-entity'], this.site === 'task-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'fa-rectangle-list']" size="lg"/>
          Task-Manager
        </router-link>
      </div>
    </div>
    <div :class="$style['menu-stick']"></div>
  </div>
</template>

<style lang="scss" module>
.menu-parent {
  display: flex;
  flex-direction: column;

  .menu-container {
    display: flex;
    flex-direction: row;
    flex-grow: 1;
    margin: 0 50px;

    .logo {
      width: 160px;
      height: auto;
    }

    .workprogramms-container {
      display: flex;
      flex-direction: row;
      flex-grow: 1;
      margin: 0 50px 0 50px;

      .workprogramms-entity {
        color: #b0b0b0;
        text-decoration: none;
        margin: 0 50px 0 50px;
        align-self: center;
      }

      .workprogramms-entity-active {
        color: white;
        text-decoration: none;
        align-self: center;
      }
    }
  }

  .menu-stick {
    flex: 0 0 2px;
    margin: 10px 25px;
    border-radius: 20px;
    background-color: rgba(50, 50, 66, 1);
  }
}
</style>