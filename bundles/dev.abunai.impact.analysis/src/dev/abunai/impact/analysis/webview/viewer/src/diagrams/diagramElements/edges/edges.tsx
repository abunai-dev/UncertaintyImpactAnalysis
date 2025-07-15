/** @jsx svg */
import { getSelectionMode, SelectionModes } from '@/diagrams/selection/SelectionModes'
import type { VNode } from 'snabbdom'
import { PolylineEdgeView, SEdgeImpl, selectFeature, svg, type IViewArgs, type RenderingContext } from 'sprotty'
import { Point } from 'sprotty-protocol/lib/utils/geometry'

export class CustomEdgeImpl extends SEdgeImpl {
    static readonly DEFAULT_FEATURES = [selectFeature];

    get getSelection() {
        return getSelectionMode(this.id)
    }
}

export class LineEdgeView extends PolylineEdgeView {
    render(edge: Readonly<CustomEdgeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        const r = super.render(edge, context, args)
        if (r === undefined) return undefined
        r.data = { ...r.data, class: {
            ...r.data?.class,
            'selected-component': edge.getSelection == SelectionModes.SELECTED, 
            'other-selected': edge.getSelection == SelectionModes.OTHER
        } }
        return r
    }
}

export class OpenArrowEdgeView extends LineEdgeView {

    override renderAdditionals(edge: SEdgeImpl, segments: Point[], context: RenderingContext) {
        const last = segments.length - 1
        const p1 = segments[last - 1]
        const p2 = segments[last]
        const angle = Math.atan2(p2.y - p1.y, p2.x - p1.x)
        const arrowLength = 10
        const arrow = [
            {
                x: p2.x - arrowLength * Math.cos(angle - Math.PI / 6),
                y: p2.y - arrowLength * Math.sin(angle - Math.PI / 6)
            },
            p2,
            {
                x: p2.x - arrowLength * Math.cos(angle + Math.PI / 6),
                y: p2.y - arrowLength * Math.sin(angle + Math.PI / 6)
            }
        ]
        return [
            svg('polyline', {
                points: arrow.map((p) => `${p.x},${p.y}`).join(' ')
            })
        ]
    }
}
