<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
</script>

<template>
  <div :class="$style['parent']">
    <div :class="$style['title-container']">
      <div :class="$style['title']">Mitarbeiter-Übersicht:</div>
      <div :class="$style['employee-button-container']">
        <div :class="$style['add-employee-button']">
          <FontAwesomeIcon icon="fa-person-circle-plus" size="xl"/>
          Mitarbeiter anlegen
        </div>
        <div :class="$style['archive-employee-button']">
          <FontAwesomeIcon icon="fa-box-archive" size="xl"/>
          Archiv
        </div>
      </div>
    </div>
    <div :class="$style['subtitle']">Eine Übersicht deiner Mitarbeiter</div>
    <div :class="$style['stick']"/>
    <div :class="$style['employee-container']">
<!--      <div :class="$style['employee-entity-container']">
        <div :class="$style['employee-entity-entry-name-container']">
          <div :class="$style['employee-entity-entry-name-name']">Max Mustermann</div>
          <div :class="$style['employee-entity-entry-name-mail']">max.mustermann@gmail.com</div>
        </div>
        <div :class="$style['employee-entity-entry']">+49 176 636 5575 9</div>
        <div :class="$style['employee-entity-entry-dob-container']">
          <div :class="$style['employee-entity-entry-dob-dob']">06.01.2002</div>
          <div :class="$style['employee-entity-entry-dob-age']">23 years</div>
        </div>
        <FontAwesomeIcon icon="fa-business-time" size="lg" :class="$style['employee-entity-entry-icon']"></FontAwesomeIcon>
        <FontAwesomeIcon icon="fa-trash-arrow-up" size="lg" :class="$style['employee-entity-entry-icon']"></FontAwesomeIcon>
        &lt;!&ndash; Upload der Kündigung und Account wird inaktiv gesetzt &ndash;&gt;
      </div>-->
    </div>
  </div>
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;
$border-design: 0.1vh solid #555;

.parent {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  background-color: $bg-medium; // Dark background
  border-radius: $border-radius;
  margin: 25px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);

  .title-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center; // Vertically center items

    .title {
      align-self: flex-start;
      font-size: large;
      font-weight: bold;
      text-decoration: underline;
      color: $text-color;
      margin: 15px 10px 10px 10px;
    }

    .employee-button-container {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      align-self: flex-end;

      .archive-employee-button, .add-employee-button {
        font-size: small;
        color: $text-color;
        background-color: $bg-light; // Slightly lighter background
        border-radius: $border-radius;
        padding: 8px 12px; // Slightly larger padding
        margin: 0 10px;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        display: flex;
        align-items: center;
        gap: 5px; // Space between icon and text

        &:hover {
          background-color: $accent; // Red on hover
        }
      }
    }
  }

  .subtitle {
    align-self: flex-start;
    font-size: small;
    color: #b0b0b0;
    margin: 0 10px;
  }

  .stick {
    flex: 0 0 2px;
    margin: 25px 10px;
    border-radius: 20px;
    background-color: #444; // Darker stick color
  }

  .employee-container {
    display: flex;
    flex-direction: column;

    .employee-entity-container {
      display: flex;
      flex-direction: row;
      // Add styles for employee entries here, if needed
    }
  }
}
</style>
