/** @jsx svg */
import { SNode } from 'sprotty-protocol'
import { DynamicContainerNode } from '../DynamicContainer'
import { IViewArgs, RectangularNodeView, RenderingContext, svg } from 'sprotty'
import { VNode } from 'snabbdom'

export interface SystemContainerScheme extends SNode {
    name: string
    typeName: string
}

export class SystemContainer extends DynamicContainerNode {
    public name: string
    public typeName: string

    get bounds() {
        const bounds = super.bounds
        return {
            x: bounds.x,
            y: bounds.y - 40,
            width: Math.max(bounds.width, this.getWidth()),
            height: bounds.height + 40
        }
    }

    getWidth(): number {
        const textLength = Math.max(this.name.length, this.typeName.length)
        return textLength * 9 + 60
    }
}

export class SystemContainerView extends RectangularNodeView {
    override render(model: Readonly<SystemContainer>, context: RenderingContext, args?: IViewArgs): VNode {
        return (
            <g class-sprotty-node={true}>
                <rect x={model.bounds.x} y={model.bounds.y} width={model.bounds.width} height={model.bounds.height} />
                <text y={19 + model.bounds.y} x={40 + model.bounds.x}>
                    {'<<' + model.typeName + '>>'}
                </text>
                <text y={37 + model.bounds.y} x={40 + model.bounds.x}>
                    {model.name}
                </text>
                {context.renderChildren(model, context)}
            </g>
        )
    }
}
