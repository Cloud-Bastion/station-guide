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
                     :class="[$style['workprogramms-entity'], props.site === 'employee-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'id-badge']" size="lg" :class="$style['icon']"/>
          <span>Mitarbeiter-Management</span>
        </router-link>
        <router-link to="product-expire-management"
                     :class="[$style['workprogramms-entity'], props.site === 'product-expire-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'lemon']" size="lg" :class="$style['icon']"/>
          <span>MHD-Tool</span>
        </router-link>
        <router-link to="task-management"
                     :class="[$style['workprogramms-entity'], props.site === 'task-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'fa-rectangle-list']" size="lg" :class="$style['icon']"/>
          <span>Task-Manager</span>
        </router-link>
      </div>
    </div>
    <div :class="$style['menu-stick']"></div>
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

.menu-parent {
  display: flex;
  flex-direction: column;
  background-color: $bg-dark; // Consistent dark background

  .menu-container {
    display: flex;
    flex-direction: row;
    align-items: center; // Vertically center items
    margin: 0 30px; // Reduced margin
    padding: 20px 0; // Add vertical padding

    .logo {
      width: 120px; // Slightly smaller logo
      height: auto;
      margin-right: 30px; // Reduced margin
    }

    .workprogramms-container {
      display: flex;
      flex-direction: row;
      flex-grow: 1;
      justify-content: space-around; // Evenly space items

      .workprogramms-entity {
        color: #b0b0b0;
        text-decoration: none;
        display: flex; // Use flex for alignment
        flex-direction: column; // Stack icon and text
        align-items: center; // Center items horizontally
        padding: 10px 15px; // Add padding
        border-radius: $border-radius; // Add border-radius
        transition: background-color $transition-speed ease, color $transition-speed ease;

        &:hover {
          background-color: $bg-light; // Hover effect
          color: $text-color;

          .icon {
            color: $accent; // Change icon color on hover
          }
        }

        .icon {
          margin-bottom: 5px; // Space between icon and text
          color: #b0b0b0; // Icon color
          transition: color $transition-speed ease;
        }
        span{
          font-size: small;
        }
      }

      .workprogramms-entity-active {
        color: $text-color;
        background-color: $bg-light; // Active state background

        .icon {
          color: $accent; // Active state icon color
        }
      }
    }
  }

  .menu-stick {
    height: 2px; // Use height instead of flex
    margin: 10px 25px; // Consistent margin
    border-radius: 20px;
    background-color: #444; // Darker stick color
  }
}
</style>
