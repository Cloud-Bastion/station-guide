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
          <div v-if="loading" :class="$style['loading']">
            Lade Aufgaben...
          </div>
          <div v-else-if="scheduledTasks.length === 0" :class="$style['no-tasks']">
            Keine geplanten Aufgaben vorhanden.
          </div>
          <div v-else v-for="task in scheduledTasks" :key="task.id" :class="$style['task-item']" @click="selectTask(task)">
            <div :class="$style['task-title']">{{ task.title }}</div>
            <div :class="$style['task-description']">{{ task.description }}</div>
            <div :class="$style['task-schedule']">Geplant für: {{ task.schedule }}</div>
            <div :class="$style['task-priority']">Priorität: {{ task.priority }}</div>
            <div :class="$style['task-completed']" v-if="task.completed">Abgeschlossen</div>
          </div>
        </div>
      </div>

      <!-- Task Details Modal -->
      <div v-if="selectedTask" :class="$style['task-details-modal']">
        <div :class="$style['modal-content']">
          <div :class="$style['modal-header']">
            <h2>{{ selectedTask.title }}</h2>
            <button @click="closeDetails" :class="$style['close-button']">
              <FontAwesomeIcon icon="times"/>
            </button>
          </div>
          <div :class="$style['modal-body']">
            <p><strong>Beschreibung:</strong> {{ selectedTask.description }}</p>
            <p><strong>Geplant für:</strong> {{ selectedTask.schedule }}</p>
            <p><strong>Priorität:</strong> {{ selectedTask.priority }}</p>
            <p><strong>Erstellt von:</strong> {{ selectedTask.createdBy }}</p>
            <p><strong>Dateien:</strong> {{ selectedTask.files.join(', ') }}</p>
            <p><strong>Subtasks:</strong></p>
            <ul>
              <li v-for="subtask in selectedTask.subtasks" :key="subtask.id">{{ subtask.title }}</li>
            </ul>
            <p><strong>Status:</strong> <span :class="selectedTask.completed ? $style['completed'] : $style['pending']">{{ selectedTask.completed ? 'Abgeschlossen' : 'Ausstehend' }}</span></p>
            <button @click="toggleCompletion" :class="$style['complete-button']">
              {{ selectedTask.completed ? 'Als ausstehend markieren' : 'Als abgeschlossen markieren' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {ref, onMounted, computed} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import TaskService from "@/service/TaskService"; // Import the service

// Define the type for the Scheduled Task (matching the backend DTO)
interface ScheduledTask {
  id: string;
  permissionGroup: string;
  startTime: string; // Use string for dates initially
  endTime: string;
  schedule: string;
  title: string;
  description: string;
  subtasks: { id: string; title: string }[]; // Simplified subtask
  files: string[];
  priority: number;
  createdBy: string;
  completed: boolean;
  templateTaskId: string;
}

const scheduledTasks = ref<ScheduledTask[]>([]);
const loading = ref(true);
const selectedTask = ref<ScheduledTask | null>(null);

onMounted(async () => {
  try {
    scheduledTasks.value = await TaskService.getScheduledTasks();
  } catch (error) {
    console.error("Error fetching scheduled tasks:", error);
    // Handle error (e.g., show an error message)
  } finally {
    scheduledTasks.value.push({
      id: 'test-task-id',
      permissionGroup: 'test-group',
      startTime: '2024-05-20T09:00:00',
      endTime: '2024-05-20T17:00:00',
      schedule: 'Täglich',
      title: 'Test Aufgabe',
      description: 'Dies ist eine Beispielaufgabe zur Überprüfung der Anzeige.',
      subtasks: [
        {id: 'subtask-1', title: 'Unteraufgabe 1'},
        {id: 'subtask-2', title: 'Unteraufgabe 2'}
      ],
      files: ['file1.pdf', 'file2.docx'],
      priority: 2,
      createdBy: 'Max Mustermann',
      completed: false,
      templateTaskId: 'template-task-id'
    });
    // --- End Add Test Task ---

    loading.value = false;
    // --- Add Test Task ---
  }
});

const openCreateTaskDialog = () => {
  // Placeholder for opening a task creation dialog (future implementation)
  alert('Hier wird ein Dialog zum Erstellen einer Aufgabe geöffnet.');
};

const selectTask = (task: ScheduledTask) => {
  selectedTask.value = task;
};

const closeDetails = () => {
  selectedTask.value = null;
};

const toggleCompletion = () => {
  if (selectedTask.value) {
    selectedTask.value.completed = !selectedTask.value.completed;
    //  Here, you would also call the backend to update the task's status.
  }
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
      cursor: pointer;
      transition: background-color $transition-speed ease;

      &:hover {
        background-color: darken($bg-light, 5%);
      }

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

      .task-schedule,
      .task-priority {
        color: #aaa;
        font-size: 0.9rem;
        margin-bottom: 5px;
      }
    }

    .no-tasks {
      color: #aaa;
      text-align: center;
      padding: 20px;
    }
    .loading{
      color: #aaa;
      text-align: center;
      padding: 20px;
    }
  }
}

.task-details-modal {
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
      color: $text-color;

      p {
        margin-bottom: 10px;
      }

      ul {
        padding-left: 20px;
        margin-bottom: 10px;
      }
      .completed{
        color: green;
      }
      .pending{
        color: $accent;
      }

      .complete-button {
        padding: 10px 15px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        margin-top: 15px;

        &:hover {
          background-color: $accent-hover;
        }
      }
    }
  }
}
</style>
