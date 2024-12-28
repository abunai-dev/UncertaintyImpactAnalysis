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
import { diagramCommonModule } from './diagrammElements/di.config'
import { transform } from './transformer/AssemblyDiagramm'
import { elkLayoutModule } from 'sprotty-elk'
import { autoLayoutModule } from './layouting/di.config'
import { StraightEdgeLayoutEngine } from './layouting/layouter'

const container = new Container()

loadDefaultModules(container)
container.load(elkLayoutModule)
container.load(commonModule, unbindHookModule, assemblyDiagramModule, diagramCommonModule, autoLayoutModule)

const dispatcher = container.get<ActionDispatcher>(TYPES.IActionDispatcher)
const defaultUIElements = container.getAll<AbstractUIExtension>(EDITOR_TYPES.DefaultUIElement)
const layouter = container.get<StraightEdgeLayoutEngine>(TYPES.IModelLayoutEngine)

dispatcher.dispatchAll([
    // Show the default uis after startup
    ...defaultUIElements.map((uiElement) => {
        return SetUIExtensionVisibilityAction.create({
            extensionId: uiElement.id(),
            visible: true
        })
    })
])

async function test() {
    const localModelSource = container.get<LocalModelSource>(TYPES.ModelSource)
    const r = await layouter.layout({
        type: 'graph',
        id: 'root',
        children: transform()
    })
    localModelSource.setModel(r)
}
document.addEventListener('DOMContentLoaded', async () => {
    test()
})
