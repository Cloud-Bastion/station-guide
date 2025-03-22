<script setup lang="ts">
import ExpireProductService, {
  ExpireProduct,
  ExpireProductCategory,
  ExpireProductState
} from "@/service/ExpireProductService";
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {onMounted} from "vue";
import Ref from "@/components/util/Ref";
import Datepicker from "@vuepic/vue-datepicker";

const otherCategorie: ExpireProductCategory = new class implements ExpireProductCategory {
  name = "Andere";
  reduceProductTime: undefined;
  showProducts = true;
}

const categories: Ref<Map<string, ExpireProductCategory>> = new Ref<Map<string, ExpireProductCategory>>(new Map<string, ExpireProductCategory>());
const expiredProducts: Ref<Map<ExpireProductCategory, ExpireProduct[]>> = new Ref<Map<ExpireProductCategory, ExpireProduct[]>>(new Map<ExpireProductCategory, ExpireProduct[]>());

const updateLastChange = (product: ExpireProduct): void => {
  if (ExpireProductService.getState(product) === ExpireProductState.REDUCED) {
    return;
  }

  if (ExpireProductService.getState(product) === ExpireProductState.REDUCE) {
    product.lastUpdateDate = new Date(Date.now());
    ExpireProductService.updateLastChange(product)
  }

  if (ExpireProductService.getState(product) === ExpireProductState.SORT_OUT) {
    product.expireDate = null
    ExpireProductService.updateExpireDate(product);
  }
};

const countCategorySuccess = (category: ExpireProductCategory): number => {
  const currentDate = new Date(Date.now());
  let count: number = 0;
  expiredProducts.value.get(categories.value.get(category.name)!)!.forEach(product => {
    if (product.lastUpdateDate !== undefined && product.lastUpdateDate !== null && ExpireProductService.compareDates(product.lastUpdateDate, currentDate)) {
      count = count + 1
    }
  })
  return count;
}

const countAllSuccess = (): number => {
  const currentDate = new Date(Date.now());
  let count: number = 0;

  for (let value of expiredProducts.value.values()) {
    value.forEach(product => {
      if (product.lastUpdateDate !== undefined && product.lastUpdateDate !== null && ExpireProductService.compareDates(product.lastUpdateDate, currentDate)) {
        count = count + 1
      }
    });
  }

  return count;
}

const getDropDownIcon = (bool: boolean): string => {
  return bool ? "fa-circle-down" : "fa-circle-right";
}

onMounted(async () => {
  try {
    const products = await ExpireProductService.getExpiringItems();

    products.forEach(product => {
      if (product.category === undefined || product.category === null) {
        product.category = otherCategorie
      }

      if (!categories.value.has(product.category.name)) {
        product.category.showProducts = true
        categories.value.set(product.category.name, product.category)
      }

      if (!expiredProducts.value.has(categories.value.get(product.category.name)!)) {
        expiredProducts.value.set(categories.value.get(product.category.name)!, []);
      }

      expiredProducts.value.get(categories.value.get(product.category.name)!)!.push(product)
    });

    for (let value of expiredProducts.value.keys()) {
      console.log(value.name + " registered as category")
    }
  } catch (error) {
    console.error("Fehler beim Laden der Artikel:", error)
  }
})
</script>

<template>
  <SidebarComponent site="product-expire-management"/>
  <div :class="$style['settings-container']">
    <div :class="$style['product-count']">{{ countAllSuccess() }} /
      {{ Array.from(expiredProducts.value.values()).reduce((sum, products) => sum + products.length, 0) }} Artikel
    </div>
    <div :class="$style['settings-button']">Einstellungen
      <FontAwesomeIcon icon="fa-gear"/>
    </div>
  </div>
  <div :class="$style['product-parent']">
    <table :class="$style['product-container']">
      <template v-for="(category, index) in expiredProducts.value.keys()"
                :key="index">
        <tr :class="$style['product-category-parent']">
          <td :class="$style['product-category-entry']" colspan="4">
            <div :class="$style['product-category-entry-wrapper']">
              <div :class="$style['product-category-name']">{{ category.name }}:</div>
              <div :class="$style['product-category-count']">({{ countCategorySuccess(category) }} /
                {{ expiredProducts.value.get(category)?.length }})
              </div>
              <FontAwesomeIcon :icon="getDropDownIcon(category.showProducts)" :class="$style['product-category-arrow']"
                               @click="category.showProducts=!category.showProducts"/>
            </div>
          </td>
        </tr>
        <tr :class="$style['product-entry-container']" v-for="(product, index) in expiredProducts.value.get(category)"
            v-if="category.showProducts">
          <td :class="$style['product-id']">#{{ product.productId }}</td>
          <td :class="$style['product-name']">{{ product.name }}</td>
          <td :class="$style['product-date']" @click="updateLastChange(product);">
            {{ ExpireProductService.getState(product) }}
          </td>
          <td :class="$style['product-newdate']">
            <Datepicker v-model="product.expireDate"
                        six-weeks
                        :month-picker="false"
                        :enable-time-picker="false"
                        :time-picker-inline="true"
                        :min-date="new Date(Date.now())"
                        :month-change-on-scroll="false"
                        format="dd.MM.yyyy"
                        locale="de"
                        @update:model-value="() => ExpireProductService.updateExpireDate(product)"
                        :dark="true"
                        :class="$style['datepicker']"
            />
          </td>
        </tr>
      </template>
    </table>
  </div>
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Changed to red
$accent-hover: #b83200; // Darker red for hover
$border-radius: 10px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;
$input-focus: #ff4500; // Red focus
$border-design: 0.1vh solid #555;

.settings-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  border-radius: 8px;
  margin: 25px 25px 0 25px;

  .product-count {
    background-color: $bg-medium;
    color: $text-color;
    font-size: 16px;
    padding: 10px;
    border-radius: 4px;
  }

  .settings-button {
    background-color: $bg-medium;
    color: $text-color;
    border-radius: 4px;
    padding: 10px;
    cursor: pointer;
    transition: background-color $transition-speed ease;

    &:hover {
      background-color: $bg-light;
    }
  }
}

.product-parent {
  display: flex;
  flex-direction: column;
  max-width: fit-content;

  .product-container {
    margin: 25px 0 0 25px;
    border: $border-design;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    background-color: $bg-medium;

    .product-category-parent {
      padding: 5px;

      .product-category-entry {

        .product-category-entry-wrapper {
          display: flex;
          flex-direction: row;
          background-color: $bg-light;
          padding: 12px;
          margin-top: 5px;
          border-radius: 6px;

          .product-category-name {
            color: $text-color;
            font-size: 18px;
            font-weight: bold;
          }

          .product-category-count {
            color: #bbb;
            font-size: 14px;
            margin: 0 50px 0 50px;
          }
          .product-category-arrow {
            cursor: pointer;
          }
        }
      }
    }

    .product-entry-container {
      background-color: $bg-medium;
      border-radius: 6px;
      transition: background-color $transition-speed ease;

      &:hover {
        background-color: $bg-light;
      }

      .product-id {
        color: #aaa;
        font-size: 14px;
        padding: 10px;
        border-right: $border-design;
      }

      .product-name {
        color: $text-color;
        font-size: 16px;
        padding: 10px;
        border-right: $border-design;
      }

      .product-date {
        color: #ccc;
        font-size: 14px;
        margin: 0 80px 0 0;
        cursor: pointer;
        background-color: transparent;
        padding: 10px;
        border-right: $border-design;
        transition: background-color $transition-speed ease, color $transition-speed ease;

        &:hover {
          background-color: $input-bg;
          color: $accent;
        }
      }

      .product-newdate {
        color: #ccc;
        font-size: 14px;
        cursor: pointer;
        padding: 10px;
        border-right: $border-design;
      }
    }
  }
    .datepicker {
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
}
</style>
