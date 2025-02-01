import { createApp } from 'vue'
import App from './App.vue'
import './assets/page.css'
import './assets/theme.css'
import './assets/icons.css'
import { TabManager } from './model/TabManager';
import { RepositoryTransformer, type RepositoryFileContent } from './diagrams/transformer/Repository';
import { AllocationTransformer, type AllocationFileContent } from './diagrams/transformer/Allocation';
import { AssemblyTransformer, type AssemblyFileContent } from './diagrams/transformer/Assembly';
import { ResourceEnvironmentTransformer, type ResourceEnvironmentFileContent } from './diagrams/transformer/ResourceEnvironment';
import { UsageModelTransformer, type UsageModelFileContent } from './diagrams/transformer/UsageModel';

const url = import.meta.env.DEV ? window.location.href + 'testing/' : window.location.href

fetch(url + 'allocation').then((response) => 
  response.json().then((json) => {
    new AllocationTransformer().transform(json as AllocationFileContent).then((graph) => {
      TabManager.getInstance().addTab(
        'Allocation',
        graph,
        false,
        false
      )
    })
  })
)

fetch(url + 'system').then((response) => 
  response.json().then((json) => {
    new AssemblyTransformer().transform(json as AssemblyFileContent).then((graph) => {
      TabManager.getInstance().addTab(
        'System',
        graph,
        false,
        false
      )
    })
  })
)

fetch(url + 'repository').then((response) => 
  response.json().then((json) => {
    new RepositoryTransformer().transform(json as RepositoryFileContent).then((graph) => {
      TabManager.getInstance().addTab(
        'Repository',
        graph,
        false,
        false
      )
    })
  })
)

fetch(url + 'resourceEnvironment').then((response) => 
  response.json().then((json) => {
    new ResourceEnvironmentTransformer().transform(json as ResourceEnvironmentFileContent).then((graph) => {
      TabManager.getInstance().addTab(
        'Resource Environment',
        graph,
        false,
        false
      )
    })
  })
)

fetch(url + 'usageModel').then((response) => 
  response.json().then((json) => {
    new UsageModelTransformer().transform(json as UsageModelFileContent).then((graph) => {
      TabManager.getInstance().addTab(
        'Usage Model',
        graph,
        false,
        false
      )
    })
  })
)

const app = createApp(App)
app.mount('#app')