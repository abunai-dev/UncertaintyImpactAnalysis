/** @jsx svg */
import { injectable } from 'inversify'
import { VNode } from 'snabbdom'
import {
    IViewArgs,
    RectangularNodeView,
    SNodeImpl,
    svg,
    RenderingContext
} from 'sprotty'
import { AssemblyContextNode } from './assemblyDiagram/AssemblyContextNode'

export class DynamicContainerNode extends SNodeImpl {
    override hasFeature(feature: symbol): boolean {
        return feature === dynamicContainerFeature || super.hasFeature(feature)
    }

    override get bounds() {
        if (!this.children || this.children.length == 0) return {
            x: this.position.x,
            y: this.position.y,
            width: 80,
            height: 80
        }
        let maxX = -Infinity,
            maxY = -Infinity
        for (const child of this.children) {
            if (child instanceof AssemblyContextNode) {
                const childBounds = (child as AssemblyContextNode).bounds
                maxX = Math.max(maxX, childBounds.x + childBounds.width)
                maxY = Math.max(maxY, childBounds.y + childBounds.height)
            }
        }

        // Add padding
        const padding = 50
        console.log(this.position, maxX, maxY)
        return {
            x: this.position.x,
            y: this.position.y,
            width: maxX + 2 * padding - this.position.x,
            height: maxY + 2 * padding - this.position.y
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
