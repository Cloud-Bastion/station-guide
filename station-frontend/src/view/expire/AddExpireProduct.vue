<script setup lang="ts">
import {ref, defineEmits, onMounted, computed} from 'vue';
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import ExpireProductService, {ExpireProductCategory} from "@/service/ExpireProductService";
import AddExpireProductCategory from "@/view/expire/AddExpireProductCategory.vue";
import { faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';


const emit = defineEmits(['close', 'product-added']);

const productName = ref('');
const productNumber = ref('');
const expireDate = ref(null);
const reduceTime = ref('');
const selectedCategory = ref<string | null>(null); // Holds category ID or null
const categories = ref<ExpireProductCategory[]>([]);
const addCategoryDialogOpen = ref(false);
const addProductSuccess = ref<boolean | null>(null); // null = no message, true = success, false = error
const addProductError = ref<boolean | null>(null);

// Validation refs
const productNumberError = ref('');
const productNameError = ref('');
const reduceTimeError = ref('');

const otherCategory: ExpireProductCategory = {
  name: "Andere",
  reduceProductTime: undefined,
  showProducts: true
};

// Computed property to include "Other" category
const allCategories = computed(() => {
  return [otherCategory, ...categories.value];
});

onMounted(async () => {
  // Fetch categories from backend
  try {
    // Assuming you have a service method to fetch categories
    categories.value = await ExpireProductService.getAllCategories();
  } catch (error) {
    console.error("Error fetching categories:", error);
  }
    addProductSuccess.value = null; // Reset success/error when opening dialog
    addProductError.value = null;
});

async function addProduct() {
  if (!validateInput()) {
    return; // Stop if validation fails
  }

  try {
    // Send category ID (or null for "Other")
    const categoryId = selectedCategory.value;

    await ExpireProductService.createProduct({
      name: productName.value,
      productId: parseInt(productNumber.value), // Ensure productId is a number
      expireDate: null, // You're not setting expireDate on creation
      reduceProductTime: reduceTime.value ? parseInt(reduceTime.value) : undefined, // Number or undefined
      productCategoryId: categoryId, // Send category ID
    });
    addProductSuccess.value = true;
    addProductError.value = false;
    emit('product-added');
    setTimeout(() => {
      emit('close'); // Close the modal after adding
      addProductSuccess.value = null; // Reset success for next time
    }, 1500);

  } catch (error) {
    console.error("Error adding product:", error);
    addProductSuccess.value = false;
    addProductError.value = true;
    // Optionally show an error message to the user
    setTimeout(() => {
      addProductError.value = null;
    }, 1500)
  }
}

async function handleCategoryAdded(newCategory: ExpireProductCategory) {
  categories.value = await ExpireProductService.getAllCategories(); // Refresh categories
  selectedCategory.value = newCategory.id; // Select the newly added category
  addCategoryDialogOpen.value = false; // Close the add category dialog
}

function validateInput() {
  let isValid = true;

  // Reset error messages
  productNumberError.value = '';
  productNameError.value = '';
  reduceTimeError.value = '';

  // Product Number validation
  if (!productNumber.value) {
    productNumberError.value = 'Artikelnummer ist erforderlich.';
    isValid = false;
  } else if (isNaN(parseInt(productNumber.value))) {
    productNumberError.value = 'Artikelnummer muss eine Zahl sein.';
    isValid = false;
  }

  // Product Name validation
  if (!productName.value) {
    productNameError.value = 'Produktname ist erforderlich.';
    isValid = false;
  }

  // Reduce Time validation (optional, but must be a number if provided)
  if (reduceTime.value && isNaN(parseInt(reduceTime.value))) {
    reduceTimeError.value = 'Reduzierzeit muss eine Zahl sein.';
    isValid = false;
  }
    if (reduceTime.value && parseInt(reduceTime.value) < 0) {
        reduceTimeError.value = 'Reduzierzeit muss positiv sein.';
        isValid = false;
    }

  return isValid;
}
</script>

<template>
  <div :class="$style.modal">
    <div :class="$style['modal-content']">
      <div :class="$style['modal-header']">
        <h2>Produkt hinzufügen</h2>
        <button @click="$emit('close')" :class="$style['close-button']">
          <FontAwesomeIcon icon="times"/>
        </button>
      </div>
      <div :class="$style['modal-body']">
        <div :class="$style['input-group']">
          <label for="product-number">Artikelnummer:</label>
          <input id="product-number" type="number" v-model="productNumber" :class="$style['input']"/>
          <div v-if="productNumberError" :class="$style['error-message']">{{ productNumberError }}</div>
        </div>
        <div :class="$style['input-group']">
          <label for="product-name">Produktname:</label>
          <input id="product-name" type="text" v-model="productName" :class="$style['input']"/>
          <div v-if="productNameError" :class="$style['error-message']">{{ productNameError }}</div>
        </div>
        <div :class="$style['input-group']">
          <label for="category">Kategorie:</label>
          <div :class="$style['category-select-wrapper']">
            <select id="category" v-model="selectedCategory" :class="$style['input']">
              <option :value="null">Andere</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
            <button @click="addCategoryDialogOpen = true" :class="$style['add-category-button']">
              <FontAwesomeIcon icon="plus"/>
            </button>
          </div>
        </div>
        <div :class="$style['input-group']">
          <label for="reduce-time">Reduzierzeit (Tage):</label>
          <input id="reduce-time" type="number" v-model="reduceTime" :class="$style['input']"/>
          <div v-if="reduceTimeError" :class="$style['error-message']">{{ reduceTimeError }}</div>
        </div>

        <transition name="fade">
          <div v-if="addProductSuccess" :class="$style['success-message']">
            <FontAwesomeIcon :icon="faCheck" :class="$style['success-icon']" />
          </div>
        </transition>
        <transition name="fade">
          <div v-if="addProductError" :class="$style['error-message']">
            <FontAwesomeIcon :icon="faTimes" :class="$style['error-icon']" />
          </div>
        </transition>

        <button @click="addProduct" :class="$style['add-button']">Hinzufügen</button>
      </div>
    </div>
  </div>

  <AddExpireProductCategory
      v-if="addCategoryDialogOpen"
      @close="addCategoryDialogOpen = false"
      @category-added="handleCategoryAdded"
  />
</template>

<style module lang="scss">
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

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001; /* Higher than settings menu */

  .modal-content {
    background-color: $bg-medium;
    border-radius: $border-radius;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
    padding: 20px;
    width: 400px;

    .modal-header {
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

    .modal-body {
      .input-group {
        display: flex;
        flex-direction: column;
        margin-bottom: 10px;

        label {
          color: $text-color;
          margin-bottom: 5px;
        }

        .input {
          padding: 8px;
          border: 1px solid $input-border;
          border-radius: $border-radius;
          background-color: $input-bg;
          color: $text-color;
          transition: border-color $transition-speed ease;

          &:focus {
            border-color: $accent;
            outline: none;
          }
        }
      }

      .category-select-wrapper {
        display: flex;
        align-items: center;
        gap: 5px; /* Spacing between select and button */

        .input {
          flex-grow: 1; /* Allow select to take up remaining space */
        }

        .add-category-button {
          background: none;
          border: none;
          color: $accent;
          cursor: pointer;
          font-size: 1rem;
          transition: color $transition-speed ease;

          &:hover {
            color: $accent-hover;
          }
        }
      }

      .add-button {
        padding: 10px 15px;
        background-color: $accent;
        color: $text-color;
        border: none;
        border-radius: $border-radius;
        cursor: pointer;
        transition: background-color $transition-speed ease;
        margin-top: 1rem;

        &:hover {
          background-color: $accent-hover;
        }
      }
      .success-message {
        color: green;
        margin-bottom: 10px;
        text-align: center;
      }
      .success-icon {
        color: green;
        font-size: 1.5rem;
      }

      .error-message {
        color: $accent;
        margin-bottom: 10px;
        text-align: center;
        font-size: 0.8rem;
      }
      .error-icon {
        color: $accent;
        font-size: 1.5rem;
      }
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
