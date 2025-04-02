<script setup lang="ts">
  // http://localhost:5173/oauth2/callback/local?code=CODE_HERE

  import {onMounted} from "vue";

  onMounted(async () => {
    const code = route.query.code as string;
    console.log("Code: " + code)
  })

</script>

<template>
  <h1>Code: {{code}}</h1>
</template>

<style scoped lang="scss">

</style>