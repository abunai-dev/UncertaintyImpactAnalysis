import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    configureViewerOptions,
    LocalModelSource,
    SGraphImpl,
    SGraphView,
    SLabelImpl,
    SLabelView,
    TYPES
} from 'sprotty'

export const diagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }
    bind(TYPES.ModelSource).to(LocalModelSource).inSingletonScope()
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureViewerOptions(context, {
        needsClientLayout: false,
        needsServerLayout: true
    })
})