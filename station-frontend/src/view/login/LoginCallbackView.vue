<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import AuthUserService from '@/service/AuthUserService';

const router = useRouter();
const isLoading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    console.log("handling callback view")
    await AuthUserService.handleLoginCallback();
    await router.push({name: 'tasks'});
  } catch (err) {
    console.error("Callback view error:", err);
    error.value = err.message || 'An error occurred during login.';
  } finally {
    // authStore.isLoading = false;
    isLoading.value = false;
  }
});
</script>

<template>
  <div>
    <p v-if="isLoading">Processing login...</p>
    <p v-if="error" style="color: red;">Error during login: {{ error }}</p>
  </div>
</template>

<style scoped>

</style>