<template>
  <div id="sidebar">
    <div id="tab-bar">
      <div id="tab-list">
        <button class="tab" @click="selected = 'Available'" :class="{ selected: selected === 'Available' }">
          Available
        </button> 
        <button class="tab" @click="selected = 'Annotated'" :class="{ selected: selected === 'Annotated' }">
          Annotated
        </button> 
      </div>
    </div>
    <div id="tab-content">
      <UncertaintySideBarSelector :mode="selected == 'Available' ? 'display':'selection'" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import UncertaintySideBarSelector from './UncertaintySideBarSelector.vue';

const selected = ref<'Available'|'Annotated'>('Available')

</script>

<style scoped>
#sidebar {
  width: 100%;
  height: 100%;
  background-color: var(--color-primary);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

#tab-bar {
  display: flex;
  gap: 1rem;
  background-color: var(--color-primary);
  border-bottom: 2px solid var(--color-background);
}

#tab-list {
  padding-left: 1rem;
  padding-top: 0.5rem;
  display: flex;
  gap: 1rem;
  flex-grow: 1;
  overflow-x: auto;
}

#tab-bar-end {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
  padding-right: 1rem;
  display: center;
  align-items: center;
  justify-content: center;
}

.tab {
  padding: 0.2rem 1rem;
  background-color: var(--color-primary);
  color: var(--color-foreground);
  border: 2px solid var(--color-background);
  border-bottom: none;
  border-top-left-radius: 0.5rem;
  border-top-right-radius: 0.5rem;
  cursor: pointer;
}

.tab.selected {
  background-color: var(--color-background);
}

#tab-content {
  flex-grow: 1;
  overflow: hidden;
}
</style>