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

.product-parent {
  display: flex;
  flex-direction: column;
  max-width: fit-content;

  .product-container {
    margin: 25px 0 0 25px;

    .product-category-parent {
      padding: 50px;

      .product-category-entry {

        .product-category-entry-wrapper {
          display: flex;
          flex-direction: row;
          background-color: #252525;
          padding: 12px;
          margin-top: 25px;
          border-radius: 6px;

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
      }
    }

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