<script setup lang="ts">

import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
</script>

<template>
  <SidebarComponent site="product-expire-management"/>
  <div :class="$style['settings-container']">
    <div :class="$style['product-count']">34 / 105 Artikel</div>
    <div :class="$style['settings-button']">Einstellungen
      <FontAwesomeIcon icon="fa-gear"/>
    </div>
  </div>
  <div :class="$style['product-category-container']">
    <div :class="$style['product-category-entry']">
      <div :class="$style['product-category-name']">Produkt-Kategorie 1</div>
      <div :class="$style['product-category-count']">(34 / 55)</div>
      <FontAwesomeIcon icon="fa-circle-down" :class="$style['product-category-arrow']"/>
    </div>
    <div :class="$style['product-container']">
      <tr :class="$style['product-entry-container']">
        <td :class="$style['product-id']">#502433</td>
        <td :class="$style['product-name']">Coca-Cola 220ml</td>
        <td :class="$style['product-date']">AUSSORTIEREN</td>
        <td :class="$style['product-newdate']">
          <FontAwesomeIcon
              icon="fa-calendar-days"/>
        </td>
      </tr>
      <tr :class="$style['product-entry-container']">
        <td :class="$style['product-id']">#502433</td>
        <td :class="$style['product-name']">Bier 330ml</td>
        <td :class="$style['product-date']">REDUZIERT</td>
        <td :class="$style['product-newdate']">
          <FontAwesomeIcon
              icon="fa-calendar-days"/>
        </td>
      </tr>
    </div>
    <div :class="$style['product-category-entry']">
      <div :class="$style['product-category-name']">Produkt-Kategorie 2</div>
      <div :class="$style['product-category-count']">(34 / 55)</div>
      <FontAwesomeIcon icon="fa-circle-down" :class="$style['product-category-arrow']"/>
    </div>
    <div :class="$style['product-container']">
      <tr :class="$style['product-entry-container']">
        <td :class="$style['product-id']">#502433</td>
        <td :class="$style['product-name']">Äppelwein 500ml</td>
        <td :class="$style['product-date']">REDUZIEREN</td>
        <td :class="$style['product-newdate']">
          <FontAwesomeIcon
              icon="fa-calendar-days"/>
        </td>
      </tr>
    </div>
  </div>
</template>


<style lang="scss" module>
.settings-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  border-radius: 8px;
  margin: 25px 25px 0 25px;

  .product-count {
    background-color: #252525;
    color: #fff;
    font-size: 16px;
    padding: 10px 10px 10px 10px;
    border-radius: 4px;
  }

  .settings-button {
    background-color: #252525;
    color: #fff;
    border-radius: 4px;
    padding: 10px 10px 10px 10px;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &:hover {
      background-color: #2d2d2d;
    }
  }
}

.product-category-container {
  display: flex;
  flex-direction: column;
  margin: 25px 0 0 25px;

  .product-category-entry {
    display: flex;
    flex-direction: row;
    background-color: #252525;
    padding: 12px;
    max-width: fit-content;
    border-radius: 6px;
    margin-top: 20px;

    .product-category-name {
      color: #fff;
      font-size: 18px;
      font-weight: bold;
    }

    .product-category-count {
      color: #bbb;
      font-size: 14px;
      margin: 0 50px 0 50px;
    }
  }

  .product-container {
    margin: 10px 0 0 25px;

    .product-entry-container {
      background-color: #2c2f38;
      border-radius: 6px;
      transition: background-color 0.3s ease;

      &:hover {
        background-color: #3e434c;
      }

      .product-id {
        color: #aaa;
        font-size: 14px;
        padding: 10px;
      }

      .product-name {
        color: #fff;
        font-size: 16px;
        padding: 10px;
      }

      .product-date {
        color: #ccc;
        font-size: 14px;
        margin: 0 80px 0 0;
        cursor: pointer;
        background-color: #2c2f38;
        padding: 10px;
      }

      .product-newdate {
        color: #ccc;
        font-size: 14px;
        cursor: pointer;
        padding: 10px;
      }
    }
  }
}
</style>