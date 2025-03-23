<script setup lang="ts">
import ExpireProductService, {
  ExpireProduct,
  ExpireProductCategory,
  ExpireProductState
} from "@/service/ExpireProductService";
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {onMounted, computed, ref} from "vue";
import Ref from "@/components/util/Ref";
import Datepicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import AddExpireProduct from "@/view/expire/AddExpireProduct.vue";

const otherCategorie: ExpireProductCategory = new class implements ExpireProductCategory {
  name = "Andere";
  reduceProductTime: undefined;
  showProducts = true;
}

const categories: Ref<Map<string, ExpireProductCategory>> = new Ref<Map<string, ExpireProductCategory>>(new Map<string, ExpireProductCategory>());
const expiredProducts: Ref<Map<ExpireProductCategory, ExpireProduct[]>> = new Ref<Map<ExpireProductCategory, ExpireProduct[]>>(new Map<ExpireProductCategory, ExpireProduct[]>());

const settingsMenuOpen = ref(false);
const addProductDialogOpen = ref(false);
const searchInput = ref(''); // Add search input

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

const getStateClass = (product: ExpireProduct): string => {
  const state = ExpireProductService.getState(product);
  const stateClass: string = ''

  if (state === ExpireProductState.REDUCE) {
    return 'state-reduce';
  } else if(state === ExpireProductState.REDUCED) {
    return 'state-reduced';
  } else if(state === ExpireProductState.SORT_OUT) {
    return 'state-sort-out';
  } else {
    return 'state-set-date'
  }
};

const getStateText = (product: ExpireProduct) => {
  return ExpireProductService.getState(product);
}

// Computed property for filtered products
const filteredProducts = computed(() => {
  const searchTerm = searchInput.value.toLowerCase();
  if (!searchTerm) {
    return []; // Return empty array if no search term for better UX in settings
  }

  let allProducts: ExpireProduct[] = [];
  for (const products of expiredProducts.value.values()) {
    allProducts = allProducts.concat(products);
  }

  return allProducts.filter(product =>
      product.name.toLowerCase().includes(searchTerm) ||
      product.productId.toString().includes(searchTerm)
  );
});

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
  <div :class="{[$style['blur-background']]: settingsMenuOpen}">
    <SidebarComponent site="product-expire-management"/>
    <div :class="$style['settings-container']">
      <div :class="$style['product-count']">{{ countAllSuccess() }} /
        {{ Array.from(expiredProducts.value.values()).reduce((sum, products) => sum + products.length, 0) }} Artikel
      </div>
      <button :class="$style['settings-button']" @click="settingsMenuOpen = !settingsMenuOpen">
        <FontAwesomeIcon icon="fa-gear"/>
        <span>Einstellungen</span>
      </button>
    </div>
    <div :class="$style['product-parent']">
      <table :class="$style['product-container']">
        <template v-for="(category, index) in expiredProducts.value.keys()" :key="index">
          <tr :class="$style['product-category-parent']">
            <td :class="$style['product-category-entry']" colspan="4">
              <div :class="$style['product-category-entry-wrapper']">
                <div :class="$style['product-category-name']">{{ category.name }}</div>
                <div :class="$style['product-category-count']">({{ countCategorySuccess(category) }} /
                  {{ expiredProducts.value.get(category)?.length }})
                </div>
                <FontAwesomeIcon :icon="getDropDownIcon(category.showProducts)" :class="$style['product-category-arrow']"
                                 @click="category.showProducts=!category.showProducts"/>
              </div>
            </td>
          </tr>
          <tr v-if="category.showProducts" :class="$style['product-spacer']"></tr>
          <tr :class="$style['product-entry-container']" v-for="(product, index) in expiredProducts.value.get(category)"
              v-if="category.showProducts">
            <td :class="$style['product-id']">#{{ product.productId }}</td>
            <td :class="$style['product-name']">{{ product.name }}</td>
            <td :class="[$style['product-date'], $style[getStateClass(product)]]" @click="updateLastChange(product)">
              {{ getStateText(product) }}
            </td>
            <td :class="$style['product-newdate']">
              <Datepicker v-model="product.expireDate"
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
  </div>

  <!-- Settings Menu -->
  <div v-if="settingsMenuOpen" :class="$style['settings-menu']">
    <div :class="$style['settings-menu-header']">
      <h2>Einstellungen</h2>
      <button @click="settingsMenuOpen = false" :class="$style['close-button']">
        <FontAwesomeIcon icon="times"/>
      </button>
    </div>
    <div :class="$style['settings-menu-content']">
      <!-- Add Product Button -->
      <button @click="addProductDialogOpen = true" :class="$style['add-product-button']">
        <FontAwesomeIcon icon="plus" :class="$style['add-product-icon']"/>
        <span>Produkt hinzufügen</span>
      </button>

      <!-- Search Input -->
      <div :class="$style['search-bar-container']">
        <FontAwesomeIcon icon="search" :class="$style['search-icon']"/>
        <input type="text" v-model="searchInput" :placeholder="'Produkt suchen...'" :class="$style['search-input']"/>
      </div>

      <!-- Filtered Products List -->
      <div :class="$style['filtered-products-list']" v-if="filteredProducts.length > 0">
        <ul>
          <li v-for="product in filteredProducts" :key="product.id">
            {{ product.name }} (#{{ product.productId }})
          </li>
        </ul>
      </div>
      <div v-else-if="searchInput">
        Keine passenden Produkte gefunden.
      </div>
    </div>
  </div>

  <AddExpireProduct v-if="addProductDialogOpen" @close="addProductDialogOpen = false" />
</template>

<style lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$bg-light: #2a2a2a;
$text-color: #f1f1f1;
$accent: #ff4500; // Red
$accent-hover: #b83200; // Darker red for hover
$border-radius: 10px;
$transition-speed: 0.3s;
$input-bg: #333;
$input-border: #555;
$input-focus: #ff4500; // Red focus
$border-design: 0.1vh solid #555;

.blur-background {
  filter: blur(4px);
  pointer-events: none; /* Prevent interaction with blurred content */
}

.settings-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  border-radius: $border-radius;
  margin: 25px;
  padding: 10px 20px;
  background-color: $bg-medium;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);

  .product-count {
    color: $text-color;
    font-size: 1rem;
  }

  .settings-button {
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

    span {
      margin-left: 8px;
    }
  }
}

.product-parent {
  display: flex;
  flex-direction: column;
  margin: 25px;
  border-radius: $border-radius;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  background-color: $bg-medium;

  .product-container {
    margin: 0;
    border: none;
    box-shadow: none;
    background-color: transparent;

    .product-category-parent {
      padding: 0;

      .product-category-entry {
        width: 100%;

        .product-category-entry-wrapper {
          display: flex;
          flex-direction: row;
          align-items: center;
          background-color: $bg-light;
          padding: 15px 20px;  // Increased padding
          border-bottom: 1px solid $bg-medium;

          .product-category-name {
            color: $text-color;
            font-size: 1.2rem; // Larger font size
            font-weight: bold;
            flex-grow: 1;
          }

          .product-category-count {
            color: #bbb;
            font-size: 1rem;   // Larger font size
            margin-right: 15px;
          }

          .product-category-arrow {
            cursor: pointer;
            color: #888;
            transition: color $transition-speed ease;

            &:hover {
              color: $text-color;
            }
          }
        }
      }
    }

    .product-spacer {
      height: 20px; // Increased spacer height
    }

    .product-entry-container {
      background-color: transparent;
      transition: background-color $transition-speed ease;
      box-shadow: 0 2px 4px rgba(0,0,0,0.2); // Added shadow

      &:hover {
        background-color: $bg-light;
      }

      td {
        padding: 12px 20px;
        color: $text-color;
        font-size: 0.9rem;
      }

      .product-id {
        color: #aaa;
      }

      .product-name {
        flex-grow: 1;
      }

      .product-date {
        cursor: pointer;
        transition: background-color $transition-speed ease, color $transition-speed ease;
        border-radius: $border-radius;
        margin-right: 15px;

        &:hover {
          background-color: $input-bg;
          color: $accent;
        }
      }

      .state-reduce {
        color: $accent;
      }

      .state-reduced {
        color: green;
      }

      .state-sort-out {
        color: $accent;
      }

      .state-set-date {
        color: #aaa;
      }

      .product-newdate {
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
    }
  }
}

.settings-menu {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: $bg-medium;
  border-radius: $border-radius;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  padding: 20px;
  width: 300px;
  z-index: 1000; /* Ensure it's above other content */

  .settings-menu-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h2 {
      color: $text-color;
      font-size: 1.5rem;
      margin: 0;
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

  .settings-menu-content {
    color: $text-color;

    .add-product-button {
      display: flex;
      align-items: center;
      padding: 10px 15px;
      background-color: $accent;
      color: $text-color;
      border: none;
      border-radius: $border-radius;
      cursor: pointer;
      transition: background-color $transition-speed ease;
      margin-bottom: 10px;

      &:hover {
        background-color: $accent-hover;
      }

      .add-product-icon {
        margin-right: 8px;
      }
    }

    .search-bar-container {
      display: flex;
      align-items: center;
      margin-bottom: 15px;

      .search-icon {
        color: #777;
        margin-right: 10px;
      }

      .search-input {
        padding: 10px;
        border: 1px solid $input-border;
        border-radius: $border-radius;
        background-color: $input-bg;
        color: $text-color;
        transition: border-color $transition-speed ease;
        flex-grow: 1;

        &:focus {
          border-color: $input-focus;
          outline: none;
        }
      }
    }

    .filtered-products-list {
      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          padding: 8px 12px;
          border-bottom: 1px solid $input-border;
          transition: background-color $transition-speed ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: $bg-light;
          }
        }
      }
    }
  }
}
</style>
