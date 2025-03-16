<script setup lang="ts">
import Ref from "@/components/util/Ref";
import Datepicker from "@vuepic/vue-datepicker";
import {computed, ref as vueRef} from "vue";
import "@vuepic/vue-datepicker/dist/main.css";

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
      <tr :class="$style['schedule-header-container']">
        <td
            :class="$style['schedule-employee-header-name']">Mitarbeiter
        </td>
        <td
            :class="$style['schedule-employee-header-balance']">Saldo
        </td>
        <td
            :class="$style['schedule-employee-header-illness']">K
        </td>
        <td
            :class="$style['schedule-date-entry']"
            v-for="day in getDaysInRange" :key="day.toISOString()">{{
            day.toLocaleDateString("de-DE", {
              day: "2-digit",
              month: "2-digit"
            })
          }}
        </td>
        <td
            :class="$style['schedule-employee-header-balance']">Saldo
        </td>
      </tr>
      <tr :class="$style['schedule-container']">
        <td :class="$style['schedule-employee-name-parent']">
          <div :class="$style['schedule-employee-name-container']">
            <div :class="$style['schedule-employee-name-entity']">Schneider, Melvin</div>
            <div :class="$style['schedule-employee-badgeid-entity']">131530</div>
          </div>
          <div :class="$style['schedule-employee-name-icon']">Info</div>
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
    </table>
  </div>
</template>

<style scoped lang="scss" module>
$min-width-name: 120px;
$min-width-balance: 20px;
$min-width-illness: 20px;
$min-width-shift: 20px;
$border-design: 0.1vh solid #555;

.parent {
  display: flex;
  flex-direction: column;

  .datepicker {
    margin: 25px 25px;
    width: fit-content;


    --dp-font-family: -apple-system, blinkmacsystemfont, "Segoe UI", roboto, oxygen, ubuntu, cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    --dp-border-radius: 5px;
    --dp-cell-border-radius: 5px;
    --dp-common-transition: all 0.1s ease-in;

    /*Sizing*/
    --dp-button-height: 25px;
    --dp-month-year-row-height: 25px; /*Height of the month-year select row */
    --dp-month-year-row-button-size: 25px; /*Specific height for the next/previous buttons*/
    --dp-button-icon-height: 10px; /*Icon sizing in buttons */
    --dp-cell-size: 25px; /*Width and height of calendar cell */
    --dp-cell-padding: 5px; /*Padding in the  cell*/
    --dp-common-padding: 10px; /*Common padding used */
    --dp-input-icon-padding: 35px; /*Padding on the left side of the input if icon is present */
    --dp-input-padding: 6px 30px 6px 12px; /*Padding in the  input*/
    --dp-menu-min-width: 220px; /*Adjust the min width of the  menu*/
    --dp-action-buttons-padding: 2px 5px; /*Adjust padding for the action buttons in action  row*/
    --dp-row-margin: 5px 0; /*Adjust the spacing between rows in the calendar */
    --dp-calendar-header-cell-padding: 0.5rem; /*Adjust padding in calendar header cells */
    --dp-two-calendars-spacing: 10px; /*Space between multiple calendars*/
    --dp-overlay-col-padding: 3px; /*Padding in the overlay column */
    --dp-time-inc-dec-button-size: 32px; /*Sizing for arrow buttons in the time picker */
    --dp-menu-padding: 6px 8px; /*Menu  padding*/

    /*Font sizes*/
    --dp-font-size: 0.8rem; /*Default font-size*/
    --dp-preview-font-size: 0.6rem; /*Font size of the date preview in the action row*/
    --dp-time-font-size: 0.6rem; /*Font size in the time picker*/

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
    margin: 20px 25px;
    background-color: #252525;
    border: $border-design;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);

    .schedule-header-container {
      font-weight: bold;

      .schedule-employee-header-name {
        padding: 8px 12px;
        border: $border-design;
        background: #333;
        color: white;
        min-width: $min-width-name;
      }

      .schedule-employee-header-balance {
        padding: 8px 12px;
        border: $border-design;
        background: #333;
        color: white;
        min-width: $min-width-balance;
      }

      .schedule-employee-header-illness {
        padding: 8px 12px;
        border: $border-design;
        background: #333;
        color: white;
        min-width: $min-width-illness;
      }

      .schedule-date-entry {
        padding: 6px 10px;
        border: $border-design;
        background: #333;
        color: white;
        min-width: $min-width-shift;
        justify-content: center;
      }
    }

    .schedule-container {
      font-size: smaller;

      .schedule-employee-name-parent {
        border: $border-design;
        text-align: right;

        .schedule-employee-name-container {
          display: flex;
          flex-direction: column;
          min-width: $min-width-name;

          .schedule-employee-name-entity {
          }
          .schedule-employee-badgeid-entity {

          }
        }
        .schedule-employee-name-icon {

        }
      }
      .schedule-employee-balance-container {
        text-align: right;
        min-width: $min-width-balance;
        border: $border-design;

        .schedule-employee-balance-entity {
          min-width: $min-width-balance;
        }
      }
      .schedule-employee-illness-container {
        text-align: right;
        min-width: $min-width-illness;
        border: $border-design;

        .schedule-employee-illness-entity {
          min-width: $min-width-illness;

        }
      }
      .schedule-employee-shift-container {
        min-width: $min-width-shift;
        border: $border-design;

        .schedule-employee-shift-header {

          min-width: $min-width-shift;
        }
        .schedule-employee-shift-entity {

          min-width: $min-width-shift;
        }
      }
    }
  }
}
</style>