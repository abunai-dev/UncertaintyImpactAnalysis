import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    configureViewerOptions,
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
import { MyModel } from './MyModel'

export const diagramCommonModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }

    bind(TYPES.ModelSource).to(MyModel).inSingletonScope()
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'edge:open', SEdgeImpl, OpenArrowEdgeView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureModelElement(context, 'dynamic', DynamicContainerNode, DynamicOuterNodeView)
    configureViewerOptions(context, {
        needsClientLayout: true,
        needsServerLayout: true
    })
})
