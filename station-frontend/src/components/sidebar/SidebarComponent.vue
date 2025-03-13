<script setup lang="ts">

</script>

<template>
  <div :class="$style['parent']">
    <img src="/logo.png" alt="circle k logo" class="w-32 h-auto">
    <div :class="$style['logo']">Logo</div>
    <div :class="$style['employeeManager']">Mitarbeiter</div>
    <div :class="$style['mhdTool']">MHD-Tool</div>
  </div>
</template>

<style lang="scss" module>
  .parent {

  }
</style>