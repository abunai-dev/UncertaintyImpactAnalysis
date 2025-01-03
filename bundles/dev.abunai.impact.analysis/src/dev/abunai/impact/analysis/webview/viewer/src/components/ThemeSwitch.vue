<template>
<div class="switch-holder">
  <img class="icon-image" src="../assets/sun-regular.svg"/>
  <label class="switch">
    <input type="checkbox" v-model="darkMode" />
    <span class="slider round"></span>
  </label>
  <img class="icon-image" src="../assets/moon-regular.svg"/>
</div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';

const darkMode = ref(window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches);
function setTheme() {
  const rootElement = document.querySelector(":root") as HTMLElement;
  const sprottyElement = document.querySelector("#sprotty") as HTMLElement;
  const theme = darkMode.value ? "dark" : "light";
  rootElement.setAttribute("data-theme", theme);
  sprottyElement.setAttribute("data-theme", theme);

  const icons = Array.from(document.getElementsByClassName("icon-image"))
  for (const icon of icons) {
    icon.classList.toggle("invert", darkMode.value);
  }
}

onMounted(() => {
  setTheme()
});
watch(() => darkMode.value, (_) => {
  setTheme()
});
</script>

<style scoped>
.switch-holder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px
}
.icon-image {
  transition: filter 0.4s;
  height: 15px;
  width: 15px;
}
.icon-image.invert {
  filter: invert(1);
}

 /* The switch - the box around the slider */
 .switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 24px;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--color-background);
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 4px;
  bottom: 4px;
  background-color: var(--color-primary);
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider:before {
  -webkit-transform: translateX(16px);
  -ms-transform: translateX(16px);
  transform: translateX(16px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
} 
</style>