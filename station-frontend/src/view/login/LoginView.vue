<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import AuthUserService from "@/service/AuthUserService";
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // Import useRouter for redirection

// --- OIDC related refs removed ---
// import { User } from 'oidc-client-ts';
// const user = ref<User | null>(null);

// --- Refs for ROPC Login ---
const email = ref('');
const password = ref('');
const loginError = ref<string | null>(null);
const isLoading = ref(false);
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

// Function to handle ROPC login submission
const submitLogin = async () => {
  loginError.value = null; // Reset error
  isLoading.value = true;
  try {
    await AuthUserService.loginWithPassword(email.value, password.value);
    isAuthenticated.value = true; // Update auth state
    console.log("Login successful, redirecting...");
    // Redirect to a protected route after successful login
    router.push({ name: 'employee-management' }); // Or your default authenticated route
  } catch (error: any) {
    console.error("Login failed:", error);
    loginError.value = error.message || "Login fehlgeschlagen. Bitte versuchen Sie es erneut.";
    isAuthenticated.value = false; // Ensure auth state is false on error
  } finally {
    isLoading.value = false;
  }
};

// Function to handle logout
const handleLogout = () => {
  AuthUserService.logout();
  isAuthenticated.value = false; // Update auth state
  email.value = ''; // Clear fields
  password.value = '';
  loginError.value = null;
  // Redirect to login page after logout
  router.push({ name: 'login' });
};

// --- OIDC login/logout handlers removed ---
// const handleLogin = async () => { ... };
// const handleOidcLogout = async () => { ... };

onMounted(() => {
  checkAuthStatus();
});

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Station Portal</h1>

      <!-- ROPC Login Form -->
      <form v-if="!isAuthenticated" @submit.prevent="submitLogin" :class="$style['login-wrapper']">
        <p :class="$style['login-prompt']">Bitte melden Sie sich an.</p>

        <!-- Email Input -->
        <div :class="$style['login-input-container']">
          <label for="email">E-Mail</label>
          <input
            type="email"
            id="email"
            v-model="email"
            required
            :class="$style.input"
            placeholder="Ihre E-Mail-Adresse"
            :disabled="isLoading"
          />
        </div>

        <!-- Password Input -->
        <div :class="$style['login-input-container']">
          <label for="password">Passwort</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            :class="$style.input"
            placeholder="Ihr Passwort"
            :disabled="isLoading"
          />
        </div>

        <!-- Display Login Errors -->
        <div v-if="loginError" :class="$style['error-message']">
          {{ loginError }}
        </div>

        <!-- Login Button -->
        <button type="submit" :class="$style['login-submit-button']" :disabled="isLoading">
          <FontAwesomeIcon v-if="!isLoading" icon="sign-in-alt" :class="$style['icon']" />
          <FontAwesomeIcon v-if="isLoading" icon="spinner" spin :class="$style['icon']" />
          <span>{{ isLoading ? 'Anmelden...' : 'Login' }}</span>
        </button>

        <!-- Optional: Forgot Password Link -->
        <!-- <div :class="$style['login-forgot-password']">Passwort vergessen?</div> -->

      </form>

      <!-- Authenticated State -->
      <div v-if="isAuthenticated" :class="$style['login-wrapper']">
         <p :class="$style['welcome-message']">
           <!-- Cannot display name easily with ROPC, needs separate API call -->
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
// SCSS Variables and Styles (mostly unchanged, added input styles back)
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

  .login-prompt, .welcome-message {
      color: $text-color;
      font-size: 1.1rem;
      margin-bottom: 25px;
  }

  .error-message {
      color: $error-color;
      background-color: rgba($error-color, 0.1);
      border: 1px solid rgba($error-color, 0.3);
      padding: 10px;
      border-radius: $border-radius;
      margin-bottom: 15px;
      font-size: 0.9rem;
      text-align: center;
      width: 100%;
  }

  // --- Styles for ROPC form elements ---
  .login-input-container {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px; // More space
    width: 100%; // Take full width

    label {
      font-size: 1rem;
      color: $text-color;
      margin-bottom: 8px; // More space
      text-align: left;
    }

    .input {
      padding: 14px; // Larger padding
      border: 1px solid $input-border;
      border-radius: $border-radius;
      background-color: $input-bg;
      color: $text-color;
      transition: border-color $transition-speed ease, background-color $transition-speed ease;

      &:focus {
        border-color: $input-focus;
        outline: none;
        background-color: #444; // Slightly lighter on focus
      }
       &:disabled {
            background-color: darken($input-bg, 5%);
            cursor: not-allowed;
            opacity: 0.7;
        }
    }
  }

  .login-forgot-password {
    text-align: center;
    font-size: 1rem; // Slightly larger
    color: #bbb;
    margin-top: 15px; // More space
    cursor: pointer;
    transition: color $transition-speed ease;
    width: 100%; // Take full width for centering

    &:hover {
      color: $text-color;
      text-decoration: underline; // Add underline on hover
    }
  }
  // --- End styles for ROPC form elements ---


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
    margin-top: 10px;
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
