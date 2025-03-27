<script setup lang="ts">
</script>

<template>
  <div :class="$style['not-found-container']">
    <div :class="$style['content']">
      <FontAwesomeIcon icon="fa-triangle-exclamation" :class="$style['icon']"/>
      <h1 :class="$style['title']">404</h1>
      <p :class="$style['subtitle']">Seite nicht gefunden</p>
      <p :class="$style['description']">
        Entschuldigung, die von Ihnen angeforderte Seite konnte nicht gefunden werden.
        Möglicherweise wurde sie verschoben, gelöscht oder existiert nicht.
      </p>
      <router-link to="/">
        <button :class="$style['home-button']">
          Zurück zur Startseite
        </button>
      </router-link>
    </div>
  </div>
</template>

<style scoped lang="scss" module>
$bg-dark: #121212;
$bg-medium: #1e1e1e;
$text-color: #f1f1f1;
$text-color-light: #b0b0b0;
$accent: #ff4500; // Red accent from other files
$accent-hover: #b83200;
$border-radius: 8px;
$transition-speed: 0.3s;

.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh; // Ensure it takes full viewport height
  background-color: $bg-dark;
  color: $text-color;
  text-align: center;
  padding: 20px;
  font-family: 'Roboto', sans-serif; // Consistent font
}

.content {
  background-color: $bg-medium;
  padding: 40px 50px;
  border-radius: $border-radius;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
  max-width: 600px;
  width: 100%;
  animation: fadeIn 0.5s ease-in-out;
}

.icon {
  font-size: 4rem; // Large icon
  color: $accent;
  margin-bottom: 20px;
}

.title {
  font-size: 6rem; // Very large 404 number
  font-weight: bold;
  color: $text-color;
  margin: 0;
  line-height: 1;
}

.subtitle {
  font-size: 1.8rem; // Large subtitle
  font-weight: 500;
  color: $text-color;
  margin: 10px 0 20px;
}

.description {
  font-size: 1rem;
  color: $text-color-light;
  line-height: 1.6;
  margin-bottom: 30px;
}

.home-button {
  padding: 12px 25px;
  background-color: $accent;
  color: $text-color;
  border: none;
  border-radius: $border-radius;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color $transition-speed ease, transform 0.1s ease;

  &:hover {
    background-color: $accent-hover;
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// Media query for smaller screens
@media (max-width: 768px) {
  .content {
    padding: 30px 25px;
  }
  .icon {
    font-size: 3rem;
  }
  .title {
    font-size: 4.5rem;
  }
  .subtitle {
    font-size: 1.5rem;
  }
  .description {
    font-size: 0.9rem;
  }
  .home-button {
    padding: 10px 20px;
    font-size: 0.9rem;
  }
}
</style>
