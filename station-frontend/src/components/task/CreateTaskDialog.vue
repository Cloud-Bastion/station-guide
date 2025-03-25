<template>
  <div :class="$style['create-task-modal']">
    <div :class="$style['modal-content']">
      <div :class="$style['modal-header']">
        <h2>Aufgabe erstellen</h2>
        <button @click="close" :class="$style['close-button']">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>
      <div :class="$style['modal-body']">
        <div :class="$style['form-group']">
          <label for="task-title">Titel:</label>
          <input type="text" id="task-title" v-model="newTask.title" :class="$style['input']"/>
        </div>
        <div :class="$style['form-group']">
          <label for="task-description">Beschreibung:</label>
          <textarea id="task-description" v-model="newTask.description" :class="$style['textarea']"></textarea>
        </div>
        <div :class="$style['form-group']">
          <label for="task-end-time">Fälligkeitsdatum:</label>
          <input type="datetime-local" id="task-end-time" v-model="newTask.endTime" :class="$style['input']"/>
        </div>
        <div :class="$style['form-group']">
          <label for="task-priority">Priorität:</label>
          <select id="task-priority" v-model="newTask.priority" :class="$style['select']">
            <option :value="1">Niedrig</option>
            <option :value="2">Normal</option>
            <option :value="3">Hoch</option>
            <option :value="4">Sehr hoch</option>
          </select>
        </div>
        <!-- Added Recurring Checkbox -->
        <div :class="[$style['form-group'], $style['form-group-checkbox']]">
          <input type="checkbox" id="task-recurring" v-model="newTask.recurring" :class="$style['checkbox']"/>
          <label for="task-recurring">Wiederkehrend</label>
        </div>
        <!-- Optional: Add Recurrence Rule input if recurring is checked -->
        <div :class="$style['form-group']" v-if="newTask.recurring">
          <label for="task-recurrence-rule">Wiederholungsregel (z.B. Cron):</label>
          <input type="text" id="task-recurrence-rule" v-model="newTask.recurrenceRule" :class="$style['input']" placeholder="z.B. 0 0 * * * *"/>
        </div>
        <div :class="$style['form-group']">
          <label for="task-files">Dateien:</label>
          <input type="file" id="task-files" multiple @change="handleFileUpload" :class="$style['input']"/>
          <!-- TODO: Add file preview/list if needed -->
        </div>
        <button @click="createTask" :class="$style['create-button']">Aufgabe erstellen</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, defineEmits} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import TaskService from "@/service/TaskService"; // Assuming TaskService handles the API call

const emit = defineEmits(['close', 'task-created']);

const newTask = ref({
  title: '',
  description: '',
  endTime: '',
  priority: 2, // Default priority
  files: [] as string[], // Store file names or URLs for now
  recurring: false, // Added recurring field
  recurrenceRule: '', // Added recurrenceRule field
});

const close = () => {
  emit('close');
};

const createTask = async () => {
  try {
    // Convert endTime to ISO string if set
    const endTimeISO = newTask.value.endTime ? new Date(newTask.value.endTime).toISOString() : undefined;

    // Prepare data for the service call, including the new fields
    await TaskService.createScheduledTask({
      title: newTask.value.title,
      description: newTask.value.description,
      endTime: endTimeISO,
      priority: newTask.value.priority,
      files: newTask.value.files, // Assuming file handling is separate or URLs are sufficient
      recurring: newTask.value.recurring, // Pass the recurring value
      recurrenceRule: newTask.value.recurring ? newTask.value.recurrenceRule : undefined, // Pass rule only if recurring
      // You might need to set other fields like permissionGroup, startTime, createdBy based on your needs
    });
    emit('task-created'); // Notify parent component
    close(); // Close the dialog on success
  } catch (error) {
    console.error("Error creating task:", error);
    // Handle error (e.g., show an error message to the user)
    // Consider adding user feedback for errors
  }
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    // Example: Store file names. For actual upload, you'll need a different strategy.
    newTask.value.files = Array.from(target.files).map(file => file.name);
    // If you need to upload files, you'd typically use FormData and send a multipart/form-data request.
    // This example just stores names.
  }
};
</script>

<style module lang="scss">
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;
$input-bg: #333; // Input background
$input-border: #555; // Input border

.create-task-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7); // Darker overlay
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    padding: 25px; // More padding
    width: 90%;
    max-width: 600px;
    position: relative;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px; // More space

      h2 {
        color: $text-color;
        font-size: 1.6rem; // Larger title
        margin: 0;
      }

      .close-button {
        background: none;
        border: none;
        color: #aaa;
        cursor: pointer;
        font-size: 1.4rem; // Larger close icon
        transition: color $transition-speed ease;

        &:hover {
          color: $text-color;
        }
      }
    }

    .modal-body {
      .form-group {
        margin-bottom: 18px; // More space between groups

        label {
          display: block;
          color: $text-color;
          margin-bottom: 8px; // More space below label
          font-size: 0.9rem; // Slightly smaller label
        }

        .input, .textarea, .select {
          width: 100%;
          padding: 10px; // More padding
          border: 1px solid $input-border;
          border-radius: $border-radius;
          background-color: $input-bg;
          color: $text-color;
          box-sizing: border-box; // Include padding and border in width
           transition: border-color $transition-speed ease;

           &:focus {
             border-color: $accent;
             outline: none;
           }
        }

        .textarea {
          resize: vertical; // Allow vertical resizing
          min-height: 100px;
        }

        // Style for checkbox group
        &.form-group-checkbox {
            display: flex;
            align-items: center;
            gap: 10px; // Space between checkbox and label

            label {
                margin-bottom: 0; // Remove bottom margin for checkbox label
                cursor: pointer;
            }

            .checkbox {
                width: 18px; // Adjust size as needed
                height: 18px;
                accent-color: $accent; // Use accent color for the check
                cursor: pointer;
            }
        }
      }

      .create-button {
        padding: 12px 20px; // Larger button
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        width: 100%; // Make button full width
        margin-top: 10px; // Space above button
        font-size: 1rem;

        &:hover {
          background-color: $accent-hover;
        }
      }
    }
  }
}
