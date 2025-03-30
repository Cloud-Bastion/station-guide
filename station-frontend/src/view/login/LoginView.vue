<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import AuthUserService from "@/service/AuthUserService";
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // Import useRouter for redirection

const isLoading = ref(false); // Used for the login button state
const isAuthenticated = ref(false); // Still useful to control UI elements
const router = useRouter(); // Initialize router

// Check authentication status when the component mounts
const checkAuthStatus = () => {
  isAuthenticated.value = AuthUserService.isAuthenticated();
  // If already authenticated, redirect away from login page
  if (isAuthenticated.value) {
      console.log("Already authenticated, redirecting to dashboard.");
      router.push({ name: 'employee-management' }); // Or your default authenticated route
  }
};

// Function to handle initiating the Authorization Code login flow
const handleLogin = async () => {
  isLoading.value = true; // Show loading state on button
  try {
    // This function now redirects the browser, so no further action needed here
    await AuthUserService.initiateLogin();
    // isLoading will remain true until the browser redirects
  } catch (error) {
    console.error("Failed to initiate login redirect:", error);
    // Handle error (e.g., show a message to the user)
    // You might want a general error display area
    alert("Login konnte nicht gestartet werden. Bitte versuchen Sie es erneut.");
    isLoading.value = false; // Reset loading state on error
  }
};

// Function to handle logout
const handleLogout = () => {
  AuthUserService.logout();
  isAuthenticated.value = false; // Update auth state
  // Redirect to login page after logout (or stay on page if preferred)
  router.push({ name: 'login' });
};

onMounted(() => {
  checkAuthStatus();
});

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Station Portal</h1>

      <!-- Login Button (Authorization Code Flow) -->
      <div v-if="!isAuthenticated" :class="$style['login-wrapper']">
        <p :class="$style['login-prompt']">Bitte melden Sie sich an.</p>

        <!-- Login Button -->
        <button @click="handleLogin" :class="$style['login-submit-button']" :disabled="isLoading">
          <FontAwesomeIcon v-if="!isLoading" icon="sign-in-alt" :class="$style['icon']" />
          <FontAwesomeIcon v-if="isLoading" icon="spinner" spin :class="$style['icon']" />
          <span>{{ isLoading ? 'Weiterleitung...' : 'Login' }}</span>
        </button>

        <!-- Optional: Add buttons for other providers like Google/GitHub here -->
        <!--
        <button @click="handleGoogleLogin" :class="$style['provider-button']">
          <FontAwesomeIcon :icon="['fab', 'google']" :class="$style['icon']" />
          <span>Login mit Google</span>
        </button>
        -->
      </div>

      <!-- Authenticated State -->
      <div v-if="isAuthenticated" :class="$style['login-wrapper']">
         <p :class="$style['welcome-message']">
           <!-- TODO: Fetch user info if needed via separate API call -->
           Willkommen! Sie sind angemeldet.
         </p>
         <button @click="handleLogout" :class="[$style['login-submit-button'], $style['logout-button']]">
           <FontAwesomeIcon icon="sign-out-alt" :class="$style['icon']" />
           <span>Logout</span>
         </button>
      </div>

    </div>
  </div>
</template>

<style lang="scss" module>
// SCSS Variables and Styles (mostly unchanged, removed ROPC form styles)
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #4f0000; // Darker red
$accent-hover: #3d0000; // Even darker red for hover
$logout-color: #6c757d; // A greyish color for logout button perhaps
$logout-hover: #5a6268;
$border-radius: 10px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;
$input-focus: #3d0000; // Darker red focus
$error-color: #e74c3c; // Error color

.parent {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: $bg-dark;
  height: 100vh;
  justify-content: center;
  font-family: 'Roboto', sans-serif;
}

.login-container {
  background: $bg-medium;
  border-radius: $border-radius;
  padding: 40px;
  width: 380px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  animation: fadeIn 0.5s ease-in-out;
  text-align: center;
}

.login-header {
  font-size: 2.5rem;
  font-weight: bold;
  color: $text-color;
  margin-bottom: 30px;
  text-align: center;
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px; // Add gap between elements

  .login-prompt, .welcome-message {
      color: $text-color;
      font-size: 1.1rem;
      margin-bottom: 5px; // Reduced margin as gap is used now
  }

  // Removed ROPC form styles (input container, label, input, forgot password)

  .login-submit-button {
    padding: 14px;
    text-align: center;
    border: none;
    border-radius: $border-radius;
    background: $accent;
    color: $text-color;
    font-size: 1.1rem;
    cursor: pointer;
    transition: background-color $transition-speed ease, transform $transition-speed ease, opacity $transition-speed ease;
    // margin-top: 10px; // Removed margin-top, using gap now
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    min-width: 150px;
    width: 100%; // Make button full width

    &:hover:not(:disabled) {
      background: $accent-hover;
      transform: translateY(-2px);
    }

    &:active:not(:disabled) {
      background: darken($accent, 10%);
      transform: translateY(0);
    }

    &:disabled {
        background-color: darken($accent, 15%);
        cursor: not-allowed;
        opacity: 0.7;
    }

    .icon {
        // Icon styles if needed, FontAwesome handles size etc.
    }

    // Specific style for logout button
    &.logout-button {
        background: $logout-color;
        &:hover:not(:disabled) {
            background: $logout-hover;
        }
         &:active:not(:disabled) {
            background: darken($logout-color, 10%);
        }
    }
  }

  // Optional styles for provider buttons
  .provider-button {
      // Similar styles to login-submit-button but maybe different colors
      padding: 14px;
      border: 1px solid $input-border; // Example border
      border-radius: $border-radius;
      background: $bg-light; // Different background
      color: $text-color;
      font-size: 1rem;
      cursor: pointer;
      transition: background-color $transition-speed ease, transform $transition-speed ease;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      width: 100%;

      &:hover {
          background: darken($bg-light, 5%);
          border-color: $input-focus;
      }
  }

}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

// Media query for smaller screens
@media (max-width: 768px) {
  .login-container {
    width: 90%;
    padding: 20px;
  }
}
</style>
