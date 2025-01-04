<template>
  <div id="sidebar">
    <div class="content">
      <div class="input">
        <span class="fa-solid fa-magnifying-glass"></span>
        <input v-model="filter" placeholder="Filter/Search" />
      </div>
      <div id="uncertainty-holder" ref="scrollContainer">
        <UncertaintyPanel v-for="uncertainty in filteredData" :key="uncertainty.id" :uncertainty="uncertainty" :scroll-offset-y="scrollOffsetY" />
      </div>
      <a href="https://arc3n.abunai.dev/" target="_blank">Open ARC3N</a>
    </div>
  </div>
</template>

<script setup lang="ts">

import { computed, onMounted, ref, Ref } from 'vue';
import { JsonUncertainty } from '../model/Uncertainty';
import UncertaintyPanel from './UncertaintyPanel.vue';
import { CategoryList, categoryOrder } from '../model/Uncertainty/Category';
import { categoryOptions } from '../model/Uncertainty/option/CategoryOption';
import { TypeRegistry } from '../model/TypeRegistry';
import { ArchitecturalElementTypeOptionList } from '../model/Uncertainty/option/ArchitecturalElementTypeOptions';

const fetchStatus: Ref<'pending'|'loaded'|'error'> = ref('pending');
const data: Ref<JsonUncertainty[]> = ref([]);

const filter = ref('');

const filteredData = computed(() => {
  return data.value.filter(uncertainty => applyFilter(uncertainty, filter.value));
});

function applyFilter(uncertainty: JsonUncertainty, filter: string): boolean {
  if(uncertainty.name.toLowerCase().includes(filter.toLowerCase())) {
    return true;
  }
  if (uncertainty.id.toString().includes(filter) || ('#'+ uncertainty.id.toString()).includes(filter)) {
    return true;
  }

  for (const category of categoryOrder) {
    const valueOfUncertainty = categoryOptions[category][uncertainty.classes[category]].name.toLowerCase();
    if (valueOfUncertainty.includes(filter.toLowerCase())) {
      return true;
    }
  }

  return false
}

const scrollContainer = ref<HTMLElement | null>(null);
const scrollOffsetY = ref(0);

onMounted(() => {
  fetch('data.json')
    .then(response => response.json() as Promise<JsonUncertainty[]>)
    .then(json => {
      data.value = json.sort((a, b) => a.id - b.id);
      fetchStatus.value = 'loaded';
      const typeRegistry = TypeRegistry.getInstance();
      for (const uncertainty of data.value) {
        typeRegistry.registerUncertainty(uncertainty.id, uncertainty.classes[CategoryList.ARCHITECTURAL_ELEMENT_TYPE] as ArchitecturalElementTypeOptionList);
      }
    })
    .catch(error => {
      console.error('Error:', error);
      fetchStatus.value = 'error';
    });
  scrollContainer.value?.addEventListener('scroll', () => {
    scrollOffsetY.value = scrollContainer.value?.scrollTop ?? 0;
  });
});
</script>

<style scoped>


#sidebar {
  width: 100%;
  height: 100%;
  background-color: var(--color-primary);
  overflow: hidden;
}

.content {
  display: flex;
  flex-direction: column;
  overflow:hidden;
  height: 100%;
  gap: 0.5rem;
  box-sizing: border-box;
  padding: 0.5rem;
}

.content .input {
  border: 1px solid var(--color-foreground);
  background-color: var(--color-background);
  color: var(--color-foreground);
  padding: 0.25rem;
  border-radius: 0.25rem;
  display: flex;
  gap: 0.25rem;
  align-items: center;
}
.input span {
  font-size: 14px;
}

input {
  border: none;
  outline: none;
  flex-grow: 1;
  background-color: var(--color-background);
  color: var(--color-foreground);
}

#uncertainty-holder {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  overflow-x: scroll;
  flex-grow: 1;
}

.content a {
  padding: 0.5rem;
  box-sizing: border-box;
  border: 1px solid var(--color-foreground);
  background-color: var(--color-background);
  color: var(--color-foreground);
  border-radius: 0.25rem;
  text-decoration: none;
  text-align: center;
  font-family: sans-serif;
  cursor: pointer;
}
</style>