import 'reflect-metadata'

import './theme.css'
import './page.css'
import 'sprotty/css/sprotty.css'
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

const container = new Container()

loadDefaultModules(container)

container.load(commonModule)

const dispatcher = container.get<ActionDispatcher>(TYPES.IActionDispatcher)
const defaultUIElements = container.getAll<AbstractUIExtension>(EDITOR_TYPES.DefaultUIElement)

dispatcher.dispatchAll([
    // Show the default uis after startup
    ...defaultUIElements.map((uiElement) => {
        return SetUIExtensionVisibilityAction.create({
            extensionId: uiElement.id(),
            visible: true
        })
    })
])

/*const modelSource = container.get<LocalModelSource>(TYPES.ModelSource);

modelSource
    .setModel({
        type: "graph",
        id: "root",
        children: [],
    })*/
