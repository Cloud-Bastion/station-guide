<script setup lang="ts">
import ExpireProductService, {
  ExpireProduct,
  ExpireProductCategory,
  ExpireProductState
} from "@/service/ExpireProductService";
import SidebarComponent from "@/components/sidebar/SidebarComponent.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {onMounted, computed, ref, watch} from "vue";
import Ref from "@/components/util/Ref";
import Datepicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import AddExpireProduct from "@/view/expire/AddExpireProduct.vue";
import AddExpireProductCategory from "@/view/expire/AddExpireProductCategory.vue";

const otherCategorie: ExpireProductCategory = new class implements ExpireProductCategory {
  name = "Andere";
  reduceProductTime: undefined;
  showProducts = true;
}

const categories: Ref<Map<string, ExpireProductCategory>> = new Ref<Map<string, ExpireProductCategory>>(new Map<string, ExpireProductCategory>());
const expiredProducts: Ref<Map<ExpireProductCategory, ExpireProduct[]>> = new Ref<Map<ExpireProductCategory, ExpireProduct[]>>(new Map<ExpireProductCategory, ExpireProduct[]>());
const allProducts: Ref<ExpireProduct[]> = new Ref<ExpireProduct[]>([]);

const settingsMenuOpen = ref(false);
const addProductDialogOpen = ref(false);
const addCategoryDialogOpen = ref(false); // Add category dialog
const searchInput = ref(''); // Add search input
const categorySearchInput = ref(''); // Category search input
const selectedSetting = ref('products'); // Add selected setting
const isProductLoading = ref(false); // Loading indicator for products
const isCategoryLoading = ref(false); // Loading indicator for categories
const displayedProducts = ref<ExpireProduct[]>([]); // New ref for displayed products


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
  (expiredProducts.value.get(categories.value.get(category.name)!) ?? []).forEach(product => {
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
  } else if (state === ExpireProductState.REDUCED) {
    return 'state-reduced';
  } else if (state === ExpireProductState.SORT_OUT) {
    return 'state-sort-out';
  } else {
    return 'state-set-date'
  }
};

const getStateText = (product: ExpireProduct) => {
  return ExpireProductService.getState(product);
}

// --- Debounced Search Logic (Products) ---
let productSearchTimeout: number | undefined;

const filteredProducts = computed(() => {
  const searchTerm = searchInput.value.toLowerCase();
  if (!searchTerm) {
    return [];
  }

  return allProducts.value.filter(product =>
      product.name.toLowerCase().includes(searchTerm) ||
      product.productId.toString().includes(searchTerm)
  );
});

watch(searchInput, () => {
  clearTimeout(productSearchTimeout);
  isProductLoading.value = true; // Show loading indicator
  productSearchTimeout = setTimeout(() => {
    // Update displayedProducts after debounce
    displayedProducts.value = filteredProducts.value;
    isProductLoading.value = false; // Hide loading indicator
  }, 300); // 300ms debounce time
});

// --- Debounced Search Logic (Categories) ---
let categorySearchTimeout: number | undefined;
const filteredCategories = computed(() => {
  const searchTerm = categorySearchInput.value.toLowerCase();
  if (!searchTerm) {
    return Array.from(categories.value.values());
  }

  return Array.from(categories.value.values()).filter(category =>
      category.name.toLowerCase().includes(searchTerm)
  );
});

watch(categorySearchInput, () => {
  clearTimeout(categorySearchTimeout);
  isCategoryLoading.value = true;
  categorySearchTimeout = setTimeout(() => {
    isCategoryLoading.value = false;
  }, 300);
});

// --- Clear Search Functions ---
function clearProductSearch() {
  searchInput.value = '';
}

function clearCategorySearch() {
  categorySearchInput.value = '';
}

async function updateProduct(product: ExpireProduct) {
  await ExpireProductService.updateExpireDate(product);
}

async function updateCategory(category: ExpireProductCategory) {
  //TODO: Add update category
}

const allCategories = computed(() => {
  return [otherCategorie, ...Array.from(categories.value.values())];
});

onMounted(async () => {
  try {
    const products = await ExpireProductService.getExpiringItems();
    allProducts.value = await ExpireProductService.getAllProducts();

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

    let allCategories = await ExpireProductService.getAllCategories();
    allCategories.forEach(category => {
      categories.value.set(category.name, category);
    })

    displayedProducts.value = [];
  } catch (error) {
    console.error("Fehler beim Laden der Artikel:", error)
  }
})
</script>

<template>
  <div :class="{[$style['blur-background']]: settingsMenuOpen}">
    <SidebarComponent site="product-expire-management"/>

    <div :class="$style['settings-container']">
      <div :class="$style['product-count']">
        {{ countAllSuccess() }} /
        {{ Array.from(expiredProducts.value.values()).reduce((sum, products) => sum + products.length, 0) }} Artikel
      </div>

      <button :class="$style['settings-button']" @click="settingsMenuOpen = !settingsMenuOpen">
        <FontAwesomeIcon icon="fa-gear"/>
        <span>Einstellungen</span>
      </button>
    </div>

    <div :class="$style['product-parent']">
      <table :class="$style['product-container']">
        <transition-group name="product-list" tag="tbody">
          <template v-for="(category, index) in expiredProducts.value.keys()" :key="category.name">
            <tr :class="$style['product-category-parent']">
              <td :class="$style['product-category-entry']" colspan="4">
                <div :class="$style['product-category-entry-wrapper']">
                  <div :class="$style['product-category-name']">{{ category.name }}:</div>

                  <div :class="$style['product-category-count']">
                    ({{ countCategorySuccess(category) }} /
                    {{ expiredProducts.value.get(category)?.length }})
                  </div>

                  <FontAwesomeIcon
                      :icon="getDropDownIcon(category.showProducts)"
                      :class="$style['product-category-arrow']"
                      @click="category.showProducts=!category.showProducts"
                  />
                </div>
              </td>
            </tr>

            <tr v-if="category.showProducts" :class="$style['product-spacer']"></tr>

            <tr
                :class="$style['product-entry-container']"
                v-for="(product, index) in expiredProducts.value.get(category)"
                v-if="category.showProducts"
                :key="product.id"
            >
              <td :class="$style['product-id']">#{{ product.productId }}</td>
              <td :class="$style['product-name']">{{ product.name }}</td>
              <td
                  :class="[$style['product-date'], $style[getStateClass(product)]]"
                  @click="updateLastChange(product)"
              >
                {{ getStateText(product) }}
              </td>
              <td :class="$style['product-newdate']">
                <Datepicker
                    v-model="product.expireDate"
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
        </transition-group>
      </table>
    </div>
  </div>

  <!-- Settings Menu -->
  <transition name="settings-menu">
    <div v-if="settingsMenuOpen" :class="$style['settings-menu']">
      <div :class="$style['settings-menu-header']">
        <h2>Einstellungen</h2>
        <button @click="settingsMenuOpen = false" :class="$style['close-button']">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>

      <div :class="$style['settings-menu-tabs']">
        <button
            :class="[$style['tab-button'], selectedSetting === 'products' ? $style['active-tab'] : '']"
            @click="selectedSetting = 'products'">
          Produkte
        </button>
        <button
            :class="[$style['tab-button'], selectedSetting === 'categories' ? $style['active-tab'] : '']"
            @click="selectedSetting = 'categories'">
          Kategorien
        </button>
      </div>

      <div :class="$style['settings-menu-content']">
        <!-- Products Section -->
        <div v-if="selectedSetting === 'products'">
          <button @click="addProductDialogOpen = true" :class="$style['add-product-button']">
            <FontAwesomeIcon icon="plus" :class="$style['add-product-icon']"/>
            <span>Produkt hinzufügen</span>
          </button>

          <div :class="$style['search-bar-container']">
            <FontAwesomeIcon icon="search" :class="$style['search-icon']"/>
            <input
                type="text"
                v-model="searchInput"
                :placeholder="'Produkt suchen...'"
                :class="$style['search-input']"
            />
            <button v-if="searchInput" @click="clearProductSearch" :class="$style['clear-search-button']">
              <FontAwesomeIcon icon="times"/>
            </button>
            <FontAwesomeIcon v-if="isProductLoading" icon="spinner" spin :class="$style['loading-spinner']"/>
          </div>

          <!-- Table for Products -->
          <table :class="$style['products-table']" v-if="displayedProducts.length > 0">
            <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Reduce Time (Days)</th>
              <th>Category</th>
            </tr>
            </thead>
            <transition-group name="product-list" tag="tbody">
              <tr v-for="product in displayedProducts" :key="product.id">
                <td>{{ product.productId }}</td>
                <td>
                  <input type="text" v-model="product.name" @change="updateProduct(product)"
                         :class="$style['editable-input']"/>
                </td>
                <td>
                  <input type="number" v-model="product.reduceProductTime" @change="updateProduct(product)"
                         :class="$style['editable-input']"/>
                </td>
                <td>
                  <select v-model="product.category" @change="updateProduct(product)"
                          :class="$style['editable-select']">
                    <option v-for="category in allCategories" :key="category.id" :value="category">
                      {{ category.name }}
                    </option>
                    <option :value="null">Andere</option>
                  </select>
                </td>
              </tr>
            </transition-group>
          </table>
          <div v-else-if="searchInput && !isProductLoading">
            Keine passenden Produkte gefunden.
          </div>
        </div>

        <!-- Categories Section -->
        <div v-else-if="selectedSetting === 'categories'">
          <button @click="addCategoryDialogOpen = true" :class="$style['add-category-button']">
            <FontAwesomeIcon icon="plus" :class="$style['add-category-icon']"/>
            <span>Kategorie hinzufügen</span>
          </button>

          <div :class="$style['search-bar-container']">
            <FontAwesomeIcon icon="search" :class="$style['search-icon']"/>
            <input
                type="text"
                v-model="categorySearchInput"
                :placeholder="'Kategorie suchen...'"
                :class="$style['search-input']"
            />
            <button v-if="categorySearchInput" @click="clearCategorySearch" :class="$style['clear-search-button']">
              <FontAwesomeIcon icon="times"/>
            </button>
            <FontAwesomeIcon v-if="isCategoryLoading" icon="spinner" spin :class="$style['loading-spinner']"/>
          </div>

          <transition-group name="category-list" tag="div">
            <div :class="$style['filtered-categories-list']" v-if="filteredCategories.length > 0">
              <ul>
                <li v-for="category in filteredCategories" :key="category.name">
                  {{ category.name }}
                </li>
              </ul>
            </div>
          </transition-group>
          <div v-if="categorySearchInput && !isCategoryLoading">
            Keine passenden Kategorien gefunden.
          </div>
        </div>
      </div>
    </div>
  </transition>

  <AddExpireProduct v-if="addProductDialogOpen" @close="addProductDialogOpen = false"/>
  <AddExpireProductCategory v-if="addCategoryDialogOpen" @close="addCategoryDialogOpen = false"/>
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
          padding: 15px 20px; // Increased padding
          border-bottom: 1px solid $bg-medium;

          .product-category-name {
            color: $text-color;
            font-size: 1.2rem; // Larger font size
            font-weight: bold;
            flex-grow: 1;
            text-align: left; /* Left-align the text */
          }

          .product-category-count {
            color: #bbb;
            font-size: 1rem; // Larger font size
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
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); // Added shadow

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
  width: 400px; /* Increased width */
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

  .settings-menu-tabs {
    display: flex;
    justify-content: space-around;
    margin-bottom: 10px;

    .tab-button {
      padding: 8px 12px;
      background: none;
      border: none;
      color: $text-color;
      cursor: pointer;
      transition: background-color $transition-speed ease, color $transition-speed ease;
      border-radius: $border-radius;

      &:hover {
        background-color: $bg-light;
      }
    }

    .active-tab {
      background-color: $accent;
      color: white;

      &:hover {
        background-color: $accent-hover;
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

    .add-category-button {
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

      .add-category-icon {
        margin-right: 8px;
      }
    }

    .search-bar-container {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      position: relative;

      .search-icon {
        color: #777;
        position: absolute;
        left: 10px;
        z-index: 1;
      }

      .search-input {
        padding: 10px;
        padding-left: 35px;
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

      .clear-search-button {
        position: absolute;
        right: 10px;
        background: none;
        border: none;
        color: #aaa;
        cursor: pointer;
        transition: color $transition-speed ease;
        padding: 0;
        z-index: 1;

        &:hover {
          color: $text-color;
        }
      }

      .loading-spinner {
        position: absolute;
        right: 10px;
        z-index: 1;
      }
    }

    .products-table {
      width: 100%;
      border-collapse: collapse;

      th, td {
        padding: 8px 12px;
        border-bottom: 1px solid $input-border;
        text-align: left;
      }

      th {
        background-color: $bg-light;
        color: $text-color;
      }

      .editable-input, .editable-select {
        padding: 6px;
        border: 1px solid $input-border;
        border-radius: $border-radius;
        background-color: $input-bg;
        color: $text-color;
        transition: border-color $transition-speed ease;
        width: 90%;

        &:focus {
          border-color: $input-focus;
          outline: none;
        }
      }
    }

    .filtered-categories-list {
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

// --- Animations ---
.settings-menu-enter-active,
.settings-menu-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.settings-menu-enter-from,
.settings-menu-leave-to {
  opacity: 0;
  transform: translate(-50%, -40%);
}

.product-list-enter-active,
.product-list-leave-active,
.category-list-enter-active,
.category-list-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.product-list-enter-from,
.product-list-leave-to,
.category-list-enter-from,
.category-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.product-list-move,
.category-list-move {
  transition: transform 0.3s ease;
}
</style>
