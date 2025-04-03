<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import AuthUserService from "@/service/AuthUserService";
import OAuthUserService, {useUserSession} from "@/service/OAuthUserService";
import {ref, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router'; // Import useRouter for redirection

// const isLoading = ref(false);
const isAuthenticated = ref(false);
const router = useRouter();

// --- Add refs for email and password ---
const username = ref(''); // Use 'username' as it maps to the ROPC parameter
const password = ref('');
const loginError = ref<string | null>(null); // To display login errors

const isLoading = ref(false)
// const router = useRouter()
const route = useRoute()
const userSession = useUserSession

const redirect = route.query.redirect as string

// Check authentication status when the component mounts
const checkAuthStatus = () => {
  isAuthenticated.value = AuthUserService.isAuthenticated();
  if (isAuthenticated.value) {
    router.push({name: 'tasks'});
  }
};
const handleLogin = async () => {
  userSession.value
      .logInEmailPw(username.value, password.value)
      .then(() => {
        console.log("Authenticated. Verifying...")
        userSession.value.verify().then(() => {
          console.log("redirecting after login")
          router.push({
            name: 'tasks',
          })
        })
      })
      .catch((err) => {
        console.log(err)
        // notif.error(err.response.data.message)
      })
};

// Function to handle logout
const handleLogout = () => {
  AuthUserService.logout();
  isAuthenticated.value = false; // Update auth state
  username.value = ''; // Clear fields on logout
  password.value = '';
  loginError.value = null;
  // Stay on login page after logout
  // router.push({ name: 'login' }); // No need to push if already there
};

onMounted(() => {
  checkAuthStatus();
});

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Station Portal</h1>

      <!-- ROPC Login Form -->
      <div v-if="!isAuthenticated" :class="$style['login-wrapper']">
        <p :class="$style['login-prompt']">Bitte melden Sie sich an.</p>

        <!-- Email Input -->
        <div :class="$style['input-container']">
          <label for="username" :class="$style['input-label']">E-Mail</label>
          <input
              type="email"
              id="username"
              v-model="username"
              :class="$style['input-field']"
              placeholder="Ihre E-Mail"
              required
              @keyup.enter="handleLogin"
          />
        </div>

        <!-- Password Input -->
        <div :class="$style['input-container']">
          <label for="password" :class="$style['input-label']">Passwort</label>
          <input
              type="password"
              id="password"
              v-model="password"
              :class="$style['input-field']"
              placeholder="Ihr Passwort"
              required
          />
          <!-- Optional: Forgot Password Link -->
          <!-- <a href="#" :class="$style['forgot-password']">Passwort vergessen?</a> -->
        </div>

        <!-- Error Message Display -->
        <div v-if="loginError" :class="$style['error-message']">
          {{ loginError }}
        </div>

        <!-- Login Button -->
        <button @click="handleLogin" :class="$style['login-submit-button']" :disabled="isLoading">
          <FontAwesomeIcon v-if="!isLoading" icon="sign-in-alt" :class="$style['icon']"/>
          <FontAwesomeIcon v-if="isLoading" icon="spinner" spin :class="$style['icon']"/>
          <span>{{ isLoading ? 'Anmelden...' : 'Login' }}</span>
        </button>

        <button :class="$style['provider-button']">
          <FontAwesomeIcon :icon="['fab', 'google']" :class="$style['icon']"/>
          <span>Login mit Google</span>
        </button>
        <button :class="$style['provider-button']">
          <FontAwesomeIcon :icon="['fab', 'apple']" :class="$style['icon']"/>
          <span>Login mit Apple</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" module>
// SCSS Variables and Styles
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0; // Lighter text for labels/placeholders
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
    margin-bottom: 5px;
  }

  // --- Styles for ROPC form ---
  .input-container {
    width: 100%;
    text-align: left; // Align labels left

    .input-label {
      display: block;
      margin-bottom: 5px;
      color: $text-color-light;
      font-size: 0.9rem;
    }

    .input-field {
      width: 100%;
      padding: 12px 15px; // Comfortable padding
      border: 1px solid $input-border;
      border-radius: $border-radius;
      background-color: $input-bg;
      color: $text-color;
      box-sizing: border-box; // Include padding and border in width
      font-size: 1rem;
      transition: border-color $transition-speed ease, box-shadow $transition-speed ease;

      &:focus {
        border-color: $input-focus;
        outline: none;
        box-shadow: 0 0 0 2px rgba($input-focus, 0.3);
      }

      // Style placeholder text
      &::placeholder {
        color: #888;
      }
    }

    .forgot-password {
      display: block;
      margin-top: 5px;
      text-align: right;
      font-size: 0.8rem;
      color: $text-color-light;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
        color: $text-color;
      }
    }
  }

  .error-message {
    color: $error-color;
    font-size: 0.9rem;
    margin-top: -10px; // Reduce gap before error
    margin-bottom: 5px; // Add gap after error
    width: 100%;
    text-align: center;
  }

  // --- End ROPC form styles ---

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
    padding: 14px;
    border: 1px solid $input-border;
    border-radius: $border-radius;
    background: $bg-light;
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
