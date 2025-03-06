import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    configureViewerOptions,
    LocalModelSource,
    MoveCommand,
    SetViewportCommand,
    SGraphImpl,
    SGraphView,
    SLabelImpl,
    SLabelView,
    SShapeElementImpl,
    TYPES
} from 'sprotty'
import { TabOpenerMouseListener } from './TabOpener'
import { MovementSaver, ZoomSaver } from './MovementSaver'
import { InvisibleLabelImpl, InvisibleNodeImpl, InvisibleView } from './InvisibleLabel'

export const diagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }
    bind(TYPES.ModelSource).to(LocalModelSource).inSingletonScope()
    bind(TYPES.MouseListener).to(TabOpenerMouseListener).inSingletonScope()
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureModelElement(context, 'label:invisible', InvisibleLabelImpl, InvisibleView)
    configureModelElement(context, 'node:invisible', InvisibleNodeImpl, InvisibleView)
    configureViewerOptions(context, {
        needsClientLayout: false,
        needsServerLayout: true
    })
    rebind(MoveCommand).to(MovementSaver)
    rebind(SetViewportCommand).to(ZoomSaver)
})