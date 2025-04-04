<script setup lang="ts">
import Ref from "@/components/util/Ref";
import Datepicker from "@vuepic/vue-datepicker";
import {computed, ref as vueRef} from "vue";
import "@vuepic/vue-datepicker/dist/main.css";
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

interface Shift {
}

interface Employee {
  firstname: string,
  lastname: string
}

const employee1: Employee = new class implements Employee {
  firstname = 'Max';
  lastname = 'Mustermann';
}
const employeeList: Ref<Array<Employee>> = new Ref([employee1]);//TODO: Employee Array

const today = new Date();
const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
const selectedRange = vueRef([firstDayOfMonth, lastDayOfMonth]);
const getDaysInRange = computed(() => {
  const [start, end] = selectedRange.value;
  const days = [];
  let current = new Date(start);
  while (current <= end) {
    days.push(new Date(current));
    current.setDate(current.getDate() + 1);
  }
  return days;
});
</script>

<template>
  <div :class="$style['parent']">
    <Datepicker v-model="selectedRange"
                range six-weeks
                :month-picker="false"
                :enable-time-picker="false"
                :time-picker-inline="true"
                :min-date="new Date(2025, 0, 1)"
                :max-date="new Date(today.getFullYear()+5, 11, 31)"
                :year-range="[2025, today.getFullYear()+5]"
                :month-change-on-scroll="false"
                format="dd.MM.yyyy"
                locale="de"
                :dark="true"
                :class="$style['datepicker']"
    ></Datepicker>

    <table :class="$style['schedule-parent']">
      <thead>
        <tr :class="$style['schedule-header-container']">
          <th>
            <FontAwesomeIcon :icon="['fas', 'user']" :class="$style['header-icon']" />
            Mitarbeiter
          </th>
          <th>
            <FontAwesomeIcon :icon="['fas', 'balance-scale']" :class="$style['header-icon']" />
            Saldo
          </th>
          <th>
            <FontAwesomeIcon :icon="['fas', 'briefcase-medical']" :class="$style['header-icon']" />
            K
          </th>
          <th v-for="day in getDaysInRange" :key="day.toISOString()" :class="$style['schedule-date-entry']">
            {{ day.toLocaleDateString("de-DE", { day: "2-digit", month: "2-digit" }) }}
          </th>
          <th>
            <FontAwesomeIcon :icon="['fas', 'balance-scale']" :class="$style['header-icon']" />
            Saldo
          </th>
        </tr>
      </thead>
      <tbody>
        <tr :class="$style['schedule-container']">
          <td :class="$style['schedule-employee-name-parent']">
            <div :class="$style['schedule-employee-name-container']">
              <div :class="$style['schedule-employee-name-entity']">Schneider, Melvin</div>
              <div :class="$style['schedule-employee-badgeid-entity']">131530</div>
            </div>
            <div :class="$style['schedule-employee-name-icon']">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" />
            </div>
          </td>
          <td :class="$style['schedule-employee-balance-container']">
            <div :class="$style['schedule-employee-balance-entity']">22:02</div>
            <div :class="$style['schedule-employee-balance-entity']">-24:45</div>
            <div :class="$style['schedule-employee-balance-entity']">-02:43</div>
          </td>
          <td :class="$style['schedule-employee-illness-container']">
            <div :class="$style['schedule-employee-illness-entity']">8</div>
            <div :class="$style['schedule-employee-illness-entity']">0</div>
            <div :class="$style['schedule-employee-illness-entity']">8</div>
          </td>
          <td :class="$style['schedule-employee-shift-container']"
              v-for="day in getDaysInRange" :key="day.toISOString()">
            <div :class="$style['schedule-employee-shift-header']">E</div>
            <div :class="$style['schedule-employee-shift-entity']">09:00</div>
            <div :class="$style['schedule-employee-shift-entity']">18:00</div>
          </td>
          <td :class="$style['schedule-employee-balance-container']">
            <div :class="$style['schedule-employee-balance-entity']">22:02</div>
            <div :class="$style['schedule-employee-balance-entity']">-24:45</div>
            <div :class="$style['schedule-employee-balance-entity']">-02:43</div>
          </td>
        </tr>
        <!--  Example Row - Add more rows as needed -->
        <tr :class="[$style['schedule-container'], $style['schedule-row-even']]">
          <td :class="$style['schedule-employee-name-parent']">
            <div :class="$style['schedule-employee-name-container']">
              <div :class="$style['schedule-employee-name-entity']">Müller, Lisa</div>
              <div :class="$style['schedule-employee-badgeid-entity']">123456</div>
            </div>
            <div :class="$style['schedule-employee-name-icon']">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" />
            </div>
          </td>
          <td :class="$style['schedule-employee-balance-container']">
            <div>15:30</div>
            <div>-10:15</div>
            <div>05:15</div>
          </td>
          <td :class="$style['schedule-employee-illness-container']">
            <div>2</div>
            <div>0</div>
            <div>2</div>
          </td>
          <td :class="$style['schedule-employee-shift-container']"
              v-for="day in getDaysInRange" :key="day.toISOString()">
            <div :class="$style['schedule-employee-shift-header']">E</div>
            <div>10:00</div>
            <div>19:00</div>
          </td>
          <td :class="$style['schedule-employee-balance-container']">
            <div>15:30</div>
            <div>-10:15</div>
            <div>05:15</div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped lang="scss" module>
$min-width-name: 140px; // Further reduced
$min-width-balance: 60px; // Further reduced
$min-width-illness: 30px; // Further reduced
$min-width-shift: 50px;   // Further reduced
$border-design: 0.1vh solid #555;
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500;
$accent-hover: #b83200;
$border-radius: 5px;
$transition-speed: 0.3s;

.parent {
  display: flex;
  flex-direction: column;
  align-items: center;

  .datepicker {
    margin: 10px; // Further reduced margin
    width: fit-content;

    --dp-font-family: -apple-system, blinkmacsystemfont, "Segoe UI", roboto, oxygen, ubuntu, cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    --dp-border-radius: 4px; // Slightly smaller
    --dp-cell-border-radius: 4px; // Slightly smaller
    --dp-common-transition: all 0.1s ease-in;

    /*Sizing*/
    --dp-button-height: 20px; // Further reduced
    --dp-month-year-row-height: 20px; // Further reduced
    --dp-month-year-row-button-size: 20px; // Further reduced
    --dp-button-icon-height: 7px; // Further reduced
    --dp-cell-size: 20px; // Further reduced
    --dp-cell-padding: 3px; // Further reduced
    --dp-common-padding: 6px; // Further reduced
    --dp-input-icon-padding: 25px; // Further reduced
    --dp-input-padding: 4px 20px 4px 8px; // Further reduced
    --dp-menu-min-width: 180px; // Further reduced
    --dp-action-buttons-padding: 1px 3px; // Further reduced
    --dp-row-margin: 3px 0; // Further reduced
    --dp-calendar-header-cell-padding: 0.3rem; // Further reduced
    --dp-two-calendars-spacing: 6px; // Further reduced
    --dp-overlay-col-padding: 1px; // Further reduced
    --dp-time-inc-dec-button-size: 24px; // Further reduced
    --dp-menu-padding: 4px 5px; // Further reduced

    /*Font sizes*/
    --dp-font-size: 0.6rem; // Further reduced
    --dp-preview-font-size: 0.4rem; // Further reduced
    --dp-time-font-size: 0.4rem; // Further reduced

    /*Transitions*/

    --dp-animation-duration: 0.1s !important;
    --dp-menu-appear-transition-timing: cubic-bezier(.4, 0, 1, 1) !important;
    --dp-transition-timing: ease-out !important;
    --dp-background-color: #1e1e1e !important;
    --dp-text-color: white !important;
    --dp-hover-color: #484848 !important;
    --dp-hover-text-color: white !important;
    --dp-primary-color: #ff4500 !important;
    --dp-primary-disabled-color: #e53935 !important;
    --dp-primary-text-color: white !important;
    --dp-secondary-color: #a9a9a9 !important;
    --dp-border-color: #333 !important;
    --dp-menu-border-color: #333 !important;
    --dp-border-color-hover: #aaaeb7 !important;
    --dp-border-color-focus: #ff4500 !important;
    --dp-disabled-color: #737373 !important;
    --dp-disabled-color-text: #d0d0d0 !important;
    --dp-scroll-bar-background: #212121 !important;
    --dp-scroll-bar-color: #484848 !important;
    --dp-success-color: #00701a !important;
    --dp-success-color-disabled: #428f59 !important;
    --dp-icon-color: #959595 !important;
    --dp-danger-color: #e53935 !important;
    --dp-marker-color: #e53935 !important;
    --dp-tooltip-color: #3e3e3e !important;
    --dp-highlight-color: rgb(0 92 178 / 20%) !important;
    --dp-range-between-dates-background-color: #ff4500 !important;
    --dp-range-between-dates-text-color: white !important;
    --dp-range-between-border-color: #e53935 !important;
  }

  .schedule-parent {
    font-size: smaller;
    text-align: center;
    margin: 10px 15px; // Further reduced margin
    background-color: $bg-medium;
    border: $border-design;
    border-radius: $border-radius;
    width: 90%;

    .schedule-header-container {
      font-weight: bold;
      background-color: $bg-light;
      color: $text-color;

      th {
        padding: 6px 8px; // Further reduced padding
        border: $border-design;
        min-width: $min-width-shift;
        white-space: nowrap;
        .header-icon {
          margin-right: 3px; // Further reduced margin
          font-size: 0.8em; // Reduce icon size
        }
      }
    }

    .schedule-container {
      &:hover {
        background-color: lighten($bg-medium, 10%);
      }
      &.schedule-row-even {
        background-color: darken($bg-medium, 5%);
      }

      td {
        padding: 4px 6px; // Further reduced padding
        border: $border-design;
        color: $text-color;
        vertical-align: top;
        font-size: 0.7rem; // Reduce font size
      }

      .schedule-employee-name-parent {
        text-align: right;
        min-width: $min-width-name;

        .schedule-employee-name-container {
          display: flex;
          flex-direction: column;
          text-align: left;

          .schedule-employee-name-entity, .schedule-employee-badgeid-entity {
            padding: 1px 0; // Further reduced padding
          }
        }

        .schedule-employee-name-icon {
          margin-left: 5px; // Further reduced margin
          color: $accent;
          font-size: 0.8em; // Reduce icon size
        }
      }

      .schedule-employee-balance-container {
        text-align: right;
        min-width: $min-width-balance;
        display: flex;
        flex-direction: column;
        .schedule-employee-balance-entity{
          padding: 1px 0; // Further reduced padding
        }
      }

      .schedule-employee-illness-container {
        text-align: right;
        min-width: $min-width-illness;
        display: flex;
        flex-direction: column;
        .schedule-employee-illness-entity{
          padding: 1px 0; // Further reduced padding
        }
      }

      .schedule-employee-shift-container {
        min-width: $min-width-shift;
        text-align: center;

        .schedule-employee-shift-header {
          font-weight: bold;
          padding-bottom: 0px; // Further reduced padding
        }
      }
    }
  }
}

// Media query for smaller screens
@media (max-width: 768px) {
  .schedule-parent {
    font-size: x-small;

    .schedule-header-container, .schedule-container {
      th, td {
        padding: 2px 4px; // Even smaller padding on very small screens
      }
    }
  }
}
</style>
