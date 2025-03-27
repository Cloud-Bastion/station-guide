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

        <!-- Task Type Selection -->
        <div :class="$style['form-group']">
          <label>Aufgabentyp:</label>
          <div :class="$style['radio-group']">
            <label>
              <input type="radio" value="single" v-model="taskType" :class="$style['radio']"/> Einmalig
            </label>
            <label>
              <input type="radio" value="recurring" v-model="taskType" :class="$style['radio']"/> Wiederkehrend
            </label>
          </div>
        </div>

        <!-- Common Fields -->
        <div :class="$style['form-group']">
          <label for="task-title">Titel:</label>
          <input type="text" id="task-title" v-model="newTask.title" :class="$style['input']"/>
        </div>
        <div :class="$style['form-group']">
          <label for="task-description">Beschreibung:</label>
          <textarea id="task-description" v-model="newTask.description" :class="$style['textarea']"></textarea>
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
          <label for="task-permission-group">Berechtigungsgruppe (Optional):</label>
          <input type="text" id="task-permission-group" v-model="newTask.permissionGroup" :class="$style['input']"/>
        </div>

        <!-- Fields for Single Task -->
        <div v-if="taskType === 'single'">
          <div :class="$style['form-group']">
            <label for="task-start-time">Startzeit (Optional):</label>
            <input type="datetime-local" id="task-start-time" v-model="newTask.startTime" :class="$style['input']"/>
          </div>
          <div :class="$style['form-group']">
            <label for="task-end-time">Fälligkeitsdatum (Optional):</label>
            <input type="datetime-local" id="task-end-time" v-model="newTask.endTime" :class="$style['input']"/>
          </div>
        </div>

        <!-- Fields for Recurring Task -->
        <div v-if="taskType === 'recurring'">
          <div :class="$style['form-group']">
            <label for="task-frequency">Frequenz:</label>
            <!-- TODO: Consider using a select dropdown for predefined frequencies -->
            <input type="text" id="task-frequency" v-model="newTask.frequency" placeholder="z.B. DAILY, WEEKLY, MONTHLY" :class="$style['input']"/>
          </div>
          <div :class="$style['form-group']">
            <label>Tage der Woche (wenn Frequenz WEEKLY):</label>
            <div :class="$style['checkbox-group']">
              <label v-for="day in weekDays" :key="day.value">
                <input type="checkbox" :value="day.value" v-model="newTask.daysOfWeek" :class="$style['checkbox-inline']"/> {{ day.label }}
              </label>
            </div>
          </div>
          <div :class="$style['form-group']">
            <label>Tage des Monats (wenn Frequenz MONTHLY):</label>
             <div :class="$style['checkbox-group']">
                 <label v-for="day in monthDays" :key="day.value">
                     <input type="checkbox" :value="day.value" v-model="newTask.daysOfMonth" :class="$style['checkbox-inline']"/> {{ day.label }}
                 </label>
             </div>
             <small>32 = Letzter Tag des Monats</small>
          </div>
          <div :class="$style['form-group']">
            <label for="task-schedule-start-time">Startzeit (HH:mm):</label>
            <input type="time" id="task-schedule-start-time" v-model="newTask.scheduleStartTime" :class="$style['input']"/>
          </div>
          <div :class="$style['form-group']">
            <label for="task-schedule-end-time">Endzeit (Optional, HH:mm):</label>
            <input type="time" id="task-schedule-end-time" v-model="newTask.scheduleEndTime" :class="$style['input']"/>
          </div>
           <div :class="$style['form-group']">
            <label for="task-end-time-days-add">Endzeit Tage hinzufügen (Optional):</label>
            <input type="number" id="task-end-time-days-add" v-model.number="newTask.endTimeDaysAdd" :class="$style['input']"/>
             <small>Anzahl der Tage, die zur Startzeit addiert werden, um die Endzeit zu bestimmen (wenn keine Endzeit angegeben ist).</small>
          </div>
        </div>

        <!-- File Upload (Common) -->
        <!-- TODO: Implement actual file upload logic if needed -->
        <!--
        <div :class="$style['form-group']">
          <label for="task-files">Dateien:</label>
          <input type="file" id="task-files" multiple @change="handleFileUpload" :class="$style['input']"/>
        </div>
        -->

        <button @click="submitTask" :class="$style['create-button']">Aufgabe erstellen</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, defineEmits, reactive} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import TaskService, { TaskCreateRequest, ScheduledTaskCreateRequest } from "@/service/TaskService";

const emit = defineEmits(['close', 'task-created']);

const taskType = ref<'single' | 'recurring'>('single'); // Default to single task

const newTask = reactive({
  // Common fields
  title: '',
  description: '',
  priority: 2, // Default priority
  permissionGroup: '',
  // files: [] as string[], // Store file names or URLs for now - TODO: Implement upload

  // Single Task fields
  startTime: '', // For datetime-local input
  endTime: '',   // For datetime-local input

  // Recurring Task fields
  frequency: '',
  daysOfWeek: [] as number[],
  daysOfMonth: [] as number[],
  scheduleStartTime: '', // For time input "HH:mm"
  scheduleEndTime: '',   // For time input "HH:mm"
  endTimeDaysAdd: null as number | null,
});

// Options for days checkboxes
const weekDays = [
  { label: 'Mo', value: 1 }, { label: 'Di', value: 2 }, { label: 'Mi', value: 3 },
  { label: 'Do', value: 4 }, { label: 'Fr', value: 5 }, { label: 'Sa', value: 6 }, { label: 'So', value: 7 }
];
const monthDays = Array.from({ length: 32 }, (_, i) => ({ label: (i + 1).toString(), value: i + 1 }));
monthDays[31].label = 'Letzter'; // Label for 32

const close = () => {
  emit('close');
};

// Format time string to "HH:mm:ss" or return null
const formatTime = (timeString: string | null | undefined): string | null => {
    if (!timeString) return null;
    // Basic check, assumes "HH:mm" input from <input type="time">
    if (/^\d{2}:\d{2}$/.test(timeString)) {
        return `${timeString}:00`;
    }
    // If already includes seconds, return as is (or handle other formats if needed)
    if (/^\d{2}:\d{2}:\d{2}$/.test(timeString)) {
        return timeString;
    }
    console.warn("Invalid time format provided:", timeString);
    return null; // Or throw error, depending on desired strictness
}

// Format datetime-local string to ISO string or return null
const formatDateTime = (dateTimeLocalString: string | null | undefined): string | null => {
    if (!dateTimeLocalString) return null;
    try {
        return new Date(dateTimeLocalString).toISOString();
    } catch (e) {
        console.error("Invalid date/time format:", dateTimeLocalString, e);
        return null;
    }
}


const submitTask = async () => {
  try {
    if (taskType.value === 'single') {
      // --- Create Single Task ---
      const taskData: TaskCreateRequest = {
        title: newTask.title,
        description: newTask.description || null,
        priority: newTask.priority,
        permissionGroup: newTask.permissionGroup || null,
        startTime: formatDateTime(newTask.startTime),
        endTime: formatDateTime(newTask.endTime),
        // createdBy will likely be set by the backend
      };
      await TaskService.createTask(taskData);

    } else {
      // --- Create Recurring Task ---
      const scheduledTaskData: ScheduledTaskCreateRequest = {
        template: {
          title: newTask.title,
          description: newTask.description || null,
          priority: newTask.priority,
          permissionGroup: newTask.permissionGroup || null,
          // createdBy will likely be set by the backend
        },
        frequency: newTask.frequency,
        // Ensure arrays are not empty before sending, or send null/undefined based on backend expectation
        daysOfWeek: newTask.daysOfWeek.length > 0 ? newTask.daysOfWeek : null,
        daysOfMonth: newTask.daysOfMonth.length > 0 ? newTask.daysOfMonth : null,
        startTime: formatTime(newTask.scheduleStartTime), // Format to HH:mm:ss
        endTime: formatTime(newTask.scheduleEndTime),     // Format to HH:mm:ss
        endTimeDaysAdd: newTask.endTimeDaysAdd,
      };
      await TaskService.createScheduledTask(scheduledTaskData);
    }

    emit('task-created'); // Notify parent component
    close(); // Close the dialog on success
  } catch (error) {
    console.error("Error creating task:", error);
    // TODO: Handle error (e.g., show an error message to the user)
  }
};

// TODO: Implement handleFileUpload if needed
// const handleFileUpload = (event: Event) => { ... };

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
    max-height: 90vh; // Limit height
    overflow-y: auto; // Allow scrolling

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px; // More space
      padding-bottom: 15px;
      border-bottom: 1px solid $bg-light;

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
          min-height: 80px; // Adjust height
        }

        // Style for radio button group
        .radio-group {
            display: flex;
            gap: 20px; // Space between radio buttons
            label {
                display: flex;
                align-items: center;
                gap: 5px;
                margin-bottom: 0; // Override default label margin
                cursor: pointer;
                color: $text-color;
            }
            .radio {
                 accent-color: $accent;
                 cursor: pointer;
            }
        }

         // Style for checkbox group
        .checkbox-group {
            display: flex;
            flex-wrap: wrap; // Allow wrapping
            gap: 10px; // Space between checkboxes
            background-color: $input-bg; // Background for the group
            padding: 10px;
            border-radius: $border-radius;
            border: 1px solid $input-border;

            label {
                display: flex;
                align-items: center;
                gap: 5px;
                margin-bottom: 0;
                cursor: pointer;
                color: $text-color;
                font-size: 0.85rem; // Smaller font for checkbox labels
            }
            .checkbox-inline {
                 accent-color: $accent;
                 cursor: pointer;
                 width: 16px;
                 height: 16px;
            }
        }

        small {
            display: block;
            margin-top: 5px;
            font-size: 0.75rem;
            color: #aaa;
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
</style>
