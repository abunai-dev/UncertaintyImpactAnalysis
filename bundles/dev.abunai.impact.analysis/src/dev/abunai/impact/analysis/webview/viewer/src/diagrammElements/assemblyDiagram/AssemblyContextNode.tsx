/** @jsx svg */
import { injectable } from 'inversify'
import { VNode } from 'snabbdom'
import { IViewArgs, RenderingContext, ShapeView, SLabelImpl, SNodeImpl, svg, undoRedoModule } from 'sprotty'
import { SNode } from 'sprotty-protocol'

export interface AssemblyContextScheme extends SNode {
    name: string
    typeName: string
}

export class AssemblyContextNode extends SNodeImpl {
    public name: string
    public typeName: string

    get bounds() {
        return {
            x: this.position.x,
            y: this.position.y,
            width: this.getWidth(),
            height: 80
        }
    }

    getWidth(): number {
        const textLength = Math.max(this.name.length, this.typeName.length)
        return textLength * 9 + 60
    }
}

@injectable()
export class AssemblyContextNodeView extends ShapeView {
    render(model: Readonly<AssemblyContextNode>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
            return undefined
        }
        return (
            <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y}>
                <rect width={model.bounds.width} height={model.bounds.height}></rect>
                <text y="19" x="40">
                    {'<<' + model.typeName + '>>'}
                </text>
                <text y="37" x="40">
                    {model.name}
                </text>

                {this.renderSymbol()}
                {context.renderChildren(model)}
            </g>
        )
    }

    private renderSymbol(): VNode {
        return (
            <g>
                <rect x="15" y="10" width="15" height="20"></rect>
                <rect x="11" y="14" width="8" height="4"></rect>
                <rect x="11" y="22" width="8" height="4"></rect>
            </g>
        )
    }
}
