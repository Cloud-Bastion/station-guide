<template>
  <div :class="$style['create-task-modal']">
    <div :class="$style['modal-content']">
      <div :class="$style['modal-header']">
        <h2>Neue Aufgabe erstellen</h2>
        <button @click="close" :class="$style['close-button']" aria-label="Schließen">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>
      <div :class="$style['modal-body']">

        <!-- Task Type Selection (Tabs) -->
        <div :class="$style['form-group']">
          <label :class="$style['label-bold']">Aufgabentyp:</label>
          <div :class="$style['task-type-tabs']">
            <button
                :class="[$style['tab-button'], taskType === 'single' ? $style['active-tab'] : '']"
                @click="taskType = 'single'">
              Einmalig
            </button>
            <button
                :class="[$style['tab-button'], taskType === 'recurring' ? $style['active-tab'] : '']"
                @click="taskType = 'recurring'">
              Wiederkehrend
            </button>
          </div>
        </div>

        <hr :class="$style['divider']">

        <!-- Common Fields -->
        <div :class="$style['form-group']">
          <label for="task-title">Titel <span :class="$style['required']">*</span></label>
          <input type="text" id="task-title" v-model="newTask.title" :class="$style['input']" required/>
        </div>
        <div :class="$style['form-group']">
          <label for="task-description">Beschreibung</label>
          <textarea id="task-description" v-model="newTask.description" :class="$style['textarea']" rows="3"></textarea>
        </div>
         <div :class="$style['form-group']">
          <label for="task-priority">Priorität</label>
          <select id="task-priority" v-model="newTask.priority" :class="$style['select']">
            <option :value="1">Niedrig</option>
            <option :value="2">Normal</option>
            <option :value="3">Hoch</option>
            <option :value="4">Sehr hoch</option>
          </select>
        </div>
        <div :class="$style['form-group']">
          <label for="task-permission-group">Berechtigungsgruppe (Optional)</label>
          <input type="text" id="task-permission-group" v-model="newTask.permissionGroup" :class="$style['input']"/>
        </div>

        <!-- Fields for Single Task -->
        <transition name="fade-section">
          <div v-if="taskType === 'single'" :class="$style['task-type-section']">
            <hr :class="$style['divider-subtle']">
            <h3 :class="$style['section-title']">Einmalige Aufgabe Details</h3>
            <div :class="$style['form-group']">
              <label for="task-start-time">Startzeit (Optional)</label>
              <input type="datetime-local" id="task-start-time" v-model="newTask.startTime" :class="$style['input']"/>
            </div>
            <div :class="$style['form-group']">
              <label for="task-end-time">Fälligkeitsdatum (Optional)</label>
              <input type="datetime-local" id="task-end-time" v-model="newTask.endTime" :class="$style['input']"/>
            </div>
          </div>
        </transition>

        <!-- Fields for Recurring Task -->
        <transition name="fade-section">
          <div v-if="taskType === 'recurring'" :class="[$style['task-type-section'], $style['recurring-options']]">
             <hr :class="$style['divider-subtle']">
             <h3 :class="$style['section-title']">Wiederkehrende Aufgabe Details</h3>
            <div :class="$style['form-group']">
              <label for="task-frequency">Frequenz <span :class="$style['required']">*</span></label>
              <select id="task-frequency" v-model="newTask.frequency" :class="$style['select']" required>
                <option value="DAILY">Täglich</option>
                <option value="WEEKLY">Wöchentlich</option>
                <option value="MONTHLY">Monatlich</option>
                <!-- <option value="YEARLY">Jährlich</option> --> {/* Removed YEARLY as per request */}
              </select>
            </div>
            <!-- Show Days of Week only if frequency is WEEKLY -->
            <div :class="$style['form-group']" v-if="newTask.frequency === 'WEEKLY'">
              <label>Tage der Woche</label>
              <div :class="$style['checkbox-group']">
                <label v-for="day in weekDays" :key="day.value" :class="$style['checkbox-label']">
                  <input type="checkbox" :value="day.value" v-model="newTask.daysOfWeek" :class="$style['checkbox-inline']"/>
                  <span :class="$style['checkbox-custom']"></span>
                  <span :class="$style['checkbox-text']">{{ day.label }}</span>
                </label>
              </div>
            </div>
            <!-- Show Days of Month only if frequency is MONTHLY -->
            <div :class="$style['form-group']" v-if="newTask.frequency === 'MONTHLY'">
              <label>Tage des Monats</label>
               <div :class="$style['checkbox-group']">
                   <label v-for="day in monthDays" :key="day.value" :class="$style['checkbox-label']">
                       <input type="checkbox" :value="day.value" v-model="newTask.daysOfMonth" :class="$style['checkbox-inline']"/>
                       <span :class="$style['checkbox-custom']"></span>
                       <span :class="$style['checkbox-text']">{{ day.label }}</span>
                   </label>
               </div>
               <small>32 = Letzter Tag des Monats</small>
            </div>
            <div :class="$style['form-group']">
              <label for="task-schedule-start-time">Startzeit (HH:mm)</label>
              <input type="time" id="task-schedule-start-time" v-model="newTask.scheduleStartTime" :class="$style['input']"/>
            </div>
            <div :class="$style['form-group']">
              <label for="task-schedule-end-time">Endzeit (Optional, HH:mm)</label>
              <input type="time" id="task-schedule-end-time" v-model="newTask.scheduleEndTime" :class="$style['input']"/>
            </div>
             <div :class="$style['form-group']">
              <label for="task-end-time-days-add">Endzeit Tage hinzufügen (Optional)</label>
              <input type="number" id="task-end-time-days-add" v-model.number="newTask.endTimeDaysAdd" :class="$style['input']" min="0"/>
               <small>Anzahl der Tage, die zur Startzeit addiert werden, um die Endzeit zu bestimmen (wenn keine Endzeit angegeben ist).</small>
            </div>
          </div>
        </transition>

        <!-- File Upload (Common) -->
        <!-- TODO: Implement actual file upload logic if needed -->
        <!--
        <div :class="$style['form-group']">
          <label for="task-files">Dateien:</label>
          <input type="file" id="task-files" multiple @change="handleFileUpload" :class="$style['input-file']"/>
        </div>
        -->

        <button @click="submitTask" :class="$style['create-button']">
            <FontAwesomeIcon icon="check" :class="$style['button-icon']"/>
            Aufgabe erstellen
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, defineEmits, reactive} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import TaskService, {ScheduledTask, Task} from "@/service/TaskService";

const emit = defineEmits(['close', 'task-created']);

const taskType = ref<'single' | 'recurring'>('single'); // Default to single task

const newTask = reactive({
  // Common fields
  title: '',
  description: '',
  priority: 2, // Default priority: Normal
  permissionGroup: '',
  // files: [] as string[], // Store file names or URLs for now - TODO: Implement upload

  // Single Task fields
  startTime: '', // For datetime-local input
  endTime: '',   // For datetime-local input

  // Recurring Task fields
  frequency: 'DAILY', // Default frequency to DAILY
  daysOfWeek: [] as number[],
  daysOfMonth: [] as number[],
  scheduleStartTime: '', // For time input "HH:mm"
  scheduleEndTime: '',   // For time input "HH:mm"
  endTimeDaysAdd: 0,
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
const formatTime = (timeString: string | undefined): string => {
    if (!timeString) return "";
    // Basic check, assumes "HH:mm" input from <input type="time">
    if (/^\d{2}:\d{2}$/.test(timeString)) {
        return `${timeString}:00`;
    }
    // If already includes seconds, return as is (or handle other formats if needed)
    if (/^\d{2}:\d{2}:\d{2}$/.test(timeString)) {
        return timeString;
    }
    console.warn("Invalid time format provided:", timeString);
    return ""; // Or throw error, depending on desired strictness
}

// Format datetime-local string to ISO string or return null
const formatDateTime = (dateTimeLocalString: string | undefined): string => {
    if (!dateTimeLocalString) return "";
    try {
        // Ensure the browser-provided string is correctly interpreted as local time before converting to ISO
        const date = new Date(dateTimeLocalString);
        if (isNaN(date.getTime())) throw new Error("Invalid date");
        return date.toISOString(); // Standard ISO string (UTC) - Backend needs to handle this correctly
    } catch (e) {
        console.error("Invalid date/time format:", dateTimeLocalString, e);
        return "";
    }
}


const submitTask = async () => {
  // Basic validation (Title is required for both)
  if (!newTask.title) {
      alert("Bitte geben Sie einen Titel für die Aufgabe an.");
      return;
  }
  // Frequency is required for recurring tasks
  if (taskType.value === 'recurring' && !newTask.frequency) {
      alert("Bitte geben Sie eine Frequenz für die wiederkehrende Aufgabe an.");
      return;
  }


  try {
    if (taskType.value === 'single') {
      // --- Create Single Task ---
      const taskData: Task = {
        title: newTask.title,
        description: newTask.description,
        priority: newTask.priority,
        permissionGroup: newTask.permissionGroup,
        startTime: formatDateTime(newTask.startTime),
        endTime: formatDateTime(newTask.endTime),
        id: undefined,
        createdBy: undefined,
        completed: false,
        isTemplate: false
        // createdBy will likely be set by the backend
      };
      await TaskService.createTask(taskData);

    } else {
      // --- Create Recurring Task ---
      const scheduledTaskData: ScheduledTask = {
        id: undefined,
        template: {
          id: undefined,
          title: newTask.title,
          description: newTask.description,
          priority: newTask.priority,
          permissionGroup: newTask.permissionGroup,
          createdBy: undefined,
          startTime: newTask.startTime,
          endTime: newTask.endTime,
          completed: false,
          isTemplate: true
        },
        frequency: newTask.frequency, // Already uppercase from select value
        // Send days only if relevant for the frequency
        daysOfWeek: newTask.frequency === 'WEEKLY' && newTask.daysOfWeek.length > 0 ? newTask.daysOfWeek.sort((a, b) => a - b) : [],
        daysOfMonth: newTask.frequency === 'MONTHLY' && newTask.daysOfMonth.length > 0 ? newTask.daysOfMonth.sort((a, b) => a - b) : [],
        startTime: formatTime(newTask.scheduleStartTime), // Format to HH:mm:ss
        endTime: formatTime(newTask.scheduleEndTime),     // Format to HH:mm:ss
        endTimeDaysAdd: newTask.endTimeDaysAdd,
        lastCreatedTask: undefined
      };
      await TaskService.createScheduledTask(scheduledTaskData);
    }

    emit('task-created'); // Notify parent component
    close(); // Close the dialog on success
  } catch (error) {
    console.error("Error creating task:", error);
    // TODO: Handle error (e.g., show an error message to the user)
    alert(`Fehler beim Erstellen der Aufgabe: ${error instanceof Error ? error.message : 'Unbekannter Fehler'}`);
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
$text-color-light: #b0b0b0;
$accent: #ff4500; // Red accent
$accent-hover: #b83200; // Darker red for hover
$border-radius: 6px; // Slightly larger radius
$transition-speed: 0.2s;
$input-bg: #333;
$input-border: #555;
$input-focus-border: $accent;
$required-color: $accent;
$checkbox-size: 18px; // Size for custom checkbox

// --- Scrollbar ---
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: $bg-light;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb {
  background-color: #555;
  border-radius: 4px;
  border: 2px solid $bg-light;
}
::-webkit-scrollbar-thumb:hover {
  background-color: #777;
}

.create-task-modal {
  position: fixed;
  inset: 0; // replaces top, left, width, height
  background-color: rgba(0, 0, 0, 0.75); // Darker overlay
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px; // Padding for smaller screens

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    padding: 0; // Remove padding, handle inside header/body
    width: 100%;
    max-width: 650px; // Wider modal
    position: relative;
    display: flex;
    flex-direction: column;
    max-height: 90vh; // Limit height

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 18px 25px; // Adjusted padding
      border-bottom: 1px solid $bg-light;

      h2 {
        color: $text-color;
        font-size: 1.5rem; // Slightly smaller title
        font-weight: 600;
        margin: 0;
      }

      .close-button {
        background: none;
        border: none;
        color: $text-color-light;
        cursor: pointer;
        font-size: 1.6rem; // Larger close icon
        padding: 5px; // Click area
        line-height: 1; // Ensure icon is centered
        transition: color $transition-speed ease;

        &:hover {
          color: $text-color;
        }
      }
    }

    .modal-body {
      padding: 25px; // Padding for the body content
      overflow-y: auto; // Enable scrolling for content
      display: flex;
      flex-direction: column;
      gap: 20px; // Consistent gap between form groups

      .form-group {
        display: flex;
        flex-direction: column;
        gap: 8px; // Space between label and input

        label {
          color: $text-color;
          font-size: 0.9rem;
          font-weight: 500; // Slightly bolder labels
        }

        .label-bold {
            font-weight: 600;
        }

        .required {
            color: $required-color;
            margin-left: 2px;
        }

        .input, .textarea, .select {
          width: 100%;
          padding: 12px 15px; // Comfortable padding
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
          // Style placeholder text
          &::placeholder {
              color: #888;
          }
        }

        // --- Date/Time Input Styling ---
        input[type="datetime-local"], input[type="time"] {
            // Basic appearance matching other inputs
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;

            // Style the calendar/clock icon for WebKit browsers
            &::-webkit-calendar-picker-indicator {
                background-color: $text-color-light; // Use a light color for the icon
                border-radius: 3px;
                padding: 3px;
                cursor: pointer;
                opacity: 0.7;
                transition: opacity $transition-speed ease;

                &:hover {
                    opacity: 1;
                }
            }
            // Try to make the icon visible in dark mode (might not work perfectly)
             color-scheme: dark; // Hint for browser styling
        }

        .textarea {
          resize: vertical;
          min-height: 80px;
        }

        .select {
            appearance: none; // Remove default arrow
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23$text-color-light'%3E%3Cpath fill-rule='evenodd' d='M4.22 6.22a.75.75 0 0 1 1.06 0L8 8.94l2.72-2.72a.75.75 0 1 1 1.06 1.06l-3.25 3.25a.75.75 0 0 1-1.06 0L4.22 7.28a.75.75 0 0 1 0-1.06Z' clip-rule='evenodd' /%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 15px center;
            background-size: 16px 16px;
            padding-right: 40px; // Make space for the custom arrow
        }

        // --- Task Type Tabs ---
        .task-type-tabs {
            display: flex;
            background-color: $input-bg;
            border: 1px solid $input-border;
            border-radius: $border-radius;
            overflow: hidden; // Clip the button borders inside

            .tab-button {
                flex: 1; // Each button takes equal width
                padding: 10px 15px;
                background: none;
                border: none;
                color: $text-color-light;
                cursor: pointer;
                transition: background-color $transition-speed ease, color $transition-speed ease;
                font-size: 0.95rem;
                text-align: center;

                // Add a subtle border between tabs
                &:not(:last-child) {
                    border-right: 1px solid $input-border;
                }

                &:hover {
                    background-color: darken($input-bg, 5%);
                    color: $text-color;
                }
            }

            .active-tab {
                background-color: $accent;
                color: white;
                font-weight: 600;

                &:hover {
                    background-color: $accent-hover;
                }
            }
        }


         // --- Custom Checkbox Group ---
        .checkbox-group {
            display: grid; // Use grid for better alignment
            grid-template-columns: repeat(auto-fit, minmax(60px, 1fr)); // Responsive columns
            gap: 10px 15px; // Row and column gap
            background-color: $input-bg;
            padding: 12px 15px;
            border-radius: $border-radius;
            border: 1px solid $input-border;

            .checkbox-label {
                display: inline-flex; // Use inline-flex for alignment
                align-items: center;
                gap: 8px; // Gap between custom checkbox and text
                cursor: pointer;
                color: $text-color;
                font-size: 0.9rem;
                position: relative; // Needed for absolute positioning of ::before/::after
            }

            // Hide the default checkbox visually but keep it accessible
            .checkbox-inline {
                 position: absolute;
                 opacity: 0;
                 cursor: pointer;
                 height: 0;
                 width: 0;
            }

            // The custom checkbox box
            .checkbox-custom {
                display: inline-block;
                width: $checkbox-size;
                height: $checkbox-size;
                background-color: $input-bg;
                border: 1px solid $input-border;
                border-radius: 4px;
                transition: background-color $transition-speed ease, border-color $transition-speed ease;
                position: relative; // For checkmark positioning
                flex-shrink: 0; // Prevent shrinking
            }

            // Style the custom checkbox when the hidden input is focused (for accessibility)
            .checkbox-inline:focus + .checkbox-custom {
                outline: none;
                box-shadow: 0 0 0 2px rgba($input-focus-border, 0.4);
                border-color: $input-focus-border;
            }

            // Style the custom checkbox when checked
            .checkbox-inline:checked + .checkbox-custom {
                background-color: $accent;
                border-color: $accent;
            }

            // The checkmark using ::after pseudo-element
            .checkbox-custom::after {
                content: "";
                position: absolute;
                display: none; // Hidden by default
                left: 5px; // Position checkmark
                top: 1px; // Position checkmark
                width: 5px;
                height: 10px;
                border: solid white;
                border-width: 0 2px 2px 0;
                transform: rotate(45deg);
            }

            // Show the checkmark when checked
            .checkbox-inline:checked + .checkbox-custom::after {
                display: block;
            }

            .checkbox-text {
                user-select: none;
            }
        }

        small {
            display: block;
            margin-top: 5px;
            font-size: 0.8rem;
            color: $text-color-light;
            line-height: 1.4;
        }
      }

      .divider {
          border: none;
          height: 1px;
          background-color: $bg-light;
          margin: 5px 0; // Reduced margin for main divider
      }
      .divider-subtle {
          border: none;
          height: 1px;
          background-color: darken($bg-light, 5%);
          margin: 15px 0;
      }

      .task-type-section {
          display: flex;
          flex-direction: column;
          gap: 20px; // Consistent gap within sections
      }

      .recurring-options {
          border: 1px solid $input-border;
          border-radius: $border-radius;
          padding: 20px;
          margin-top: 10px; // Space above the box
          background-color: darken($bg-medium, 2%); // Slightly different background
      }

      .section-title {
          font-size: 1.1rem;
          font-weight: 600;
          color: $text-color;
          margin: 0 0 10px 0; // Adjust margin
      }


      .create-button {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        padding: 12px 25px; // Generous padding
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease, transform 0.1s ease;
        width: 100%;
        margin-top: 15px; // Space above button
        font-size: 1rem;
        font-weight: 600;

        &:hover {
          background-color: $accent-hover;
        }
        &:active {
            transform: scale(0.98); // Click effect
        }

        .button-icon {
            font-size: 0.9em;
        }
      }
    }
  }
}

// --- Transitions ---
.fade-section-enter-active,
.fade-section-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
  max-height: 1000px; // Set a large max-height for transition
}

.fade-section-enter-from,
.fade-section-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  max-height: 0;
  overflow: hidden;
  margin-top: 0 !important; // Prevent margin jump during transition
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  border-width: 0 !important;
}

// --- Media Query for smaller screens ---
@media (max-width: 768px) {
    .create-task-modal .modal-content {
        max-width: 95%;
        .modal-header {
            padding: 15px 20px;
            h2 { font-size: 1.3rem; }
        }
        .modal-body {
            padding: 20px;
            gap: 15px; // Reduce gap slightly
            .form-group {
                .input, .textarea, .select { padding: 10px 12px; }
                .task-type-tabs .tab-button { font-size: 0.9rem; padding: 8px 10px; } // Adjust tab button size
                .checkbox-group {
                    grid-template-columns: repeat(auto-fit, minmax(50px, 1fr)); // Adjust columns for smaller screens
                    gap: 8px 12px;
                }
            }
            .create-button { padding: 10px 20px; font-size: 0.95rem; }
        }
    }
}
</style>
