import { ContainerModule } from 'inversify'
import {
    configureModelElement,
    PolylineEdgeView,
    SEdgeImpl
} from 'sprotty'
import { OpenArrowEdgeView } from './edges'
import { EDGES } from '.'

export const edgeModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, rebind, isBound }
    configureModelElement(context, EDGES.ARROW_OPEN, SEdgeImpl, OpenArrowEdgeView)
    configureModelElement(context, EDGES.LINE, SEdgeImpl, PolylineEdgeView)
})
