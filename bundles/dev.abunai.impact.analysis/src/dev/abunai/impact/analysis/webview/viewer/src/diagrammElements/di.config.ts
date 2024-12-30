import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    configureViewerOptions,
    LocalModelSource,
    PolylineEdgeView,
    SEdgeImpl,
    SGraphImpl,
    SGraphView,
    SLabelImpl,
    SLabelView,
    TYPES
} from 'sprotty'
import { OpenArrowEdgeView } from './edges'
import { DynamicContainerNode, DynamicOuterNodeView } from './DynamicContainer'

export const diagramCommonModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }

    bind(TYPES.ModelSource).to(LocalModelSource).inSingletonScope()
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'edge:open', SEdgeImpl, OpenArrowEdgeView)
    configureModelElement(context, 'edge:line', SEdgeImpl, PolylineEdgeView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureModelElement(context, 'dynamic', DynamicContainerNode, DynamicOuterNodeView)
    configureViewerOptions(context, {
        needsClientLayout: true,
        needsServerLayout: true
    })
})
