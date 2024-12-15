import 'reflect-metadata'

import './theme.css'
import './page.css'
import 'sprotty/css/sprotty.css'
import './diagrammElements/elementStyles.css'
import { Container } from 'inversify'
import {
    AbstractUIExtension,
    ActionDispatcher,
    loadDefaultModules,
    LocalModelSource,
    SetUIExtensionVisibilityAction,
    TYPES
} from 'sprotty'
import { commonModule } from './common/di.config'
import { EDITOR_TYPES } from './EditorTypes'
import { unbindHookModule } from './editMode/di.config'
import { assemblyDiagramModule } from './diagrammElements/assemblyDiagram/di.config'

const container = new Container()

loadDefaultModules(container)

container.load(commonModule, unbindHookModule, assemblyDiagramModule)

const dispatcher = container.get<ActionDispatcher>(TYPES.IActionDispatcher)
const defaultUIElements = container.getAll<AbstractUIExtension>(EDITOR_TYPES.DefaultUIElement)
const localModelSource = container.get<LocalModelSource>(TYPES.ModelSource)

dispatcher.dispatchAll([
    // Show the default uis after startup
    ...defaultUIElements.map((uiElement) => {
        return SetUIExtensionVisibilityAction.create({
            extensionId: uiElement.id(),
            visible: true
        })
    })
])

localModelSource.setModel({
    type: 'graph',
    id: 'root',
    children: [
        {
            type: 'node:assembly:assemblyContext',
            id: 'node1'
        },
        {
            type: 'node:assembly:assemblyContext',
            id: 'node2'
        },
    ]
})