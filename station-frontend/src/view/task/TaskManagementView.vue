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
          <div v-else v-for="task in scheduledTasks" :key="task.id" :class="[$style['task-item'], task.completed ? $style['task-completed'] : '']" @click="selectTask(task)">
            <div :class="$style['task-left']">
              <FontAwesomeIcon v-if="task.completed" icon="check-circle" :class="$style['completed-icon']" />
              <FontAwesomeIcon v-else icon="circle" :class="$style['pending-icon']" />
              <div :class="$style['task-title-wrapper']">
                <div :class="$style['task-title']" :style="{ color: isOverdue(task) ? 'red' : '' }">{{ task.title }}</div>
                <div :class="[$style['task-priority-label'], $style[priorityLabel(task).class]]">{{ priorityLabel(task).text }}</div>
              </div>
            </div>
            <div :class="$style['task-right']">
              <div :class="$style['task-schedule']">{{ task.schedule }}</div>
              <div v-if="task.endTime" :class="$style['task-due-date']" :style="{ color: isOverdue(task) ? 'red' : '' }">Fällig: {{ formatDateTime(task.endTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Task Details Modal -->
      <div v-if="selectedTask" :class="$style['task-details-modal']">
        <div :class="$style['modal-content']">
          <div :class="$style['modal-header']">
            <h2>{{ selectedTask.title }}</h2>
            <!-- Corrected dynamic class binding in modal -->
            <div :class="[$style['modal-priority-label'], $style[priorityLabel(selectedTask).class]]">{{ priorityLabel(selectedTask).text }}</div>
            <button @click="closeDetails" :class="$style['close-button']">
              <FontAwesomeIcon icon="times"/>
            </button>
          </div>
          <div :class="$style['modal-body']">
            <p><strong>Beschreibung:</strong> {{ selectedTask.description }}</p>
            <p><strong>Geplant für:</strong> {{ formatDateTime(selectedTask.startTime) }}</p>
            <p><strong>Fällig:</strong> {{ formatDateTime(selectedTask.endTime) }}</p>
            <p><strong>Erstellt von:</strong> {{ selectedTask.createdBy }}</p>
            <p><strong>Dateien:</strong></p>
            <div :class="$style['file-list']">
              <a v-for="file in selectedTask.files" :key="file" :href="file" :download="getFilenameFromUrl(file)" :class="$style['file-link']">
                <FontAwesomeIcon icon="download" :class="$style['download-icon']"/>
                <span>{{ getFilenameFromUrl(file) }}</span>
              </a>
            </div>
            <p><strong>Subtasks:</strong></p>
            <ul>
              <li v-for="subtask in selectedTask.subtasks" :key="subtask.id" :class="$style['subtask-item']">
                <FontAwesomeIcon v-if="subtask.completed" icon="check-circle" :class="$style['subtask-completed-icon']" />
                <FontAwesomeIcon v-else icon="circle" :class="$style['subtask-pending-icon']" />
                <span>{{ subtask.title }}</span>
                <button @click="toggleSubtaskCompletion(subtask)" :class="$style['subtask-complete-button']">
                  {{ subtask.completed ? 'Als ausstehend markieren' : 'Als abgeschlossen markieren' }}
                </button>
              </li>
            </ul>
            <p><strong>Status:</strong> <span :class="selectedTask.completed ? $style['completed'] : $style['pending']">{{ selectedTask.completed ? 'Abgeschlossen' : 'Ausstehend' }}</span></p>
            <button @click="toggleCompletion" :class="$style['complete-button']" :disabled="!canCompleteTask">
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
  endTime: string; // Added endTime for due date
  schedule: string;
  title: string;
  description: string;
  subtasks: { id: string; title: string, completed: boolean }[]; // Subtask with completion status
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
    // --- Add Test Task ---
    scheduledTasks.value.push({
      id: 'test-task-id',
      permissionGroup: 'test-group',
      startTime: '2024-05-20T09:00:00',
      endTime: '2024-05-18T17:30:00', // Overdue task
      schedule: 'Täglich',
      title: 'Test Aufgabe (Overdue)',
      description: 'Dies ist eine Beispielaufgabe zur Überprüfung der Anzeige.',
      subtasks: [
        {id: 'subtask-1', title: 'Unteraufgabe 1', completed: false},
        {id: 'subtask-2', title: 'Unteraufgabe 2', completed: false}
      ],
      files: ['/path/to/file1.pdf', '/path/to/file2.docx'], // Example file URLs
      priority: 2,
      createdBy: 'Max Mustermann',
      completed: false,
      templateTaskId: 'template-task-id'
    });
      scheduledTasks.value.push({
          id: 'test-task-id-2',
          permissionGroup: 'test-group',
          startTime: '2024-05-20T10:00:00',
          endTime: '2024-06-20T17:00:00', // Not overdue task
          schedule: 'Täglich',
          title: 'Test Aufgabe 2',
          description: 'Dies ist eine weitere Beispielaufgabe.',
          subtasks: [
              {id: 'subtask-3', title: 'Unteraufgabe 3', completed: false},
              {id: 'subtask-4', title: 'Unteraufgabe 4', completed: false}
          ],
          files: ['/path/to/file3.pdf', '/path/to/file4.docx'], // More example URLs
          priority: 3,
          createdBy: 'Max Mustermann',
          completed: false,
          templateTaskId: 'template-task-id'
      });
    loading.value = false;
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
    if (!selectedTask.value.completed && selectedTask.value.subtasks.some(st => !st.completed)) {
      alert('Alle Unteraufgaben müssen abgeschlossen sein, bevor die Hauptaufgabe abgeschlossen werden kann.');
      return;
    }
    selectedTask.value.completed = !selectedTask.value.completed;
    //  Here, you would also call the backend to update the task's status.
  }
};

const toggleSubtaskCompletion = (subtask: { id: string; title: string; completed: boolean }) => {
  subtask.completed = !subtask.completed;
  // Here you would also call the backend to update the subtask's status
};

// Computed property to check if the main task can be completed
const canCompleteTask = computed(() => {
  return selectedTask.value && (selectedTask.value.completed || selectedTask.value.subtasks.every(st => st.completed));
});

// Corrected computed property: Use this.$style to access CSS Module classes
const priorityLabel = (task: ScheduledTask) => {
    switch (task.priority) {
        case 1: return { text: 'Niedrig', class: 'priority-low' };
        case 2: return { text: 'Normal', class: 'priority-medium' };
        case 3: return { text: 'Hoch', class: 'priority-high' };
        case 4: return { text: 'Sehr hoch', class: 'priority-very-high' };
        default: return { text: '', class: '' }; // Important: return empty string for default
    }
};

const isOverdue = (task: ScheduledTask) => {
    if (!task.endTime) {
        return false; // No due date, not overdue
    }
    const now = new Date();
    const dueDate = new Date(task.endTime);
    return dueDate < now;
}

const formatDateTime = (dateTimeString: string | undefined) => {
    if (!dateTimeString) {
        return '';
    }
    const date = new Date(dateTimeString);
    const formattedDate = date.toLocaleDateString('de-DE', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
    });
    const formattedTime = date.toLocaleTimeString('de-DE', {
        hour: '2-digit',
        minute: '2-digit',
    });
    return `${formattedDate} - ${formattedTime}`;
};

const getFilenameFromUrl = (url: string) => {
    return url.substring(url.lastIndexOf('/') + 1);
}
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
