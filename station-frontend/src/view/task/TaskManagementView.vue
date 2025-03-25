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
          <div v-else-if="tasks.length === 0" :class="$style['no-tasks']">
            Keine Aufgaben vorhanden.
          </div>
          <div v-else v-for="task in tasks" :key="task.id" :class="[$style['task-item'], task.completed ? $style['task-completed'] : '']" @click="selectTask(task)">
            <div :class="$style['task-left']">
              <FontAwesomeIcon v-if="task.completed" icon="check-circle" :class="$style['completed-icon']" />
              <FontAwesomeIcon v-else icon="circle" :class="$style['pending-icon']" />
              <div :class="$style['task-title-wrapper']">
                <div :class="$style['task-title']" :style="{ color: isOverdue(task) ? 'red' : '' }">{{ task.title || 'Unbenannte Aufgabe' }}</div>
                <div :class="[$style['task-priority-label'], $style[priorityLabel(task).class]]">{{ priorityLabel(task).text }}</div>
              </div>
            </div>
            <div :class="$style['task-right']">
              <!-- Display scheduled task ID if available -->
              <div v-if="task.scheduledTaskId" :class="$style['task-schedule']">Scheduled Task ID: {{ task.scheduledTaskId }}</div>
              <div v-if="task.endTime" :class="$style['task-due-date']" :style="{ color: isOverdue(task) ? 'red' : '' }">Fällig: {{ formatDateTime(task.endTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Task Details Modal -->
      <div v-if="selectedTask" :class="$style['task-details-modal']">
        <div :class="$style['modal-content']">
          <div :class="$style['modal-header']">
            <h2>{{ selectedTask.title || 'Unbenannte Aufgabe' }}</h2>
            <div :class="[$style['modal-priority-label'], $style[priorityLabel(selectedTask).class]]">{{ priorityLabel(selectedTask).text }}</div>
            <button @click="closeDetails" :class="$style['close-button']">
              <FontAwesomeIcon icon="times"/>
            </button>
          </div>
          <div :class="$style['modal-body']">
            <p :class="$style['modal-description']">{{ selectedTask.description }}</p>

            <!-- Display scheduled task ID if available -->
            <p v-if="selectedTask.scheduledTaskId"><strong>Scheduled Task ID:</strong> {{ selectedTask.scheduledTaskId }}</p>
            <p v-if="selectedTask.endTime"><strong>Fällig:</strong> {{ formatDateTime(selectedTask.endTime) }}</p>
            <p><strong>Status:</strong> <span :class="selectedTask.completed ? $style['completed'] : $style['pending']">{{ selectedTask.completed ? 'Abgeschlossen' : 'Ausstehend' }}</span></p>

            <button @click="toggleCompletion" :class="$style['complete-button']">
              {{ selectedTask.completed ? 'Als ausstehend markieren' : 'Als abgeschlossen markieren' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <CreateTaskDialog v-if="showCreateTaskDialog" @close="closeCreateTaskDialog" @task-created="handleTaskCreated" />
</template>

<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import { ref, onMounted, computed } from 'vue';
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import TaskService from "@/service/TaskService"; // Import the service
import CreateTaskDialog from "@/components/task/CreateTaskDialog.vue";

// --- Interface for OpenPlannedTask (matches StationTaskDTO) ---
interface OpenPlannedTask {
  id: string;
  permissionGroup: string | null;
  endTime: string | null;
  isTemplate: boolean;
  scheduledTaskId: string | null;
  completed: boolean;
  title?: string; // Add title, description, etc.
  description?: string;
  priority?: number;
}

const tasks = ref<OpenPlannedTask[]>([]); // Use OpenPlannedTask
const loading = ref(true);
const selectedTask = ref<OpenPlannedTask | null>(null); // Use OpenPlannedTask
const showCreateTaskDialog = ref(false);

onMounted(async () => {
  await loadTasks();
});

async function loadTasks() {
  try {
    // --- Fetch ALL open tasks ---
    tasks.value = await TaskService.getAllOpenTasks();
  } catch (error) {
    console.error("Error fetching tasks:", error);
  } finally {
    loading.value = false;
  }
}

const openCreateTaskDialog = () => {
  showCreateTaskDialog.value = true;
};

const closeCreateTaskDialog = () => {
  showCreateTaskDialog.value = false;
};

const handleTaskCreated = async () => {
    closeCreateTaskDialog();
    await loadTasks();
}

const selectTask = (task: OpenPlannedTask) => { // Use OpenPlannedTask
  selectedTask.value = task;
};

const closeDetails = () => {
  selectedTask.value = null;
};

const toggleCompletion = () => {
  if (selectedTask.value) {
    selectedTask.value.completed = !selectedTask.value.completed;
    // TODO: Call backend to update task status
  }
};

// --- Priority Label Logic (Adjust as needed) ---
const priorityLabel = (task: OpenPlannedTask) => {
  switch (task.priority) {
    case 1: return { text: 'Niedrig', class: 'priority-low' };
    case 2: return { text: 'Normal', class: 'priority-medium' };
    case 3: return { text: 'Hoch', class: 'priority-high' };
    case 4: return { text: 'Sehr hoch', class: 'priority-very-high' };
    default: return { text: 'Keine Priorität', class: '' }; // Handle undefined priority
  }
};

const isOverdue = (task: OpenPlannedTask) => {
  if (!task.endTime) {
    return false;
  }
  const now = new Date();
  const dueDate = new Date(task.endTime);
  return dueDate < now;
};

const formatDateTime = (dateTimeString: string | null | undefined) => {
  if (!dateTimeString) {
    return '';
  }
  const date = new Date(dateTimeString);
  return date.toLocaleString('de-DE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
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
      justify-content: space-between;
      align-items: center; // Vertically center items
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

      .task-left {
        display: flex;
        //flex-direction: column; /* Stack title and priority vertically */
        align-items: center; /* Align to the left */

        .completed-icon {
          color: green;
          margin-right: 10px;
          font-size: 1.2rem;
        }

        .pending-icon {
          color: #aaa;
          margin-right: 10px;
          font-size: 1.2rem;
        }
        .task-title-wrapper {
          display: flex;
          flex-direction: row;
          align-items: center;
          gap: 10px;

          .task-title {
            font-size: 1.1rem;
            font-weight: bold;
            color: $text-color;
            //margin-bottom: 5px; /* Space between title and priority */
          }

          .task-priority-label {
            font-size: 0.8rem;
            padding: 3px 8px;
            border-radius: 10px; /* More rounded */
            color: white;
            //Button Design
            border: none;

            &.priority-low {
              background-color: green;
            }

            &.priority-medium {
              background-color: orange;
            }

            &.priority-high {
              background-color: red;
            }

            &.priority-very-high {
              background-color: darkred;
            }
          }
        }
      }

      .task-right {
        display: flex;
        align-items: center;
        gap: 15px; // Space between schedule and priority

        .task-schedule,
        .task-priority {
          color: #aaa;
          font-size: 0.9rem;
        }
        .task-due-date {
          color: #aaa;
          font-size: 0.9rem;
        }
      }
    }
    .task-completed {
      .task-title{
        text-decoration: line-through;
        color: #aaa;
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
        margin-bottom: 5px;
      }
      .modal-priority-label {
        font-size: 0.9rem;
        padding: 3px 8px;
        border-radius: 10px; /* More rounded */
        color: white;
        margin-bottom: 10px;
        //Button Design
        border: none;

        &.priority-low {
          background-color: green;
        }
        &.priority-medium {
          background-color: orange;
        }
        &.priority-high {
          background-color: red;
        }
        &.priority-very-high {
          background-color: darkred;
        }
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

      .modal-description {
        text-align: left;
        margin-bottom: 10px;
      }

      p {
        margin-bottom: 10px;
      }

      ul {
        padding-left: 20px;
        margin-bottom: 10px;
        list-style: none;
      }

      .subtask-item {
        display: flex;
        align-items: center;
        margin-bottom: 5px;

        .subtask-completed-icon {
          color: green;
          margin-right: 5px;
        }

        .subtask-pending-icon {
          color: #aaa;
          margin-right: 5px;
        }
        .subtask-complete-button{
          margin-left: auto;
          padding: 4px 8px;
          background-color: $accent;
          color: $text-color;
          border: none;
          border-radius: $border-radius;
          cursor: pointer;
          transition: background-color $transition-speed ease;
          font-size: 0.8rem;

          &:hover {
            background-color: $accent-hover;
          }
        }
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
        &:disabled {
          background-color: #7d1f00;
          cursor: not-allowed;
        }
      }
      .file-list {
        display: flex;
        flex-wrap: wrap; /* Allow wrapping to multiple lines */
        gap: 10px; /* Spacing between items */
        margin-bottom: 10px;

        .file-link {
          display: inline-flex; /* Use inline-flex for proper alignment */
          align-items: center;
          color: $accent;
          text-decoration: none;
          padding: 5px 10px;
          border-radius: $border-radius;
          background-color: $bg-light;
          transition: background-color $transition-speed ease, color $transition-speed ease;

          &:hover {
            background-color: darken($bg-light, 10%);
            color: $accent-hover;
          }

          .download-icon {
            margin-right: 5px;
            font-size: 1rem;
          }
          span{
            font-size: small;
          }
        }
      }
    }
  }
}
</style>
