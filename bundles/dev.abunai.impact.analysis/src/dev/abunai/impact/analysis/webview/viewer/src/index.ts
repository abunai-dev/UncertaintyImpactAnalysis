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
import {
    SEdge,
    SLabel,
    SModelElement,
    SNode
} from 'sprotty-protocol'
import { commonModule } from './common/di.config'
import { EDITOR_TYPES } from './EditorTypes'
import { unbindHookModule } from './editMode/di.config'
import { assemblyDiagramModule } from './diagrammElements/assemblyDiagram/di.config'
import { AssemblyPort } from './diagrammElements/assemblyDiagram/ProvidingPort'
import { AssemblyContextScheme } from './diagrammElements/assemblyDiagram/AssemblyContextNode'
import { diagramCommonModule } from './diagrammElements/di.config'

const container = new Container()

loadDefaultModules(container)

container.load(commonModule, unbindHookModule, assemblyDiagramModule, diagramCommonModule)

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

function generateRandomId() {
    return Math.random().toString(36).substring(7)
}

function buildLabel(text: string): SLabel {
    return {
        id: generateRandomId(),
        type: 'label',
        text: text
    }
}

function buildPort(id?: string): AssemblyPort {
    return {
        id: id ?? generateRandomId(),
        type: 'port:assembly:providing',
        name: '1234'
    }
}

function buildEdge(source: string, target: string): SEdge {
    return {
        id: generateRandomId(),
        type: 'edge:open',
        sourceId: source,
        targetId: target
    }
}

function buildNode(label: SModelElement[]): AssemblyContextScheme {
    return {
        id: generateRandomId(),
        type: 'node:assembly:assemblyContext',
        children: label,
        layout: 'vbox',
        typeName: 'AssemblyContext',
        name: 'World',
        layoutOptions: {
            hAlign: 'center',
        },

        position: {
            x: 100,
            y: 100
        }
    }
}


localModelSource.setModel({
    type: 'graph',
    id: 'root',
    children: [
        buildNode([buildPort('123')]),
        buildNode([buildPort('456')]),
        buildEdge('123', '456')
    ]
})