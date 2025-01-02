import 'reflect-metadata'
import 'sprotty/css/sprotty.css'
import './diagramElements/elementStyles.css'
import { Container, ContainerModule } from 'inversify'
import {
    configureViewerOptions,
    type IActionDispatcher,
    loadDefaultModules,
    LocalModelSource,
    TYPES
} from 'sprotty'
import { unbindHookModule } from './editMode/di.config'
import { elkLayoutModule } from 'sprotty-elk'
import { autoLayoutModule } from './layouting/di.config'
import { type SGraph } from 'sprotty-protocol'
import { diagramModule } from './diagramElements/di.config'
import { nodeModule } from './diagramElements/nodes/di.config'
import { edgeModule } from './diagramElements/edges/di.config'
import { portModule } from './diagramElements/ports/di.config'

let container: Container| undefined

export function init() {
    container = new Container()
    loadDefaultModules(container)
    container.load(elkLayoutModule)
    container.load(unbindHookModule, autoLayoutModule, diagramModule, nodeModule, edgeModule, portModule)
}

export function load(content: SGraph) {
    if (!container) return
    const localModelSource = container!.get<LocalModelSource>(TYPES.ModelSource)
    localModelSource.setModel(content)
}