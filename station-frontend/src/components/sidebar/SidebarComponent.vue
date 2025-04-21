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

// Placeholder data for the user profile.  Replace this with actual user data.
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
  if (isDropdownOpen.value && event.target instanceof Node && !$refs.userProfile.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};

const logout = () => {
  AuthUserService.logout()
};

onMounted(() => {
  window.addEventListener('click', closeDropdown);
});

onMounted(() => {
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
          <span>Mitarbeiter-Management</span>
        </router-link>
        <router-link v-if="useAuthStore().hasPermission('expire_product:read')" to="/expire/management"
                     :class="[$style['workprogramms-entity'], props.site === 'product-expire-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'lemon']" size="lg" :class="$style['icon']"/>
          <span>MHD-Tool</span>
        </router-link>
        <router-link to="/tasks"
                     :class="[$style['workprogramms-entity'], props.site === 'task-management' ? $style['workprogramms-entity-active'] : '']">
          <FontAwesomeIcon :icon="['far', 'fa-rectangle-list']" size="lg" :class="$style['icon']"/>
          <span>Task-Manager</span>
        </router-link>
      </div>

      <!-- User Profile Section -->
      <div :class="$style['user-profile']" @click="toggleDropdown" ref="userProfile">
        <img :src="useAuthStore().getUserProfileInfo.profilePictureUrl" alt="User Avatar" :class="$style['user-avatar']"/>
        <div :class="$style['user-info']">
          <div :class="$style['user-name']">{{ useAuthStore().getUserProfileInfo.firstname + useAuthStore().getUserProfileInfo.lastname }}</div>
          <div :class="$style['user-role']">{{ useAuthStore().getUserProfileInfo.roleName }}</div> <!-- Display user role -->
        </div>
        <FontAwesomeIcon :icon="['fas', 'chevron-down']" :class="$style['dropdown-icon']" />

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
            <div @click="logout();" :class="$style['dropdown-item']" >
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

        span {
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

    .user-profile {
      display: flex;
      align-items: center;
      margin-left: auto; /* Push to the right */
      cursor: pointer; /* Indicate it's clickable */
      position: relative; /* For dropdown positioning */

      .user-avatar {
        width: 40px; /* Adjust size as needed */
        height: 40px; /* Adjust size as needed */
        border-radius: 50%; /* Make it circular */
        margin-right: 10px; /* Space between avatar and text */
        object-fit: cover; /* Ensure the image covers the area */
        border: 2px solid $accent; /* Add a border */
      }

      .user-info {
        text-align: left; /* Align text to the left */

        .user-name {
          color: $text-color;
          font-weight: bold;
          font-size: 0.9rem; /* Adjust size as needed */
        }

        .user-role {
          color: #aaa; /* Lighter color for the role */
          font-size: 0.7rem; /* Adjust size as needed */
        }
      }

      .dropdown-icon {
        color: #aaa;
        margin-left: 5px;
        transition: transform $transition-speed ease;
      }

      &:hover .dropdown-icon {
        transform: rotate(180deg); /* Rotate on hover */
      }

      .dropdown-menu {
        position: absolute;
        top: 100%; /* Position below the profile */
        right: 0; /* Align to the right */
        background-color: $bg-medium;
        border: 1px solid #444;
        border-radius: $border-radius;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
        z-index: 100; /* Ensure it's above other content */
        min-width: 160px; /* Minimum width */
        padding: 5px 0; /* Vertical padding */

        .dropdown-item {
          display: flex;
          align-items: center;
          padding: 8px 15px;
          color: $text-color;
          text-decoration: none;
          transition: background-color $transition-speed ease;

          &:hover {
            background-color: $bg-light;
          }

          .dropdown-item-icon {
            margin-right: 10px;
            color: #aaa;
            width: 20px; /* Fixed width for icons */
            text-align: center; /* Center the icons */
          }
        }

        .dropdown-divider {
          height: 1px;
          background-color: #444;
          margin: 5px 0;
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

// --- Transitions ---
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px); /* Slight vertical movement */
}
</style>
