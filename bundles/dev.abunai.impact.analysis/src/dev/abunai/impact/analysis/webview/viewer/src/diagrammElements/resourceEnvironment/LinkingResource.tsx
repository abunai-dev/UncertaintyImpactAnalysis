/** @jsx svg */
import { injectable } from "inversify";
import { ShapeView, SNodeImpl, svg, RenderingContext, IViewArgs } from "sprotty";
import { SNode } from "sprotty-protocol";
import { VNode } from "snabbdom";

export interface LinkingResourceScheme extends SNode {
  typeName: string
  name: string
}

export class LinkingResourceNode extends SNodeImpl {
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
    return this.name.length * 9 + 60
  }
}

@injectable()
export class LinkingResourceView extends ShapeView {
    render(model: Readonly<LinkingResourceNode>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
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
                {context.renderChildren(model)}
            </g>
        )
    }
  }