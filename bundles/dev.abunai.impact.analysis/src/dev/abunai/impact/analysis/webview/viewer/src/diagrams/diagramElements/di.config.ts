import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    configureViewerOptions,
    LocalModelSource,
    MoveCommand,
    SGraphImpl,
    SGraphView,
    SLabelImpl,
    SLabelView,
    TYPES
} from 'sprotty'
import { TabOpenerMouseListener } from './TabOpener'
import { MovementSaver } from './MovementSaver'

export const diagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }
    bind(TYPES.ModelSource).to(LocalModelSource).inSingletonScope()
    bind(TYPES.MouseListener).to(TabOpenerMouseListener).inSingletonScope()
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureViewerOptions(context, {
        needsClientLayout: false,
        needsServerLayout: true
    })
    rebind(MoveCommand).to(MovementSaver)
})