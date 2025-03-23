<script setup lang="ts">
import {ref, defineEmits} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

const emit = defineEmits(['close']);

const categoryName = ref('');
const reduceTime = ref('');

function addCategory() {
  //TODO: Implement logic to add the category using an API call
  console.log('Adding category:', {
    name: categoryName.value,
    reduceTime: reduceTime.value
  });
  emit('close'); // Close the modal after adding
}
</script>

<template>
  <div :class="$style.modal">
    <div :class="$style['modal-content']">
      <div :class="$style['modal-header']">
        <h2>Kategorie hinzufügen</h2>
        <button @click="$emit('close')" :class="$style['close-button']">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>
      <div :class="$style['modal-body']">
        <div :class="$style['input-group']">
          <label for="category-name">Kategoriename:</label>
          <input id="category-name" type="text" v-model="categoryName" :class="$style['input']"/>
        </div>
        <div :class="$style['input-group']">
          <label for="reduce-time">Reduzierzeit (Tage):</label>
          <input id="reduce-time" type="number" v-model="reduceTime" :class="$style['input']"/>
        </div>
        <button @click="addCategory" :class="$style['add-button']">Hinzufügen</button>
      </div>
    </div>
  </div>
</template>

<style module lang="scss">
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red
$accent-hover: #b83200; // Darker red for hover
$border-radius: 10px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1002; /* Higher than other modals */

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    padding: 20px;
    width: 400px;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      h2 {
        color: $text-color;
        font-size: 1.5rem;
        margin: 0;
      }

      .close-button {
        background: none;
        border: none;
        color: #aaa;
        cursor: pointer;
        font-size: 1.2rem;
        transition: color $transition-speed ease;

        &:hover {
          color: $text-color;
        }
      }
    }

    .modal-body {
      .input-group {
        display: flex;
        flex-direction: column;
        margin-bottom: 10px;

        label {
          color: $text-color;
          margin-bottom: 5px;
        }

        .input {
          padding: 8px;
          border: 1px solid $input-border;
          border-radius: $border-radius;
          background-color: $input-bg;
          color: $text-color;
          transition: border-color $transition-speed ease;

          &:focus {
            border-color: $accent;
            outline: none;
          }
        }
      }

      .add-button {
        padding: 10px 15px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        margin-top: 1rem;

        &:hover {
          background-color: $accent-hover;
        }
      }
    }
  }
}
</style>
