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
          <!-- Task Item Loop -->
          <div v-else v-for="task in tasks" :key="task.id" :class="[$style['task-item'], task.completed ? $style['task-completed'] : '']" @click="selectTask(task)">
            <div :class="$style['task-left']">
              <FontAwesomeIcon v-if="task.completed" icon="check-circle" :class="$style['completed-icon']" />
              <FontAwesomeIcon v-else icon="circle" :class="$style['pending-icon']" />
              <div :class="$style['task-title-wrapper']">
                <div :class="$style['task-title']" :style="{ color: isOverdue(task) ? 'red' : '' }">{{ task.title || 'Unbenannte Aufgabe' }}</div>
                <div :class="[$style['task-priority-label'], $style[priorityLabel(task).class]]">{{ priorityLabel(task).text }}</div>
              </div>
            </div>
            <div :class="$style['task-middle']">
                <div :class="$style['task-created-by']" v-if="task.createdBy">Erstellt von: {{ task.createdBy }}</div>
                <div :class="$style['task-permission-group']" v-if="task.permissionGroup">Gruppe: {{ task.permissionGroup }}</div>
            </div>
            <div :class="$style['task-right']">
              <div :class="$style['task-time-info']">
                <div :class="$style['task-start-time']">Start: {{ task.startTime ? formatDateTime(task.startTime) : "Kein Start" }}</div>
                <div :class="$style['task-due-date']" :style="{ color: isOverdue(task) ? 'red' : '' }">Fällig: {{ task.endTime ? formatDateTime(task.endTime) : "Keine Fälligkeit" }}</div>
              </div>
            </div>
          </div>
          <!-- End Task Item Loop -->
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
            <p :class="$style['modal-description']">{{ selectedTask.description || 'Keine Beschreibung vorhanden.' }}</p>
            <hr :class="$style['modal-divider']">
            <p><strong>Erstellt von:</strong> {{ selectedTask.createdBy || 'Unbekannt' }}</p>
            <p><strong>Berechtigungsgruppe:</strong> {{ selectedTask.permissionGroup || 'Keine' }}</p>
            <p><strong>Startzeit:</strong> {{ selectedTask.startTime ? formatDateTime(selectedTask.startTime) : "Keine Startzeit" }}</p>
            <p><strong>Fällig:</strong> {{ selectedTask.endTime ? formatDateTime(selectedTask.endTime) : "Keine Fälligkeit" }}</p>
            <p><strong>Status:</strong> <span :class="selectedTask.completed ? $style['completed'] : $style['pending']">{{ selectedTask.completed ? 'Abgeschlossen' : 'Ausstehend' }}</span></p>
            <p><strong>Vorlage:</strong> {{ selectedTask.isTemplate ? 'Ja' : 'Nein' }}</p>
            <hr :class="$style['modal-divider']">
            <button @click="toggleCompletion" :class="$style['complete-button']">
              {{ selectedTask.completed ? 'Als ausstehend markieren' : 'Als abgeschlossen markieren' }}
            </button>
            <!-- TODO: Add file list display -->
            <!-- TODO: Add subtask display -->
          </div>
        </div>
      </div>
      <!-- End Task Details Modal -->
    </div>
  </div>
  <CreateTaskDialog v-if="showCreateTaskDialog" @close="closeCreateTaskDialog" @task-created="handleTaskCreated" />
</template>

<script setup lang="ts">
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import { ref, onMounted, computed } from 'vue';
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import TaskService, {Task} from "@/service/TaskService"; // Import the service
import CreateTaskDialog from "@/components/task/CreateTaskDialog.vue";

const tasks = ref<Task[]>([]); // Use Task interface
const loading = ref(true);
const selectedTask = ref<Task | null>(null); // Use Task interface
const showCreateTaskDialog = ref(false);

onMounted(async () => {
  await loadTasks();
});

async function loadTasks() {
  loading.value = true; // Set loading to true when starting to load
  try {
    // --- Fetch ALL open tasks ---
    tasks.value = await TaskService.getAllOpenTasks();
  } catch (error) {
    console.error("Error fetching tasks:", error);
    tasks.value = []; // Clear tasks on error
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
    await loadTasks(); // Reload tasks after creation
}

const selectTask = (task: Task) => {
  selectedTask.value = task;
};

const closeDetails = () => {
  selectedTask.value = null;
};

const toggleCompletion = async () => {
  if (selectedTask.value) {
    selectedTask.value.completed = !selectedTask.value.completed;
    await TaskService.closeOrOpenTask(selectedTask.value.id, selectedTask.value.completed)
  }
};

// --- Priority Label Logic ---
const priorityLabel = (task: Task) => {
  switch (task.priority) {
    case 1: return { text: 'Niedrig', class: 'priority-low' };
    case 2: return { text: 'Normal', class: 'priority-medium' };
    case 3: return { text: 'Hoch', class: 'priority-high' };
    case 4: return { text: 'Sehr hoch', class: 'priority-very-high' };
    default: return { text: 'Prio?', class: 'priority-unknown' }; // Handle unknown/default priority
  }
};

// --- Check if Task is Overdue ---
const isOverdue = (task: Task): boolean => {
  if (!task.endTime || task.completed) { // Don't show overdue if completed
    return false;
  }
  const now = new Date();
  const dueDate = new Date(task.endTime);
  return dueDate < now;
};

// --- Format Date/Time ---
const formatDateTime = (dateTimeString: string | null | undefined): string => {
  if (!dateTimeString) {
    return '';
  }
  try {
    const date = new Date(dateTimeString);
    // Check if date is valid
    if (isNaN(date.getTime())) {
        return "Ungültiges Datum";
    }
    return date.toLocaleString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch (e) {
    console.error("Error formatting date:", dateTimeString, e);
    return "Fehler Datum"; // Indicate error during formatting
  }
};

</script>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0; // Lighter text for secondary info
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 5px;
$transition-speed: 0.3s;
$priority-low: #2ecc71; // Green
$priority-medium: #f39c12; // Orange
$priority-high: #e74c3c; // Red
$priority-very-high: #c0392b; // Darker Red
$completed-color: #27ae60; // Green for completed
$pending-color: $accent; // Accent for pending
$overdue-color: $priority-high; // Red for overdue

.top-level-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow-y: auto; // Allow scrolling if content overflows
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
    padding-bottom: 15px;
    border-bottom: 1px solid $bg-light; // Separator line

    .title {
      font-size: 1.8rem; // Slightly larger title
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
      font-size: 0.9rem;

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
    gap: 12px; // Space between task items

    .task-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: $bg-light;
      padding: 12px 18px; // Adjusted padding
      border-radius: $border-radius;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.15);
      cursor: pointer;
      transition: background-color $transition-speed ease, transform 0.2s ease;

      &:hover {
        background-color: darken($bg-light, 5%);
        transform: translateY(-2px); // Slight lift on hover
      }

      .task-left {
        display: flex;
        align-items: center;
        gap: 12px; // Space between icon and title wrapper
        flex-shrink: 0; // Prevent shrinking

        .completed-icon, .pending-icon {
          font-size: 1.3rem; // Slightly larger icons
        }
        .completed-icon { color: $completed-color; }
        .pending-icon { color: $text-color-light; }

        .task-title-wrapper {
          display: flex;
          align-items: center;
          gap: 10px;

          .task-title {
            font-size: 1.1rem;
            font-weight: 600; // Slightly bolder
            color: $text-color;
          }

          .task-priority-label {
            font-size: 0.75rem; // Smaller priority label
            padding: 2px 8px;
            border-radius: 10px;
            color: white;
            font-weight: 500;
            white-space: nowrap; // Prevent wrapping

            &.priority-low { background-color: $priority-low; }
            &.priority-medium { background-color: $priority-medium; }
            &.priority-high { background-color: $priority-high; }
            &.priority-very-high { background-color: $priority-very-high; }
            &.priority-unknown { background-color: $text-color-light; color: $bg-dark; }
          }
        }
      }

      .task-middle {
          display: flex;
          flex-direction: column; // Stack createdBy and permissionGroup
          align-items: flex-start; // Align text to the start
          gap: 2px; // Small gap between lines
          margin: 0 15px; // Add horizontal margin
          flex-grow: 1; // Allow middle section to grow
          min-width: 100px; // Ensure some minimum width
          overflow: hidden; // Hide overflow

          .task-created-by, .task-permission-group {
              font-size: 0.8rem;
              color: $text-color-light;
              white-space: nowrap; // Prevent wrapping
              overflow: hidden; // Hide overflow
              text-overflow: ellipsis; // Add ellipsis if text is too long
          }
      }


      .task-right {
        display: flex;
        align-items: center;
        gap: 15px;
        flex-shrink: 0; // Prevent shrinking

        .task-time-info {
            display: flex;
            flex-direction: column; // Stack start and due date
            align-items: flex-end; // Align dates to the right
            gap: 2px; // Small gap between dates

            .task-start-time, .task-due-date {
              color: $text-color-light;
              font-size: 0.8rem; // Smaller font for dates
              white-space: nowrap; // Prevent wrapping
            }
            .task-due-date[style*="color: red"] { // More specific selector for overdue
                color: $overdue-color !important; // Ensure overdue color overrides
                font-weight: 600;
            }
        }
      }
    }
    .task-completed {
      background-color: darken($bg-light, 3%); // Slightly different background for completed
      .task-title{
        text-decoration: line-through;
        color: $text-color-light; // Dim completed task title
      }
      &:hover {
         background-color: darken($bg-light, 7%);
      }
    }

    .no-tasks, .loading {
      color: $text-color-light;
      text-align: center;
      padding: 30px;
      font-size: 1.1rem;
    }
  }
}

// --- Task Details Modal ---
.task-details-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6); // Slightly darker overlay
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.4);
    padding: 25px 30px; // More padding
    width: 90%;
    max-width: 650px; // Wider modal
    position: relative;
    display: flex;
    flex-direction: column;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      padding-bottom: 15px;
      border-bottom: 1px solid $bg-light;

      h2 {
        color: $text-color;
        font-size: 1.6rem; // Larger title
        margin: 0;
        flex-grow: 1; // Allow title to take space
        margin-right: 15px; // Space before priority label
      }
      .modal-priority-label {
        font-size: 0.85rem;
        padding: 4px 10px;
        border-radius: 12px;
        color: white;
        font-weight: 500;
        white-space: nowrap;
        flex-shrink: 0; // Prevent shrinking

        &.priority-low { background-color: $priority-low; }
        &.priority-medium { background-color: $priority-medium; }
        &.priority-high { background-color: $priority-high; }
        &.priority-very-high { background-color: $priority-very-high; }
        &.priority-unknown { background-color: $text-color-light; color: $bg-dark; }
      }

      .close-button {
        background: none;
        border: none;
        color: $text-color-light;
        cursor: pointer;
        font-size: 1.5rem; // Larger close icon
        transition: color $transition-speed ease;
        padding: 5px; // Easier to click
        margin-left: 15px; // Space after priority label

        &:hover {
          color: $text-color;
        }
      }
    }

    .modal-body {
      color: $text-color;
      flex-grow: 1; // Allow body to take remaining space
      overflow-y: auto; // Add scroll if content overflows
      max-height: 60vh; // Limit modal height

      .modal-description {
        text-align: left;
        margin-bottom: 15px;
        line-height: 1.5; // Better readability
        color: $text-color-light; // Slightly lighter description
        white-space: pre-wrap; // <-- HIER DIE ÄNDERUNG: Whitespace und Zeilenumbrüche beibehalten
      }

      p {
        margin-bottom: 12px; // Consistent spacing
        font-size: 0.95rem;
        strong {
            color: $text-color;
            margin-right: 5px;
        }
        color: $text-color-light; // Lighter text for details
      }

      .modal-divider {
          border: none;
          height: 1px;
          background-color: $bg-light;
          margin: 15px 0;
      }

      .completed{ color: $completed-color; font-weight: bold; }
      .pending{ color: $pending-color; font-weight: bold; }

      .complete-button {
        padding: 10px 18px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        margin-top: 20px; // More space above button
        font-size: 0.95rem;

        &:hover {
          background-color: $accent-hover;
        }
        &:disabled {
          background-color: darken($accent, 20%);
          cursor: not-allowed;
        }
      }
      // Placeholder styles for files/subtasks if added later
      .file-list, .subtask-list {
          margin-top: 15px;
          padding-top: 15px;
          border-top: 1px solid $bg-light;
          // Add specific styles here
      }
    }
  }
}

// --- Media Query for smaller screens ---
@media (max-width: 768px) {
    .task-management-container {
        margin: 15px;
        padding: 15px;
        .header-container {
            flex-direction: column;
            align-items: flex-start;
            gap: 10px;
            .title { font-size: 1.5rem; }
            .add-task-button { align-self: flex-end; }
        }
        .task-list .task-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
            padding: 10px 12px;
            .task-left { gap: 8px; }
            .task-middle { margin: 5px 0; } // Adjust margin for column layout
            .task-right {
                width: 100%; // Take full width
                .task-time-info {
                    width: 100%;
                    flex-direction: row; // Side-by-side dates on small screens
                    justify-content: space-between; // Space them out
                    align-items: center;
                }
            }
        }
    }
    .task-details-modal .modal-content {
        max-width: 95%;
        padding: 20px;
        .modal-header {
            h2 { font-size: 1.4rem; }
            .modal-priority-label { font-size: 0.8rem; padding: 3px 8px; }
        }
    }
}

</style>
