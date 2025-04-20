<script setup lang="ts">
import { ref, reactive, defineEmits } from 'vue';
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import EmployeeService from "@/service/EmployeeService"; // Assuming EmployeeService will have createEmployee

const emit = defineEmits(['close', 'employee-created']);

const newEmployee = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  // Add other fields if necessary based on your backend requirements
});

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// Basic validation (can be expanded)
const validateForm = () => {
  errorMessage.value = '';
  if (!newEmployee.firstName) {
    errorMessage.value = 'Vorname ist erforderlich.';
    return false;
  }
  if (!newEmployee.lastName) {
    errorMessage.value = 'Nachname ist erforderlich.';
    return false;
  }
  if (!newEmployee.email || !/\S+@\S+\.\S+/.test(newEmployee.email)) {
    errorMessage.value = 'Gültige E-Mail ist erforderlich.';
    return false;
  }
  if (!newEmployee.password || newEmployee.password.length < 8) { // Example: Minimum password length
    errorMessage.value = 'Passwort muss mindestens 8 Zeichen lang sein.';
    return false;
  }
  return true;
};

const createEmployee = async () => {
  if (!validateForm()) {
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    // Prepare data matching the expected Account structure for creation
    const accountData = {
        firstName: newEmployee.firstName,
        lastName: newEmployee.lastName,
        email: newEmployee.email,
        password: newEmployee.password,
        // badgeNumber might be generated server-side
        // authorities might have a default or be set later
    };

    await EmployeeService.createEmployee(accountData); // Call the service function

    successMessage.value = 'Mitarbeiter erfolgreich erstellt!';
    isLoading.value = false;

    // Optionally reset form
    newEmployee.firstName = '';
    newEmployee.lastName = '';
    newEmployee.email = '';
    newEmployee.password = '';

    emit('employee-created'); // Notify parent
    setTimeout(() => {
        closeDialog(); // Close after a short delay
    }, 1500); // Close after 1.5 seconds

  } catch (error: any) {
    console.error("Error creating employee:", error);
    errorMessage.value = `Fehler beim Erstellen des Mitarbeiters: ${error.response?.data?.message || error.message || 'Unbekannter Fehler'}`;
    isLoading.value = false;
  }
};

const closeDialog = () => {
  // Reset state if needed before closing
  errorMessage.value = '';
  successMessage.value = '';
  isLoading.value = false;
  emit('close');
};
</script>

<template>
  <div :class="$style['modal-overlay']">
    <div :class="$style['modal-content']">
      <div :class="$style['modal-header']">
        <h2>Neuen Mitarbeiter anlegen</h2>
        <button @click="closeDialog" :class="$style['close-button']" aria-label="Schließen">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>
      <div :class="$style['modal-body']">
        <form @submit.prevent="createEmployee">
          <div :class="$style['form-group']">
            <label for="employee-firstname">Vorname <span :class="$style['required']">*</span></label>
            <input type="text" id="employee-firstname" v-model="newEmployee.firstName" :class="$style['input']" required :disabled="isLoading"/>
          </div>
          <div :class="$style['form-group']">
            <label for="employee-lastname">Nachname <span :class="$style['required']">*</span></label>
            <input type="text" id="employee-lastname" v-model="newEmployee.lastName" :class="$style['input']" required :disabled="isLoading"/>
          </div>
          <div :class="$style['form-group']">
            <label for="employee-email">E-Mail <span :class="$style['required']">*</span></label>
            <input type="email" id="employee-email" v-model="newEmployee.email" :class="$style['input']" required :disabled="isLoading"/>
          </div>
          <div :class="$style['form-group']">
            <label for="employee-password">Passwort <span :class="$style['required']">*</span></label>
            <input type="password" id="employee-password" v-model="newEmployee.password" :class="$style['input']" required :disabled="isLoading"/>
            <small>Mindestens 8 Zeichen.</small>
          </div>

          <!-- Add other fields as needed -->

          <div v-if="errorMessage" :class="$style['error-message']">
            {{ errorMessage }}
          </div>
           <div v-if="successMessage" :class="$style['success-message']">
            {{ successMessage }}
          </div>

          <button type="submit" :class="$style['create-button']" :disabled="isLoading">
            <FontAwesomeIcon v-if="isLoading" icon="spinner" spin :class="$style['button-icon']"/>
            <FontAwesomeIcon v-else icon="check" :class="$style['button-icon']"/>
            {{ isLoading ? 'Wird erstellt...' : 'Mitarbeiter erstellen' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style module lang="scss">
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 6px;
$transition-speed: 0.2s;
$input-bg: #333;
$input-border: #555;
$input-focus-border: $accent;
$required-color: $accent;
$error-color: $accent;
$success-color: #2ecc71; // Green

.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001; // Ensure it's above settings menu if needed
  padding: 20px;
}

.modal-content {
  background-color: $bg-medium;
  border-radius: $border-radius;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  padding: 0;
  width: 100%;
  max-width: 550px; // Adjust width as needed
  position: relative;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 25px;
  border-bottom: 1px solid $bg-light;

  h2 {
    color: $text-color;
    font-size: 1.4rem;
    font-weight: 600;
    margin: 0;
  }

  .close-button {
    background: none;
    border: none;
    color: $text-color-light;
    cursor: pointer;
    font-size: 1.6rem;
    padding: 5px;
    line-height: 1;
    transition: color $transition-speed ease;

    &:hover {
      color: $text-color;
    }
  }
}

.modal-body {
  padding: 25px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 18px; // Gap between form groups

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;

    label {
      color: $text-color;
      font-size: 0.9rem;
      font-weight: 500;
    }

    .required {
      color: $required-color;
      margin-left: 2px;
    }

    .input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid $input-border;
      border-radius: $border-radius;
      background-color: $input-bg;
      color: $text-color;
      box-sizing: border-box;
      font-size: 0.95rem;
      transition: border-color $transition-speed ease, box-shadow $transition-speed ease;

      &:focus {
        border-color: $input-focus-border;
        outline: none;
        box-shadow: 0 0 0 2px rgba($input-focus-border, 0.3);
      }
      &:disabled {
          background-color: darken($input-bg, 5%);
          cursor: not-allowed;
      }
    }
     small {
        font-size: 0.8rem;
        color: $text-color-light;
     }
  }

  .error-message {
    color: $error-color;
    background-color: rgba($error-color, 0.1);
    border: 1px solid rgba($error-color, 0.3);
    border-radius: $border-radius;
    padding: 10px 15px;
    font-size: 0.9rem;
    margin-top: 5px;
  }
  .success-message {
    color: $success-color;
    background-color: rgba($success-color, 0.1);
    border: 1px solid rgba($success-color, 0.3);
    border-radius: $border-radius;
    padding: 10px 15px;
    font-size: 0.9rem;
    margin-top: 5px;
  }

  .create-button {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 12px 25px;
    background-color: $accent;
    color: $text-color;
    border: none;
    border-radius: $border-radius;
    cursor: pointer;
    transition: background-color $transition-speed ease, transform 0.1s ease;
    width: 100%;
    margin-top: 15px;
    font-size: 1rem;
    font-weight: 600;

    &:hover:not(:disabled) {
      background-color: $accent-hover;
    }
    &:active:not(:disabled) {
      transform: scale(0.98);
    }
    &:disabled {
        background-color: darken($accent, 20%);
        cursor: not-allowed;
    }

    .button-icon {
      font-size: 0.9em;
    }
  }
}
</style>
