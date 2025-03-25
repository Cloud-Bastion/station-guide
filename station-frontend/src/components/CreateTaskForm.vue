<template>
  <div>
    <h2>Create New Task</h2>
    <form @submit.prevent="handleCreateTask">
      <div>
        <label for="title">Title:</label>
        <input type="text" id="title" v-model="newTaskData.title" required>
      </div>
      <div>
        <label for="description">Description:</label>
        <textarea id="description" v-model="newTaskData.description"></textarea>
      </div>
      <div>
        <label for="startTime">Start Time:</label>
        <input type="datetime-local" id="startTime" v-model="newTaskData.startTime">
      </div>
      <div>
        <label for="recurring">Recurring:</label>
        <input type="checkbox" id="recurring" v-model="newTaskData.recurring">
      </div>
      <div>
        <label for="priority">Priority:</label>
        <input type="number" id="priority" v-model="newTaskData.priority" required>
      </div>
      <div>
        <label for="files">Files:</label>
        <input type="file" id="files" multiple @change="handleFileChange">
      </div>

      <button type="submit">Create Task</button>
    </form>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue';
import TaskService, { CreateScheduledTaskRequest } from '../service/TaskService';

export default defineComponent({
  name: 'CreateTaskForm',
  setup() {
    const newTaskData = reactive<CreateScheduledTaskRequest>({
      title: '',
      description: '',
      startTime: '',
      recurring: false,
      priority: 1,
      files: [],
    });

    const error = ref<string | null>(null);

    const handleFileChange = (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.files) {
        // Convert FileList to an array of strings (filenames or base64 encoded data)
        newTaskData.files = Array.from(target.files).map(file => file.name); // Store filenames
        // For actual file content, you'd likely use FileReader to read as base64
      }
    };

    const handleCreateTask = async () => {
      error.value = null; // Clear previous errors
      try {
        // Ensure startTime is in ISO format if it's set
        if (newTaskData.startTime) {
          newTaskData.startTime = new Date(newTaskData.startTime).toISOString();
        }

        const createdTask = await TaskService.createScheduledTask(newTaskData);
        console.log('Task created:', createdTask);

        // Reset the form
        newTaskData.title = '';
        newTaskData.description = '';
        newTaskData.startTime = '';
        newTaskData.recurring = false;
        newTaskData.priority = 1;
        newTaskData.files = [];

        // Optionally, emit an event to notify parent components
        // emit('task-created', createdTask);

      } catch (err: any) {
        console.error('Error creating task:', err);
        error.value = err.message || 'An error occurred while creating the task.';
      }
    };

    return {
      newTaskData,
      handleCreateTask,
      handleFileChange,
      error,
    };
  },
});
</script>

<style scoped>
.error-message {
  color: red;
  margin-top: 10px;
}
</style>
