import 'reflect-metadata'
import './theme.css'
import './page.css'
import 'sprotty/css/sprotty.css'
import './diagramElements/elementStyles.css'
import { Container } from 'inversify'
import {
    IActionDispatcher,
    loadDefaultModules,
    LocalModelSource,
    TYPES
} from 'sprotty'
import { unbindHookModule } from './editMode/di.config'
import { elkLayoutModule } from 'sprotty-elk'
import { autoLayoutModule } from './layouting/di.config'
import { FitToScreenAction, SModelElement } from 'sprotty-protocol'
import { diagramModule } from './diagramElements/di.config'
import { nodeModule } from './diagramElements/nodes/di.config'
import { edgeModule } from './diagramElements/edges/di.config'
import { portModule } from './diagramElements/ports/di.config'
import { AllocationFileContent, AllocationTransformer } from './transformer/Allocation'
import allocationJson from './transformer/json/allocation.json'
import { AssemblyFileContent, AssemblyTransformer } from './transformer/Assembly'
import assemblyJson from './transformer/json/system.json'
import { ResourceEnvironmentFileContent, ResourceEnvironmentTransformer } from './transformer/ResourceEnvironment'
import resourceEnvironmentJson from './transformer/json/resourceEnvironment.json'
import { UsageModelFileContent, UsageModelTransformer } from './transformer/UsageModel'
import usageModelJson from './transformer/json/usageModel.json'

const container = new Container()

loadDefaultModules(container)
container.load(elkLayoutModule)
container.load(unbindHookModule, autoLayoutModule, diagramModule, nodeModule, edgeModule, portModule)

async function test(content: SModelElement[]) {
    const localModelSource = container.get<LocalModelSource>(TYPES.ModelSource)
    localModelSource.setModel({
        type: 'graph',
        id: 'root',
        children: content
    }).then(() => {
        container.get<IActionDispatcher>(TYPES.IActionDispatcher).dispatch(
        FitToScreenAction.create([localModelSource.model.id], {
            padding: 10
        }))
    })
}


document.addEventListener('DOMContentLoaded', async () => {
    //test(new AllocationTransformer().transform(allocationJson as AllocationFileContent))
    //test(new AssemblyTransformer().transform(assemblyJson as AssemblyFileContent))
    //test(new ResourceEnvironmentTransformer().transform(resourceEnvironmentJson as ResourceEnvironmentFileContent))
    test(new UsageModelTransformer().transform(usageModelJson as UsageModelFileContent))
})
