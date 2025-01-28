import { createApp } from 'vue'
import App from './App.vue'
import './assets/page.css'
import './assets/theme.css'
import './assets/icons.css'
import { TabManager } from './model/TabManager';
import { RepositoryTransformer, type RepositoryFileContent } from './diagrams/transformer/Repository';
import repositoryJson from './assets/json/repository.json'
import { AllocationTransformer, type AllocationFileContent } from './diagrams/transformer/Allocation';
import allocationJson from './assets/json/allocation.json'
import { AssemblyTransformer, type AssemblyFileContent } from './diagrams/transformer/Assembly';
import systemJson from './assets/json/system.json'
import { ResourceEnvironmentTransformer, type ResourceEnvironmentFileContent } from './diagrams/transformer/ResourceEnvironment';
import resourceJson from './assets/json/resourceEnvironment.json'
import { UsageModelTransformer, type UsageModelFileContent } from './diagrams/transformer/UsageModel';
import usageJson from './assets/json/usageModel.json'


new AllocationTransformer().transform(allocationJson as AllocationFileContent).then((graph) => {
  TabManager.getInstance().addTab(
    'Allocation',graph,
    false,
    false
  )
})
new AssemblyTransformer().transform(systemJson as AssemblyFileContent).then((graph) => {
  TabManager.getInstance().addTab(
    'System',
    graph,
    false,
    false
  )
})
new RepositoryTransformer().transform(repositoryJson as RepositoryFileContent).then((graph) => {
  TabManager.getInstance().addTab(
    'Repository',
    graph,
    false,
    false
  )
})
new ResourceEnvironmentTransformer().transform(resourceJson as ResourceEnvironmentFileContent).then((graph) => {
  TabManager.getInstance().addTab(
    'Resource Environment',
    graph,
    false,
    false
  )
})
new UsageModelTransformer().transform(usageJson as UsageModelFileContent).then((graph) => {
  TabManager.getInstance().addTab(
    'Usage Model',
    graph,
    false,
    false
  )
})

const app = createApp(App)
app.mount('#app')