<template>
  <div id="main">
    <div id="sidebar-holder">
      <UncertaintySideBar />
    </div>
    <div id="diagram-tab-manager">
      <DiagramTabDisplay />
    </div>
  </div>
  <ThemeSwitch id="theme-switch" />
</template>

<script setup lang="ts">
import DiagramTabDisplay from './components/DiagramTabDisplay.vue';
import ThemeSwitch from './components/ThemeSwitch.vue';
import UncertaintySideBar from './components/UncertaintySideBar.vue';
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
  width: 200px;
  height: 100%;
}

#theme-switch {
  position: absolute;
  top: 5px;
  right: 10px;
}
</style>