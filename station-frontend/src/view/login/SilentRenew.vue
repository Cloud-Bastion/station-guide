<script setup lang="ts">
  import AuthUserService from "@/service/AuthUserService";
  import {useRouter} from "vue-router";
  import {onMounted} from "vue";

  let { userManager } = useUserSession.value
  onMounted(async () => {
    console.log("signinSilentCallback")
    await AuthUserService.getUserManager().signinSilentCallback().then(() => {
      console.log("Success silent login")
    })

  })
</script>

<template>
</template>

<style scoped lang="scss">

</style>