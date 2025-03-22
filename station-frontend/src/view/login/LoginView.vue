<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
</script>

<template>
  <div :class="$style['parent']">
    <h1 :class="$style['login-header']">Login</h1>

    <div :class="$style['login-container']">
      <div :class="$style['login-wrapper']">
        <div :class="$style['login-third-party-container']">
          <button :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'apple']" size="lg" />
            <span>Login mit Apple</span>
          </button>
          <button :class="$style['login-third-party-button']">
            <FontAwesomeIcon :icon="['fab', 'google']" size="lg" />
            <span>Login mit Google</span>
          </button>
        </div>

        <div :class="$style['login-sidebar-container']">
          <div :class="$style['login-sidebar-line']"></div>
          <span class="login-sidebar-text">Oder</span>
          <div :class="$style['login-sidebar-line']"></div>
        </div>

        <div :class="$style['login-input-container']">
          <label for="email">E-Mail / Personalnummer</label>
          <input id="email" type="text" placeholder="Gib deine E-Mail ein" />
        </div>

        <div :class="$style['login-input-container']">
          <label for="password">Passwort</label>
          <input id="password" type="password" placeholder="••••••••" />
        </div>

        <div :class="$style['login-remember-container']">
          <input type="checkbox" id="remember-me" />
          <label for="remember-me">Angemeldet bleiben</label>
        </div>

        <button :class="$style['login-submit-button']">Einloggen</button>
        <a href="#" :class="$style['login-forgot-password']">Passwort vergessen?</a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" module>$bg-dark: #181818;
$bg-medium: #222;
$bg-light: #2c2c2c;
$text-color: #f1f1f1;
$accent: #4a90e2;
$accent-hover: #5aa2ff;
$border-radius: 8px;
$transition-speed: 0.3s;

.parent {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: $bg-dark;
  height: 100vh;
  justify-content: center;

  .login-header {
    font-size: 2rem;
    font-weight: bold;
    color: $text-color;
    margin-bottom: 20px;
  }

  .login-container {
    display: flex;
    flex-direction: column;
    background: $bg-medium;
    border-radius: $border-radius;
    padding: 40px;
    width: 350px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
    animation: fadeIn 0.5s ease-in-out;

    .login-wrapper {
      display: flex;
      flex-direction: column;

      .login-third-party-container {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;

        .login-third-party-logo {
          cursor: pointer;
          font-size: 2rem;
          color: $text-color;
          transition: transform $transition-speed ease, color $transition-speed ease;

          &:hover {
            transform: scale(1.1);
            color: $accent;
          }
        }
      }

      .login-sidebar-container {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        margin: 10px 0 20px;

        .login-sidebar-entry {
          flex: 1;
          height: 1px;
          background-color: #444;
        }

        .login-sidebar-text {
          font-size: 0.9rem;
          color: #bbb;
        }
      }

      .login-email-container,
      .login-password-container {
        display: flex;
        flex-direction: column;
        margin-bottom: 15px;

        .login-email-header,
        .login-password-header {
          font-size: 1rem;
          color: $text-color;
          margin-bottom: 5px;
        }

        input {
          padding: 12px;
          border: 1px solid #444;
          border-radius: $border-radius;
          background-color: $bg-light;
          color: $text-color;
          transition: border-color $transition-speed ease, background-color $transition-speed ease;

          &:focus {
            border-color: $accent;
            outline: none;
            background-color: #333;
          }
        }
      }

      .login-remember-container {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;

        .login-remember-button {
          width: 18px;
          height: 18px;
          accent-color: $accent;
          cursor: pointer;
        }

        .login-remember-text {
          font-size: 0.9rem;
          color: $text-color;
        }
      }

      .login-submit-button {
        padding: 12px;
        text-align: center;
        border: none;
        border-radius: $border-radius;
        background: $accent;
        color: $text-color;
        font-size: 1rem;
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
        font-size: 0.9rem;
        color: #bbb;
        margin-top: 10px;
        cursor: pointer;
        transition: color $transition-speed ease;

        &:hover {
          color: $text-color;
        }
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

</style>