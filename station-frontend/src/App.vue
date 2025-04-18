<script setup lang="ts">
import { useAuthStore } from './storage/AuthUserStore';
import {onMounted} from "vue";

const authStore = useAuthStore();

onMounted(() => {
  // Initialize authentication state when the app component mounts
  authStore.initializeAuth();
});
</script>

<template>
  <router-view/>
</template>

<style lang="scss" module>
html, body, #app {
  background-color: #0A0A0AFF;
  color: white;
  font-family: 'Apple Color Emoji', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
}

</style>
