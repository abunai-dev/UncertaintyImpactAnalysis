<template>
  <div id="main" :style="{ cursor: hasSelectedUncertainty ? 'cell' : 'default' }">
    <div id="sidebar-holder">
      <UncertaintySideBar />
    </div>
    <div id="diagram-tab-manager">
      <DiagramTabDisplay />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import DiagramTabDisplay from './components/DiagramTabDisplay.vue';
import UncertaintySideBar from './components/UncertaintySideBar.vue';
import { SelectionManager } from './model/SelectionManager';

const hasSelectedUncertainty = ref(false)

SelectionManager.getInstance().addSelectUncertaintyListener((i: number) => {
  hasSelectedUncertainty.value = i !== null;
})

document.onkeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape') {
    SelectionManager.getInstance().deselect();
  }
}

</script>

<style scoped>
#main {
  display: flex;
  flex-direction: row;
  background-color: var(--color-background);
  height: 100vh;
  width: 100vw;
}

#diagram-tab-manager {
  width: 100%;
  height: 100%;
  flex-grow: 1;
}

#sidebar-holder {
  width: 300px;
  min-width: 300px;
  height: 100%;
}
</style>