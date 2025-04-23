<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {ref, onMounted, computed} from 'vue';
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import AuthUserService from "@/service/AuthUserService";
import {useAuthStore} from "@/storage/AuthUserStore";

const props = defineProps<{
  site: string;
}>()

const authStore = useAuthStore(); // Get auth store instance

// Placeholder data for the user profile. Replace this with actual user data.
// Simulate fetching user data (replace with actual API call if needed)
onMounted(async () => {
  // In a real application, you would fetch user data from an API here.
  // Example:
  // try {
  //   const userData = await UserService.getCurrentUser();
  //   user.value = userData;
  // } catch (error) {
  //   console.error("Error fetching user data:", error);
  // }
});

const isDropdownOpen = ref(false);

// Computed property to toggle dropdown visibility
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// Close dropdown when clicking outside
const closeDropdown = (event: MouseEvent) => {
    // Check if the click target exists and if the userProfile ref exists and contains the target
    const userProfileElement = document.getElementById('userProfileContainer'); // Use a static ID
    if (isDropdownOpen.value && event.target instanceof Node && userProfileElement && !userProfileElement.contains(event.target)) {
        isDropdownOpen.value = false;
    }
};


const logout = () => {
  AuthUserService.logout()
};

onMounted(() => {
  window.addEventListener('click', closeDropdown);
});

// Use onBeforeUnmount for cleanup
import { onBeforeUnmount } from 'vue';
onBeforeUnmount(() => {
    window.removeEventListener('click', closeDropdown);
})
</script>

<template>
  <div :class="$style['menu-parent']">
    <div :class="$style['menu-container']">
      <img src="/logo.png" alt="circle k logo" :class="$style['logo']">

      <div :class="$style['workprogramms-container']">
        <router-link to="/employee/management"
                     :class="[$style['workprogramms-entity'], props.site === 'employee-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'id-badge']" size="lg" :class="$style['icon']"/>
          <span>Mitarbeiter</span>
        </router-link>
        <router-link v-if="authStore.hasPermission('expire_product:read')" to="/expire/management"
                     :class="[$style['workprogramms-entity'], props.site === 'product-expire-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'lemon']" size="lg" :class="$style['icon']"/>
          <span>MHD-Tool</span>
        </router-link>
        <router-link to="/tasks"
                     :class="[$style['workprogramms-entity'], props.site === 'task-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'fa-rectangle-list']" size="lg" :class="$style['icon']"/>
          <span>Aufgaben</span>
        </router-link>
         <!-- New Link for Role/Authority Management -->
         <router-link v-if="authStore.hasPermission('role:read')" to="/admin/roles"
                      :class="[$style['workprogramms-entity'], props.site === 'role-authority-management' ? $style['workprogramms-entity-active'] : '']">
           <FontAwesomeIcon :icon="['fas', 'user-shield']" size="lg" :class="$style['icon']"/>
           <span>Rollen</span>
         </router-link>
         <!-- End New Link -->
      </div>

      <!-- User Profile Section -->
      <!-- Added id="userProfileContainer" for click outside detection -->
      <div :class="$style['user-profile']" @click="toggleDropdown" id="userProfileContainer">
        <img :src="authStore.getUserProfileInfo.profilePictureUrl" alt="User Avatar" :class="$style['user-avatar']"/>
        <div :class="$style['user-info']">
          <div :class="$style['user-name']">{{ authStore.getUserProfileInfo.firstname }} {{ authStore.getUserProfileInfo.lastname }}</div>
          <div :class="$style['user-role']">{{ authStore.getUserProfileInfo.roleName }}</div> <!-- Display user role -->
        </div>
        <FontAwesomeIcon :icon="['fas', 'chevron-down']" :class="[$style['dropdown-icon'], isDropdownOpen ? $style['dropdown-icon-open'] : '']" />

        <!-- Dropdown Menu -->
        <transition name="fade">
          <div v-if="isDropdownOpen" :class="$style['dropdown-menu']">
            <router-link to="/profile" :class="$style['dropdown-item']">
              <FontAwesomeIcon :icon="['fas', 'user']" :class="$style['dropdown-item-icon']" />
              Profil
            </router-link>
            <router-link to="/settings" :class="$style['dropdown-item']">
              <FontAwesomeIcon :icon="['fas', 'cog']" :class="$style['dropdown-item-icon']" />
              Einstellungen
            </router-link>
            <div :class="$style['dropdown-divider']"></div>
            <div @click.stop="logout();" :class="$style['dropdown-item']" > <!-- Added .stop to prevent immediate closing -->
              <FontAwesomeIcon :icon="['fas', 'sign-out-alt']" :class="$style['dropdown-item-icon']" />
              Logout
            </div>
          </div>
        </transition>
      </div>
      <!-- End User Profile Section -->
    </div>
    <div :class="$style['menu-stick']"></div>
  </div>
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

.menu-parent {
  display: flex;
  flex-direction: column;
  background-color: $bg-dark; // Consistent dark background
  flex-shrink: 0; // Prevent sidebar from shrinking

  .menu-container {
    display: flex;
    flex-direction: row;
    align-items: center; // Vertically center items
    margin: 0 30px; // Reduced margin
    padding: 15px 0; // Adjusted vertical padding

    .logo {
      width: 100px; // Smaller logo
      height: auto;
      margin-right: 25px; // Reduced margin
      flex-shrink: 0;
    }

    .workprogramms-container {
      display: flex;
      flex-direction: row;
      flex-grow: 1;
      justify-content: flex-start; // Align items to the start
      gap: 15px; // Space between items

      .workprogramms-entity {
        color: $text-color-light;
        text-decoration: none;
        display: flex; // Use flex for alignment
        flex-direction: column; // Stack icon and text
        align-items: center; // Center items horizontally
        padding: 8px 12px; // Adjusted padding
        border-radius: $border-radius; // Add border-radius
        transition: background-color $transition-speed ease, color $transition-speed ease;
        text-align: center;
        min-width: 80px; // Ensure minimum width for items

        &:hover {
          background-color: $bg-light; // Hover effect
          color: $text-color;

          .icon {
            color: $accent; // Change icon color on hover
          }
        }

        .icon {
          margin-bottom: 5px; // Space between icon and text
          color: $text-color-light; // Icon color
          transition: color $transition-speed ease;
          font-size: 1.1rem; // Slightly larger icon
        }

        span {
          font-size: 0.75rem; // Smaller text
          line-height: 1.2;
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

    .user-profile {
      display: flex;
      align-items: center;
      margin-left: auto; /* Push to the right */
      cursor: pointer; /* Indicate it's clickable */
      position: relative; /* For dropdown positioning */
      padding: 5px; // Add some padding for easier clicking
      border-radius: $border-radius;
      transition: background-color $transition-speed ease;
      flex-shrink: 0;

      &:hover {
          background-color: $bg-light;
      }

      .user-avatar {
        width: 35px; // Slightly smaller avatar
        height: 35px;
        border-radius: 50%;
        margin-right: 10px;
        object-fit: cover;
        border: 1px solid $accent; // Thinner border
      }

      .user-info {
        text-align: left;
        margin-right: 8px; // Space before icon

        .user-name {
          color: $text-color;
          font-weight: 500; // Normal weight
          font-size: 0.85rem;
          white-space: nowrap;
        }

        .user-role {
          color: $text-color-light;
          font-size: 0.7rem;
          white-space: nowrap;
        }
      }

      .dropdown-icon {
        color: $text-color-light;
        transition: transform $transition-speed ease;
        font-size: 0.8rem; // Smaller icon
      }
      .dropdown-icon-open { // Style for when dropdown is open
          transform: rotate(180deg);
      }


      .dropdown-menu {
        position: absolute;
        top: calc(100% + 5px); // Position below the profile with a small gap
        right: 0;
        background-color: $bg-medium;
        border: 1px solid $input-border; // Use input border color
        border-radius: $border-radius;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
        z-index: 1100; // Ensure dropdown is on top
        min-width: 180px;
        padding: 6px 0; // Vertical padding

        .dropdown-item {
          display: flex;
          align-items: center;
          padding: 9px 15px; // Adjusted padding
          color: $text-color;
          text-decoration: none;
          transition: background-color $transition-speed ease;
          font-size: 0.9rem; // Consistent font size

          &:hover {
            background-color: $bg-light;
          }

          .dropdown-item-icon {
            margin-right: 10px;
            color: $text-color-light; // Lighter icon color
            width: 18px; // Fixed width for icons
            text-align: center;
            font-size: 0.9em; // Icon size relative to text
          }
        }

        .dropdown-divider {
          height: 1px;
          background-color: $input-border; // Use input border color
          margin: 6px 0;
        }
      }
    }
  }

  .menu-stick {
    height: 1px; // Thinner stick
    margin: 0 25px 10px 25px; // Adjust margin
    border-radius: 20px;
    background-color: $input-border; // Use input border color
  }
}

// --- Transitions ---
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px); // Smaller vertical movement
}
</style>
