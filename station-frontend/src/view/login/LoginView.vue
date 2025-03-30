<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import AuthUserService from "@/service/AuthUserService";
import { ref, onMounted } from 'vue';
import { User } from 'oidc-client-ts';
// Removed useRouter as redirection is handled by guards and service now
// Removed PKCE utils as oidc-client-ts handles it

const isAuthenticated = ref(false);
const user = ref<User | null>(null);
const loginError = ref<string | null>(null); // Can be used for general login initiation errors

// Check authentication status when the component mounts
const checkAuthStatus = async () => {
  try {
    const currentUser = await AuthUserService.getUser();
    isAuthenticated.value = !!currentUser && !currentUser.expired;
    user.value = currentUser;
  } catch (error) {
    console.error("Error checking auth status:", error);
    isAuthenticated.value = false;
    user.value = null;
  }
};

// Function to initiate the OIDC login flow
const handleLogin = async () => {
  loginError.value = null; // Reset error
  try {
    // Redirects the user to the auth server's login page
    await AuthUserService.login();
    // The browser will navigate away, so code here might not execute immediately after.
  } catch (error) {
    console.error("Login initiation failed:", error);
    loginError.value = "Login konnte nicht gestartet werden. Bitte versuchen Sie es erneut.";
  }
};

// Function to initiate the OIDC logout flow
const handleLogout = async () => {
  try {
    await AuthUserService.logout();
    // The browser will navigate away to the auth server and then back to the post_logout_redirect_uri.
  } catch (error) {
    console.error("Logout failed:", error);
    // Handle logout error display if needed
  }
};

// --- Potentially keep Google Login if needed, but it bypasses your Auth Server ---
// const googleLogin = async () => {
//   // This flow bypasses your auth-server. Consider integrating it via the auth-server itself.
//   const GOOGLE_CLIENT_ID = encodeURIComponent("YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com") // Replace with actual ID
//   const GOOGLE_REDIRECT_URI = encodeURIComponent("http://localhost:5173/google-callback") // Needs a handler route
//   window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=${GOOGLE_REDIRECT_URI}&response_type=code&client_id=${GOOGLE_CLIENT_ID}&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&access_type=offline`
// };
// --- End Google Login ---


onMounted(() => {
  checkAuthStatus();
});

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Station Portal</h1>

      <div v-if="!isAuthenticated" :class="$style['login-wrapper']">
        <p :class="$style['login-prompt']">Bitte melden Sie sich an.</p>

        <!-- Display general login errors -->
        <div v-if="loginError" :class="$style['error-message']">
          {{ loginError }}
        </div>

        <button @click="handleLogin" :class="$style['login-submit-button']">
          <FontAwesomeIcon icon="sign-in-alt" :class="$style['icon']" />
          <span>Login</span>
        </button>

        <!-- Optional: Separator and Third-Party Logins (if keeping Google/Apple) -->
        <!--
        <div :class="$style['login-sidebar-container']">
          <div :class="$style['login-sidebar-line']"></div>
          <span :class="$style['login-sidebar-text']">Oder</span>
          <div :class="$style['login-sidebar-line']"></div>
        </div>

        <div :class="$style['login-third-party-container']">
          <button type="button" :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'apple']" size="lg" :class="$style['icon']"/>
            <span>Login mit Apple</span>
          </button>
          <button type="button" :class="$style['login-third-party-button']" @click="googleLogin()">
            <FontAwesomeIcon :icon="['fab', 'google']" size="lg" :class="$style['icon']" />
            <span>Login mit Google</span>
          </button>
        </div>
        -->
      </div>

      <div v-if="isAuthenticated" :class="$style['login-wrapper']">
         <p :class="$style['welcome-message']">
           Willkommen, {{ user?.profile?.name || user?.profile?.preferred_username || 'Benutzer' }}!
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
// SCSS Variables and Styles from the original file
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
  font-family: 'Roboto', sans-serif; // More professional font
}

.login-container {
  background: $bg-medium;
  border-radius: $border-radius;
  padding: 40px;
  width: 380px; // Slightly wider
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5); // Stronger shadow
  animation: fadeIn 0.5s ease-in-out;
  text-align: center; // Center content like buttons
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
  align-items: center; // Center items horizontally

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
      width: 100%; // Take full width within the wrapper
  }

  // --- Styles for optional third-party buttons ---
  .login-third-party-container {
    display: flex;
    justify-content: space-between;
    margin-bottom: 25px; // More space
    width: 100%; // Take full width

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
    width: 100%;

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
  // --- End styles for optional third-party buttons ---


  // --- Styles for removed elements (kept for reference or potential reuse) ---
  /*
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
    }
  }

  .login-remember-container {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 25px; // More space
    text-align: left;
    width: 100%;

    .checkbox {
      width: 18px;
      height: 18px;
      accent-color: $accent;
      cursor: pointer;
      border-radius: 6px;
    }

    .remember-label {
      font-size: 1rem; // Slightly larger
      color: $text-color;
      user-select: none; // Prevent text selection
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
  */
  // --- End styles for removed elements ---


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
    display: inline-flex; // Use inline-flex for button sizing
    align-items: center;
    justify-content: center;
    gap: 8px; // Space between icon and text
    min-width: 150px; // Give button a minimum width

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
    width: 90%; // Take up more width on smaller screens
    padding: 20px;
  }
  // Adjust third-party container if used
  /*
  .login-third-party-container {
    flex-direction: column;

    .login-third-party-button {
      width: 100%;
      margin-bottom: 10px;
    }
  }
  */
}
</style>
