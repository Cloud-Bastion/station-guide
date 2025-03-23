<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

</script>

<template>
  <div :class="$style.parent">
    <div :class="$style['login-container']">
      <h1 :class="$style['login-header']">Login</h1>

      <div :class="$style['login-wrapper']">

        <div :class="$style['login-input-container']">
          <label for="email">E-Mail / Personalnummer</label>
          <input id="email" type="text" placeholder="Gib deine E-Mail ein" :class="$style['input']" />
        </div>

        <div :class="$style['login-input-container']">
          <label for="password">Passwort</label>
          <input id="password" type="password" placeholder="••••••••" :class="$style['input']" />
        </div>

        <div :class="$style['login-remember-container']">
          <input type="checkbox" id="remember-me" :class="$style['checkbox']"/>
          <label for="remember-me" :class="$style['remember-label']">Angemeldet bleiben</label>
        </div>

        <button :class="$style['login-submit-button']">Einloggen</button>
        <a href="#" :class="$style['login-forgot-password']">Passwort vergessen?</a>

        <div :class="$style['login-sidebar-container']">
          <div :class="$style['login-sidebar-line']"></div>
          <span :class="$style['login-sidebar-text']">Oder</span>
          <div :class="$style['login-sidebar-line']"></div>
        </div>

        <div :class="$style['login-third-party-container']">
          <button :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'apple']" size="lg" :class="$style['icon']"/>
            <span>Login mit Apple</span>
          </button>
          <button :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'google']" size="lg" :class="$style['icon']" />
            <span>Login mit Google</span>
          </button>
        </div>
      </div>
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
$input-focus: #3d0000ire; // Darker red focus

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
    transition: background-color $transition-speed ease, transform $transition-speed ease;

    &:hover {
      background: $accent-hover;
      transform: translateY(-2px);
    }

    &:active {
      background: darken($accent, 10%);
      transform: translateY(0);
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
