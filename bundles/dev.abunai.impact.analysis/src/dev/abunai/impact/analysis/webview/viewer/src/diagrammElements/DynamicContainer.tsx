/** @jsx svg */
import { injectable } from 'inversify'
import { VNode } from 'snabbdom'
import {
    IViewArgs,
    RectangularNode,
    RectangularNodeView,
    SNodeImpl,
    svg,
    RenderingContext,
    IVNodePostprocessor
} from 'sprotty'
import { SNode } from 'sprotty-protocol'
import { AssemblyContextNode } from './assemblyDiagram/AssemblyContextNode'

export class DynamicContainerNode extends SNodeImpl {
    override hasFeature(feature: symbol): boolean {
        return feature === dynamicContainerFeature || super.hasFeature(feature)
    }

    override get bounds() {
        if (!this.children) return super.bounds
        let minX = Infinity,
            minY = Infinity,
            maxX = -Infinity,
            maxY = -Infinity
        for (const child of this.children) {
            if (child instanceof AssemblyContextNode) {
                const childBounds = (child as AssemblyContextNode).bounds
                minX = Math.min(minX, childBounds.x)
                minY = Math.min(minY, childBounds.y)
                maxX = Math.max(maxX, childBounds.x + childBounds.width)
                maxY = Math.max(maxY, childBounds.y + childBounds.height)
            }
        }

        // Add padding
        const padding = 50
        return {
            x: minX - padding,
            y: minY - padding,
            width: maxX - minX + 2 * padding,
            height: maxY - minY + 2 * padding
        }
    }
}

export const dynamicContainerFeature = Symbol('dynamicContainerFeature')

@injectable()
export class DynamicOuterNodeView extends RectangularNodeView {
    override render(model: Readonly<DynamicContainerNode>, context: RenderingContext, args?: IViewArgs): VNode {
        return (
            <g class-sprotty-node={true}>
                <rect x={model.bounds.x} y={model.bounds.y} width={model.bounds.width} height={model.bounds.height} />
                {context.renderChildren(model, context)}
            </g>
        )
    }
}
