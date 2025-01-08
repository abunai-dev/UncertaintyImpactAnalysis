<template>
  <div class="uncertainty-panel tooltip-container" ref="container" :class="{'lowerOpacity': displayNonSelectedStyle }" :style="{
    borderColor: selectedUncertainty === uncertainty.id ? 'var(--color-valid)' : 'var(--color-background)'
  }">
    <div class="header">#{{ uncertainty.id }} - {{ uncertainty.name }}</div>
    <div class="icon-list">
      <span v-for="category of categoryOrder" :key="category" class="fa-solid" :class="categoryOptions[category][uncertainty.classes[category]].icon" :style="{ 'color':  displayNonSelectedStyle ? colors.gray[500] :
      categoryOptions[category][uncertainty.classes[category]].color[500] }"></span>
      <a :href="'https://arc3n.abunai.dev/uncertainty/' + uncertainty.id" target="_blank">More</a>
    </div>

    <span class="tooltip" ref="tooltip" :style="{ top: toolTipYOffset + 'px' }">
      <div class="header">#{{ uncertainty.id }} - {{ uncertainty.name }}</div>
      <div v-for="[index, category] of categoryOrder.entries()" :key="category" class="category" :style="{ gridRow: index + 2}">
        {{ category }}
      </div>
      <div v-for="[index, category] of categoryOrder.entries()" :key="category" class="category-item" :style="{ gridRow: index + 2}">
        <span class="fa-solid" :class="categoryOptions[category][uncertainty.classes[category]].icon" :style="{color: categoryOptions[category][uncertainty.classes[category]].color[500]}"></span> {{ categoryOptions[category][uncertainty.classes[category]].name }}
      </div>
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, PropType, ref, watch } from 'vue';
import { JsonUncertainty } from '../model/Uncertainty';
import { CategoryList, categoryOrder } from '../model/Uncertainty/Category';
import { categoryOptions } from '../model/Uncertainty/option/CategoryOption';
import { SelectionManager } from '../model/SelectionManager';
import { ArchitecturalElementTypeOptionList } from '../model/Uncertainty/option/ArchitecturalElementTypeOptions';
import { TypeRegistry } from '../model/TypeRegistry';
import colors from 'tailwindcss/colors';

const props = defineProps({
  uncertainty: {
    type: Object as PropType<JsonUncertainty>,
    required: true
  },
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  },
  index: {
    type: Number,
    required: false,
    default: 0
  }
})

const container = ref<HTMLElement | null>(null);
const tooltip = ref<HTMLElement | null>(null);
const offset = ref(0);
const selectionManager = SelectionManager.getInstance();
const typeRegistry = TypeRegistry.getInstance()
const selectedUncertainty = ref<number|null>(null);
const selectedComponentType = ref<ArchitecturalElementTypeOptionList|null>(null)
selectionManager.addSelectUncertaintyListener((id) => {
  selectedUncertainty.value = id;
})
selectionManager.addSelectComponentListener((id) => {
  if (id !== null) {
    selectedComponentType.value = typeRegistry.getComponent(id)
  } else {
    selectedComponentType.value = null
  }
})

const displayNonSelectedStyle = computed(() => {
  return (selectedUncertainty.value && selectedUncertainty.value !== props.uncertainty.id) || (selectedComponentType.value && selectedComponentType.value !== props.uncertainty.classes[CategoryList.ARCHITECTURAL_ELEMENT_TYPE])
})

const toolTipYOffset = ref(0)

function updateToolTipOffset() {
  if (!container.value || !tooltip.value) {
    return
  }
  const t = container.value.offsetTop
  toolTipYOffset.value = t-props.scrollOffsetY;
}

watch(() => props.scrollOffsetY, () => {
  nextTick(() => {
    updateToolTipOffset()
  })
})
watch(() => props.index, () => {
  nextTick(() => {
    updateToolTipOffset()
  })
})

onMounted(() => {
  offset.value = container.value?.offsetTop ?? 0;
  container.value?.addEventListener('click', () => {
    selectionManager.selectUncertainty(props.uncertainty.id);
  })
  updateToolTipOffset()
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

.uncertainty-panel.lowerOpacity .header {
  opacity: 0.5;
}

.uncertainty-panel.lowerOpacity .icon-list {
  opacity: 0.5;
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

.tooltip {
  display: none;
  position: absolute;
  left: 305px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 0.5rem;
  border-radius: 0.25rem;
  font-size: 12px;
  z-index: 1;
  column-gap: 0.25rem;
}

.tooltip-container:hover .tooltip {
  display: grid;
}

.tooltip .header {
  grid-column-start: 1;
  grid-column-end: 3;
  font-size: 14px;
}

.tooltip .category {
  font-weight: bold;
  grid-column-start: 1;
}

.tooltip .category-item {
  grid-column-start: 2;
}

.tooltip .category-item .fa-solid {
  width: 16px;
}
</style>