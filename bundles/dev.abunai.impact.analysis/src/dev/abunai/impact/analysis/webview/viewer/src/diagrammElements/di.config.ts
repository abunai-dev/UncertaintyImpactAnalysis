import { ContainerModule } from 'inversify'
import { configureModelElement, configureViewerOptions, PolylineEdgeView, SEdgeImpl, SGraphImpl, SGraphView, SLabelImpl, SLabelView } from 'sprotty'
import { OpenArrowEdgeView } from './edges'

export const diagramCommonModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = {bind, unbind, rebind, isBound}
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureModelElement(context, 'edge:open', SEdgeImpl, OpenArrowEdgeView)
    configureModelElement(context, 'label', SLabelImpl, SLabelView)
    configureViewerOptions(context, {
        needsClientLayout: true
    })
})
