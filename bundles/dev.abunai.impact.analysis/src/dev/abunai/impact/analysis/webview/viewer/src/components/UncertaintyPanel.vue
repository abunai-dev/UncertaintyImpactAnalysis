<template>
  <div class="uncertainty-panel">
    <div class="header">#{{ uncertainty.id }} - {{ uncertainty.name }}</div>
    <div class="icon-list">
      <span v-for="category of categoryOrder" :key="category" class="fa-solid" :class="categoryOptions[category][uncertainty.classes[category]].icon" :style="{ 'color': categoryOptions[category][uncertainty.classes[category]].color[500] }"></span>
      <a :href="'https://arc3n.abunai.dev/uncertainty/' + uncertainty.id" target="_blank">More</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import { JsonUncertainty } from '../model/Uncertainty';
import { categoryOrder } from '../model/Uncertainty/Category';
import { categoryOptions } from '../model/Uncertainty/option/CategoryOption';

defineProps({
  uncertainty: {
    type: Object as PropType<JsonUncertainty>,
    required: true
  }
})
</script>

<style scoped>
.uncertainty-panel {
  width: 100%;
  background-color: var(--color-primary);
  border: 1px solid var(--color-background);
  padding: 0.25rem;
  box-sizing: border-box;
  font-family: sans-serif;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  border-radius: 0.25rem;
}

.uncertainty-panel .header {
  font-weight: bold;
}

.icon-list {
  display: flex;
  gap: 0.5rem;
  font-size: 16px;
}

.icon-list .fa-solid {
  width: 16px;
}

.icon-list a {
  margin-left: auto;
  padding: 0.1rem 0.2rem;
  border: 1px solid var(--color-foreground);
  background-color: var(--color-background);
  color: var(--color-foreground);
  box-sizing: border-box;
  border-radius: 0.1rem;
  cursor: pointer;
  font-size: 10px;
  text-decoration: none;
}
</style>