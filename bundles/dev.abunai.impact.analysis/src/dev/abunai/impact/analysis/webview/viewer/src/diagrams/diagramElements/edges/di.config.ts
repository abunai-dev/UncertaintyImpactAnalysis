import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    PolylineEdgeView,
    SEdgeImpl
} from 'sprotty'
import { CustomEdgeImpl, LineEdgeView, OpenArrowEdgeView } from './edges'
import { EDGES } from '.'

export const edgeModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }
    configureModelElement(context, EDGES.ARROW_OPEN, CustomEdgeImpl, OpenArrowEdgeView)
    configureModelElement(context, EDGES.LINE, CustomEdgeImpl, LineEdgeView)
})
