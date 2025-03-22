<script setup lang="ts">
import ExpireProductService, {ExpireProduct, ExpireProductCategory} from "@/service/ExpireProductService";
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {onMounted} from "vue";
import Ref from "@/components/util/Ref";

const otherCategorie: ExpireProductCategory = new class implements ExpireProductCategory {
  name = "Andere";
  reduceProductTime: undefined;
}

const categories: Ref<Map<string, ExpireProductCategory>> = new Ref<Map<string, ExpireProductCategory>>(new Map<string, ExpireProductCategory>());
const expiredProducts: Ref<Map<ExpireProductCategory, ExpireProduct[]>> = new Ref<Map<ExpireProductCategory, ExpireProduct[]>>(new Map<ExpireProductCategory, ExpireProduct[]>());


onMounted(async () => {
  try {
    const products = await ExpireProductService.getExpiringItems();

    products.forEach(product => {
      if (product.category === undefined || product.category === null) {
        product.category = otherCategorie
      }

      if (!categories.value.has(product.category.name)) {
        categories.value.set(product.category.name, product.category)
      }

      if (!expiredProducts.value.has(categories.value.get(product.category.name))) {
        expiredProducts.value.set(categories.value.get(product.category.name), []);
      }

      expiredProducts.value.get(categories.value.get(product.category.name)).push(product)
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
    <div :class="$style['product-count']">34 / 105 Artikel</div>
    <div :class="$style['settings-button']">Einstellungen
      <FontAwesomeIcon icon="fa-gear"/>
    </div>
  </div>
  <div :class="$style['product-category-container']">
    <div :class="$style['product-category-entry']" v-for="(category, index) in expiredProducts.value.keys()"
         :key="index">
      <div :class="$style['product-category-name']">{{ category.name }}:</div>
      <div :class="$style['product-category-count']">(0 / {{ expiredProducts.value.get(category).length }})</div>
      <FontAwesomeIcon icon="fa-circle-down" :class="$style['product-category-arrow']"/>
      <div :class="$style['product-container']" v-for="(product, index) in expiredProducts.value.get(category)">
        <tr :class="$style['product-entry-container']">
          <th :class="$style['product-id']">{{ product.productId }}</th>
          <th :class="$style['product-name']">{{ product.name }}</th>
          <th :class="$style['product-date']">AUSSORTIEREN</th>
          <th :class="$style['product-newdate']">
            <FontAwesomeIcon
                icon="fa-calendar-days"/>
          </th>
        </tr>
      </div>
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