<template>
  <div :class="$style['top-level-container']">
    <SidebarComponent site="task-management"/>
    <div :class="$style['main-content']">
      <div :class="$style['task-management-container']">
        <div :class="$style['header-container']">
          <h1 :class="$style['title']">Task-Manager</h1>
          <button :class="$style['add-task-button']" @click="openCreateTaskDialog">
            <FontAwesomeIcon icon="plus" :class="$style['add-task-icon']"/>
            <span>Aufgabe erstellen</span>
          </button>
        </div>
        <div :class="$style['task-list']">
          <div v-for="task in tasks" :key="task.id" :class="$style['task-item']">
            <div :class="$style['task-title']">{{ task.title }}</div>
            <div :class="$style['task-description']">{{ task.description }}</div>
            <div :class="$style['task-due-date']">Fällig am: {{ task.dueDate }}</div>
          </div>
          <div v-if="tasks.length === 0" :class="$style['no-tasks']">
            Keine Aufgaben vorhanden.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {ref} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

// Placeholder data for tasks
const tasks = ref([
  {id: 1, title: 'Inventur überprüfen', description: 'Überprüfen Sie die neuesten Inventurdaten.', dueDate: '2024-05-15'},
  {id: 2, title: 'Mitarbeitergespräche führen', description: 'Planen und führen Sie Mitarbeitergespräche.', dueDate: '2024-05-20'},
  {id: 3, title: 'Bestellungen aufgeben', description: 'Geben Sie die wöchentlichen Bestellungen auf.', dueDate: '2024-05-22'},
]);

const openCreateTaskDialog = () => {
  // Placeholder for opening a task creation dialog
  alert('Hier wird ein Dialog zum Erstellen einer Aufgabe geöffnet.');
};
</script>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;

.top-level-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.task-management-container {
  display: flex;
  flex-direction: column;
  margin: 25px;
  padding: 20px;
  background-color: $bg-medium;
  border-radius: $border-radius;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  flex-grow: 1;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .title {
      font-size: 1.5rem;
      font-weight: bold;
      color: $text-color;
    }

    .add-task-button {
      display: flex;
      align-items: center;
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

      .add-task-icon {
        margin-right: 8px;
      }
    }
  }

  .task-list {
    display: flex;
    flex-direction: column;

    .task-item {
      display: flex;
      flex-direction: column;
      background-color: $bg-light;
      padding: 15px;
      margin-bottom: 10px;
      border-radius: $border-radius;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .task-title {
        font-size: 1.1rem;
        font-weight: bold;
        color: $text-color;
        margin-bottom: 5px;
      }

      .task-description {
        color: #ccc;
        margin-bottom: 10px;
      }

      .task-due-date {
        color: #aaa;
        font-size: 0.9rem;
      }
    }

    .no-tasks {
      color: #aaa;
      text-align: center;
      padding: 20px;
    }
  }
}
</style>
