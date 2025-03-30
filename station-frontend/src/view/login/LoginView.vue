<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import Ref from "@/components/util/Ref";
import AuthUserService from "@/service/AuthUserService";
// Removed PKCE imports: import { generateRandomString, generateCodeChallenge } from '@/utils/pkce';
import { ref } from 'vue';
import { useRouter } from 'vue-router'; // Import useRouter for redirection

const email = ref(""); // Use standard ref for v-model
const password = ref(""); // Use standard ref for v-model
const loginError = ref<string | null>(null); // For displaying errors
const isLoading = ref(false); // Loading indicator
const router = useRouter(); // Get router instance

// OAuth2 Configuration (Client ID and Scopes are still needed for ROPC)
const CLIENT_ID = "station-frontend-client"; // Must match RegisteredClient in auth-server
const SCOPES = "openid profile station.read station.write"; // Requested scopes

const submitLogin = async () => {
  loginError.value = null; // Clear previous errors
  isLoading.value = true; // Show loading indicator

  if (!email.value || !password.value) {
      loginError.value = "Bitte geben Sie E-Mail und Passwort ein.";
      isLoading.value = false;
      return;
  }

  try {
    // Call the ROPC login function from the service
    const tokenResponse = await AuthUserService.loginWithPassword(
        email.value,
        password.value,
        CLIENT_ID,
        SCOPES
    );

    if (tokenResponse.access_token) {
      // Store the token
      localStorage.setItem('auth_token', tokenResponse.access_token);
      if (tokenResponse.refresh_token) {
        localStorage.setItem('refresh_token', tokenResponse.refresh_token);
      }

      // Redirect to the dashboard or intended page
      router.push({ name: 'employee-management' });

    } else {
      // Should not happen if service throws error, but as a fallback
      throw new Error("Kein Access Token erhalten.");
    }

  } catch (error: any) {
    console.error("Login failed:", error);
    // Display specific error from service or a generic one
    loginError.value = error.message || "Login fehlgeschlagen. Überprüfen Sie Ihre Anmeldedaten.";
  } finally {
    isLoading.value = false; // Hide loading indicator
  }
};

// Keep Google login for now, but ideally it should also go through the auth-server
const googleLogin = async () => {
  // This flow bypasses your auth-server. Consider integrating it.
  const GOOGLE_CLIENT_ID = encodeURIComponent("158421481211-6vp5a7oq3lbd60s16f43r3fs3ic66s7r.apps.googleusercontent.com")
  // Redirect URI for Google needs to be handled separately or integrated into auth server flow
  const GOOGLE_REDIRECT_URI = encodeURIComponent("http://localhost:5173/google-callback")
  window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=${GOOGLE_REDIRECT_URI}&response_type=code&client_id=${GOOGLE_CLIENT_ID}&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&access_type=offline`
};

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Login</h1>

      <form @submit.prevent="submitLogin" :class="$style['login-wrapper']">

        <!-- Email and Password fields are now active -->
        <div :class="$style['login-input-container']">
          <label for="email">E-Mail / Personalnummer</label>
          <input v-model="email" type="text" id="email" placeholder="Ihre E-Mail" :class="$style['input']" required/>
        </div>

        <div :class="$style['login-input-container']">
          <label for="password">Passwort</label>
          <input v-model="password" type="password" id="password" placeholder="••••••••" :class="$style['input']" required/>
        </div>

        <!-- Display login errors -->
        <div v-if="loginError" :class="$style['error-message']">
          {{ loginError }}
        </div>

        <!-- Removed remember me as session is managed by auth-server/token -->

        <button type="submit" :class="$style['login-submit-button']" :disabled="isLoading">
          <FontAwesomeIcon v-if="isLoading" icon="spinner" spin :class="$style['loading-spinner']"/>
          <span v-else>Einloggen</span>
        </button>
        <a href="#" :class="$style['login-forgot-password']">Passwort vergessen?</a> <!-- Link should point to auth-server's password reset -->

        <div :class="$style['login-sidebar-container']">
          <div :class="$style['login-sidebar-line']"></div>
          <span :class="$style['login-sidebar-text']">Oder</span>
          <div :class="$style['login-sidebar-line']"></div>
        </div>

        <div :class="$style['login-third-party-container']">
          <!-- Keep these buttons but they should ideally trigger the auth-server flow too -->
          <button type="button" :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'apple']" size="lg" :class="$style['icon']"/>
            <span>Login mit Apple</span>
          </button>
          <button type="button" :class="$style['login-third-party-button']" @click="googleLogin()">
            <FontAwesomeIcon :icon="['fab', 'google']" size="lg" :class="$style['icon']" />
            <span>Login mit Google</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #4f0000; // Darker red
$accent-hover: #3d0000; // Even darker red for hover
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
  font-family: 'Roboto', sans-serif; // More professional font
}

.login-container {
  background: $bg-medium;
  border-radius: $border-radius;
  padding: 40px;
  width: 380px; // Slightly wider
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5); // Stronger shadow
  animation: fadeIn 0.5s ease-in-out;
}

.login-header {
  font-size: 2.5rem; // Larger font size
  font-weight: bold;
  color: $text-color;
  margin-bottom: 30px; // More space
  text-align: center;
}

.login-wrapper {
  display: flex;
  flex-direction: column;

  .error-message {
      color: $error-color;
      background-color: rgba($error-color, 0.1);
      border: 1px solid rgba($error-color, 0.3);
      padding: 10px;
      border-radius: $border-radius;
      margin-bottom: 15px;
      font-size: 0.9rem;
      text-align: center;
  }


  .login-third-party-container {
    display: flex;
    justify-content: space-between;
    margin-bottom: 25px; // More space

    .login-third-party-button {
      display: flex;
      align-items: center;
      justify-content: center; // Center the content
      padding: 12px 20px;
      background-color: transparent;
      color: $text-color;
      border: 1px solid $input-border;
      border-radius: $border-radius;
      cursor: pointer;
      transition: background-color $transition-speed ease, border-color $transition-speed ease, color $transition-speed;
      width: 48%; // Make buttons take up equal space

      &:hover {
        background-color: $input-bg;
        border-color: $accent;
        color: $accent;
      }

      .icon {
        margin-right: 8px; // Space between icon and text
        color: inherit;
      }

      span {
        color: inherit;
      }
    }
  }

  .login-sidebar-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 15px; // More space
    margin: 20px 0 25px;

    .login-sidebar-line {
      flex: 1;
      height: 1px;
      background-color: #444;
    }

    .login-sidebar-text {
      font-size: 1rem; // Slightly larger
      color: #999; // Darker gray
    }
  }

  .login-input-container {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px; // More space

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
       // Removed disabled style as inputs are active now
    }
  }

  .login-remember-container {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 25px; // More space
    text-align: left;

    .checkbox {
      width: 18px;
      height: 18px;
      accent-color: $accent;
      cursor: pointer;
      border-radius: 6px; /* Added border-radius */
    }

    .remember-label {
      font-size: 1rem; // Slightly larger
      color: $text-color;
      user-select: none; // Prevent text selection
    }
  }

  .login-submit-button {
    padding: 14px; // Larger padding
    text-align: center;
    border: none;
    border-radius: $border-radius;
    background: $accent;
    color: $text-color;
    font-size: 1.1rem; // Larger font size
    cursor: pointer;
    transition: background-color $transition-speed ease, transform $transition-speed ease, opacity $transition-speed ease;
    margin-top: 10px; // Add some margin above the button
    display: flex; // For spinner alignment
    align-items: center;
    justify-content: center;
    gap: 8px; // Space between spinner and text

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

    .loading-spinner {
        // Spinner styles are handled by FontAwesome component props
    }
  }

  .login-forgot-password {
    text-align: center;
    font-size: 1rem; // Slightly larger
    color: #bbb;
    margin-top: 15px; // More space
    cursor: pointer;
    transition: color $transition-speed ease;

    &:hover {
      color: $text-color;
      text-decoration: underline; // Add underline on hover
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
    width: 90%; // Take up more width on smaller screens
    padding: 20px;
  }
  .login-third-party-container {
    flex-direction: column;

    .login-third-party-button {
      width: 100%;
      margin-bottom: 10px;
    }
  }
}
</style>
