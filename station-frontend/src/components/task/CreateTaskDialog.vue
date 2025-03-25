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
        <div :class="$style['form-group']">
          <label for="task-files">Dateien:</label>
          <input type="file" id="task-files" multiple @change="handleFileUpload" :class="$style['input']"/>
        </div>
        <button @click="createTask" :class="$style['create-button']">Aufgabe erstellen</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, defineEmits} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import TaskService from "@/service/TaskService";

const emit = defineEmits(['close', 'task-created']);

const newTask = ref({
  title: '',
  description: '',
  endTime: '',
  priority: 2, // Default priority
  files: [] as string[],
});

const close = () => {
  emit('close');
};

const createTask = async () => {
  try {
    // Convert endTime to ISO string
    const endTimeISO = newTask.value.endTime ? new Date(newTask.value.endTime).toISOString() : undefined;

    await TaskService.createScheduledTask({
      title: newTask.value.title,
      description: newTask.value.description,
      endTime: endTimeISO,
      priority: newTask.value.priority,
      files: newTask.value.files, // Assuming you handle file uploads separately
      // You might need to set other fields like permissionGroup, recurring, etc. based on your needs
    });
    emit('task-created'); // Notify parent component
  } catch (error) {
    console.error("Error creating task:", error);
    // Handle error (e.g., show an error message)
  }
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    // Convert FileList to array of file paths (or however you want to store them)
    newTask.value.files = Array.from(target.files).map(file => URL.createObjectURL(file)); // Store object URLs for preview
    // For actual upload, you'll likely send the File objects to your backend
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

.create-task-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    padding: 20px;
    width: 80%;
    max-width: 600px;
    position: relative;

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
      .form-group {
        margin-bottom: 15px;

        label {
          display: block;
          color: $text-color;
          margin-bottom: 5px;
        }

        .input, .textarea, .select {
          width: 100%;
          padding: 8px;
          border: 1px solid #ccc;
          border-radius: $border-radius;
          background-color: $bg-light;
          color: $text-color;
        }

        .textarea {
          resize: vertical; // Allow vertical resizing
          min-height: 100px;
        }
      }

      .create-button {
        padding: 10px 15px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;

        &:hover {
          background-color: $accent-hover;
        }
      }
    }
  }
}
</style>
