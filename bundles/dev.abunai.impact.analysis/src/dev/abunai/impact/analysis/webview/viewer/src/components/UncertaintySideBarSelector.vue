<template>
    <div class="content">
        <div class="input">
            <span class="fa-solid fa-magnifying-glass"></span>
            <input v-model="filter" placeholder="Filter/Search" />
        </div>
        <div id="uncertainty-holder" ref="scrollContainer">
            <UncertaintyPanel
                v-for="[idx, uncertainty] in filteredData.entries()"
                :key="uncertainty.id"
                :uncertainty="uncertainty"
                :scroll-offset-y="scrollOffsetY"
                :index="idx"
                :mode="mode"
            />
        </div>
        <a class="button" v-if="mode == 'display'" href="https://arc3n.abunai.dev/" target="_blank">Open ARC<sup>3</sup>N</a>
        <div class="button" v-else>Get Json</div>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, PropType, ref, Ref } from 'vue'
import { JsonUncertainty } from '../model/Uncertainty'
import UncertaintyPanel from './UncertaintyPanel.vue'
import { CategoryList, categoryOrder } from '../model/Uncertainty/Category'
import { categoryOptions } from '../model/Uncertainty/option/CategoryOption'
import { TypeRegistry } from '../model/TypeRegistry'
import { ArchitecturalElementTypeOptionList } from '../model/Uncertainty/option/ArchitecturalElementTypeOptions'
import { SelectionManager, Selection } from '../model/SelectionManager'

const props = defineProps({
    mode: {
        type: String as PropType<'display' | 'selection'>,
        required: true
    }
})

const fetchStatus: Ref<'pending' | 'loaded' | 'error'> = ref('pending')
const data: Ref<JsonUncertainty[]> = ref([])

const filter = ref('')

const selections = ref<Selection[]>([])
SelectionManager.getInstance().addSelectionChangedListener((s) => {
    selections.value = s
})

const filteredData = computed(() => {
    const modeSpecific = props.mode === 'display' ? data.value : data.value.filter(u => selections.value.find((s) => s.uncertainty === u.id) !== undefined)
    return modeSpecific.filter((uncertainty) => applyFilter(uncertainty, filter.value))
})

function applyFilter(uncertainty: JsonUncertainty, filter: string): boolean {
    if (uncertainty.name.toLowerCase().includes(filter.toLowerCase())) {
        return true
    }
    if (uncertainty.id.toString().includes(filter) || ('#' + uncertainty.id.toString()).includes(filter)) {
        return true
    }

    for (const category of categoryOrder) {
        const valueOfUncertainty = categoryOptions[category][uncertainty.classes[category]].name.toLowerCase()
        if (valueOfUncertainty.includes(filter.toLowerCase())) {
            return true
        }
    }

    return false
}

const selectionManager = SelectionManager.getInstance()
const typeRegistry = TypeRegistry.getInstance()
selectionManager.addSelectComponentListener((v) => {
    if (v == null) {
        data.value = data.value.sort((a, b) => a.id - b.id)
    } else {
        const selectedType = typeRegistry.getComponent(v)
        data.value = data.value.sort((a, b) => {
            const aType = a.classes[CategoryList.ARCHITECTURAL_ELEMENT_TYPE] as ArchitecturalElementTypeOptionList
            const bType = b.classes[CategoryList.ARCHITECTURAL_ELEMENT_TYPE] as ArchitecturalElementTypeOptionList
            if (aType === selectedType && bType !== selectedType) {
                return -1
            } else if (aType !== selectedType && bType === selectedType) {
                return 1
            } else {
                return a.id - b.id
            }
        })
    }
})

const scrollContainer = ref<HTMLElement | null>(null)
const scrollOffsetY = ref(0)

onMounted(() => {
    fetch('https://arc3n.abunai.dev/data.json')
        .then((response) => response.json() as Promise<JsonUncertainty[]>)
        .then((json) => {
            data.value = json.sort((a, b) => a.id - b.id)
            fetchStatus.value = 'loaded'
            for (const uncertainty of data.value) {
                typeRegistry.registerUncertainty(
                    uncertainty.id,
                    uncertainty.classes[CategoryList.ARCHITECTURAL_ELEMENT_TYPE] as ArchitecturalElementTypeOptionList
                )
            }
        })
        .catch((error) => {
            console.error('Error:', error)
            fetchStatus.value = 'error'
        })
    scrollContainer.value?.addEventListener('scroll', () => {
        scrollOffsetY.value = scrollContainer.value?.scrollTop ?? 0
    })
})
</script>

<style scoped>
.content {
    display: flex;
    flex-direction: column;
    overflow: hidden;
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

.content .button {
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
