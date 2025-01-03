<template>
  <div id="tab-outer">
    <div id="tab-bar">
      <div id="tab-list">
          <button @click="changeTab(idx)" class="tab" v-for="[idx, uuid] of uuids.entries()" :key="uuid + '-tab'" :class="{ selected: idx == index }">
          {{ tabManager.getTab(idx).name }}
          <span v-if="tabManager.getTab(idx).closable" @click="e => closeTab(idx, e)">X</span>
        </button> 
      </div>
      <div id="tab-bar-end">
        <ThemeSwitch />
      </div>
    </div>
    <div id="tab-content">
      <div id="sprotty"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { TabManager } from '../model/TabManager';
import { init, load } from '../diagrams';
import ThemeSwitch from './ThemeSwitch.vue';

const tabManager = TabManager.getInstance()
const uuids = ref(tabManager.getUuids())
const loadedFirstTab = ref(false)
tabManager.addChangeListener(() => {
  uuids.value = tabManager.getUuids()
  if (!loadedFirstTab.value && uuids.value.length > 0) {
    load(tabManager.getTab(0).content)
    loadedFirstTab.value = true
  }
})
const index = ref(0)
tabManager.addIndexChangeListener((i) => {
  index.value = i
  load(tabManager.getTab(i).content)
})

function changeTab(i: number) {
  tabManager.setIndex(i)
}

function closeTab(i: number, event: MouseEvent) {
  event.stopPropagation()
  tabManager.removeTab(uuids.value[i])
}

onMounted(() => {
  init()
  if (uuids.value.length > 0) {
    load(tabManager.getTab(0).content)
    loadedFirstTab.value = true
  }
})

</script>

<style scoped>
#tab-bar {
  display: flex;
  gap: 1rem;
  background-color: var(--color-primary);
  padding-right: 1rem;
  align-items: center;
  align-items: center;
  justify-content: center;
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

#tab-outer {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%
}

#tab-content {
  flex-grow: 1;
  overflow: hidden;
}
</style>