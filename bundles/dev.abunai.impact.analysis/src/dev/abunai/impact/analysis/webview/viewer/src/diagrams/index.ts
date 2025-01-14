import 'reflect-metadata'
import 'sprotty/css/sprotty.css'
import './diagramElements/elementStyles.css'
import { Container, ContainerModule } from 'inversify'
import {
    ActionDispatcher,
    configureViewerOptions,
    type IActionDispatcher,
    loadDefaultModules,
    LocalModelSource,
    TYPES
} from 'sprotty'
import { unbindHookModule } from './editMode/di.config'
import { elkLayoutModule } from 'sprotty-elk'
import { type SGraph } from 'sprotty-protocol'
import { diagramModule } from './diagramElements/di.config'
import { nodeModule } from './diagramElements/nodes/di.config'
import { edgeModule } from './diagramElements/edges/di.config'
import { portModule } from './diagramElements/ports/di.config'
import { selectionModule } from './selection/di.config'
import { SelectionManager } from '@/model/SelectionManager'

let container: Container| undefined

export function init() {
    container = new Container()
    loadDefaultModules(container)
    container.load(elkLayoutModule)
    container.load(unbindHookModule, diagramModule, nodeModule, edgeModule, portModule, selectionModule)
    SelectionManager.getInstance().dispatcher = container.get<ActionDispatcher>(TYPES.IActionDispatcher)
}

export function load(content: SGraph) {
    if (!container) return
    const localModelSource = container!.get<LocalModelSource>(TYPES.ModelSource)
    localModelSource.setModel(content)
}