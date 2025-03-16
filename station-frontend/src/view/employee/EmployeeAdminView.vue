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
.parent {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  background-color: #111111;
  border-radius: 5px 5px 5px 5px;
  margin: 25px 25px;

  .title-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;

    .title {
      align-self: flex-start;
      font-size: large;
      font-weight: bold;
      text-decoration: underline;
      color: white;
      margin: 15px 10px 10px 10px;
    }

    .employee-button-container {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      align-self: flex-end;

      .archive-employee-button {
        font-size: small;
        color: white;
        background-color: black;
        border-radius: 5px 5px 5px 5px;
        padding: 5px;
        margin: 0 10px;
      }

      .add-employee-button {
        font-size: small;
        color: white;
        background-color: black;
        border-radius: 5px 5px 5px 5px;
        padding: 5px;
        margin: 0 10px;
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
    background-color: rgba(50, 50, 66, 1);
  }

  .employee-container {
    display: flex;
    flex-direction: column;

    .employee-entity-container {
      display: flex;
      flex-direction: row;
    }
  }
}
</style>