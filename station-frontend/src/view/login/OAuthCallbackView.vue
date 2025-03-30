<template>
  <div :class="$style.container">
    <div v-if="loading" :class="$style.message">
      <FontAwesomeIcon icon="spinner" spin size="3x" />
      <p>Verarbeite Login...</p>
    </div>
    <div v-if="error" :class="[$style.message, $style.error]">
      <FontAwesomeIcon icon="exclamation-triangle" size="3x" />
      <p>Login fehlgeschlagen:</p>
      <p>{{ error }}</p>
      <router-link to="/login">Zurück zum Login</router-link>
    </div>
    <!-- Success state is handled by redirect -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AuthUserService from '@/service/AuthUserService';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  const code = route.query.code as string;
  const state = route.query.state as string; // Optional: Handle state for CSRF check
  const errorParam = route.query.error as string;
  const errorDescription = route.query.error_description as string;

  // Retrieve the code verifier stored before redirect
  const codeVerifier = sessionStorage.getItem('pkce_code_verifier');
  // --- REMOVED cleanup here, it's done in exchangeCodeForToken ---
  // sessionStorage.removeItem('pkce_code_verifier'); // Clean up verifier

  // Optional: Retrieve and validate state
  // const storedState = sessionStorage.getItem('oauth_state');
  // sessionStorage.removeItem('oauth_state');
  // if (!state || state !== storedState) {
  //   error.value = "Ungültiger Statusparameter (CSRF-Schutz).";
  //   loading.value = false;
  //   // --- ADDED cleanup here on error ---
  //   sessionStorage.removeItem('pkce_code_verifier');
  //   return;
  // }

  if (errorParam) {
    error.value = errorDescription || errorParam || "Unbekannter OAuth-Fehler.";
    loading.value = false;
    // --- ADDED cleanup here on error ---
    sessionStorage.removeItem('pkce_code_verifier');
    return;
  }

  if (!code) {
    error.value = "Kein Authorisierungscode empfangen.";
    loading.value = false;
    // --- ADDED cleanup here on error ---
    sessionStorage.removeItem('pkce_code_verifier');
    return;
  }

  if (!codeVerifier) {
    error.value = "Code Verifier nicht gefunden. Login-Vorgang möglicherweise unterbrochen.";
    loading.value = false;
    // No need to remove verifier here as it wasn't found
    return;
  }

  try {
    // Exchange the code for a token (this now handles storing tokens and removing verifier)
    const tokenResponse = await AuthUserService.exchangeCodeForToken(code, codeVerifier);

    if (tokenResponse.access_token) {
      // --- REMOVED redundant token storage ---
      // localStorage.setItem('auth_token', tokenResponse.access_token);
      // if (tokenResponse.refresh_token) {
      //   localStorage.setItem('refresh_token', tokenResponse.refresh_token);
      // }

      // Redirect to the intended page or a default dashboard
      // const intendedUrl = sessionStorage.getItem('intended_url') || '/employee/management';
      // sessionStorage.removeItem('intended_url');
      router.push({ name: 'employee-management' }); // Redirect to dashboard

    } else {
      // This case should ideally be handled within exchangeCodeForToken's error handling
      throw new Error("Kein Access Token in der Antwort erhalten.");
    }

  } catch (err: any) {
    console.error("Error exchanging code for token:", err);
    // Error message is now derived from the error thrown by exchangeCodeForToken
    error.value = err.message || "Fehler beim Austausch des Codes gegen ein Token.";
    loading.value = false;
    // Verifier cleanup is handled within exchangeCodeForToken's catch block
  }
});
</script>

<style module lang="scss">
$bg-dark: #121212;
$text-color: #f1f1f1;
$error-color: #e74c3c;
$accent: #ff4500;

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: $bg-dark;
  color: $text-color;
  text-align: center;
  font-family: 'Roboto', sans-serif;
}

.message {
  padding: 30px;
  p {
    margin-top: 15px;
    font-size: 1.2rem;
  }
}

.error {
  color: $error-color;
  p {
    font-size: 1rem;
  }
  a {
    color: $accent;
    text-decoration: underline;
    margin-top: 20px;
    display: inline-block;
    &:hover {
      color: lighten($accent, 10%);
    }
  }
}
</style>
