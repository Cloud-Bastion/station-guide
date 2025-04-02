<script setup lang="ts">
  import {onMounted} from "vue";
  import {useUserSession} from "@/service/OAuthUserService";
  import {useRouter} from "vue-router";

  const router = useRouter()

  onMounted(async () => {
    console.log("user logout ", useUserSession.value)
    useUserSession.value.logoutUser().then(() => {
      console.log("logout successful")
      // router.push({name: "login"})
    }).catch(() => {
      console.log("logout didnt work")
    })

  })
</script>

<template>
</template>

<style scoped lang="scss">

</style>