<script setup lang="ts">
  import {useUserSession} from "@/service/OAuthUserService";
  import {useRouter} from "vue-router";
  import {onMounted} from "vue";

  let { userManager } = useUserSession.value
  onMounted(async () => {
    console.log("signinSilentCallback")
    await userManager.signinSilentCallback().then(() => {
      console.log("Success silent login")
    })

  })
</script>

<template>
</template>

<style scoped lang="scss">

</style>