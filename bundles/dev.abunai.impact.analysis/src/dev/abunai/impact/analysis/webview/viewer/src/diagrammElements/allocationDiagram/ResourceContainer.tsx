/** @jsx svg */
import { SNode } from "sprotty-protocol";
import { DynamicContainerNode } from "../DynamicContainer";
import { IViewArgs, RenderingContext, SChildElementImpl, ShapeView, svg } from "sprotty";
import { injectable } from "inversify";
import { VNode } from "snabbdom";

export interface ResourceContainerScheme extends SNode {
  name: string
  typeName: string
}

export class ResourceContainerNode extends DynamicContainerNode {
  public name: string
  public typeName: string

  get bounds() {
      const bounds = super.bounds
      return {
          x: bounds.x,
          y: bounds.y,
          width: Math.max(bounds.width, this.getWidth()),
          height: bounds.height
      }
  }

  getWidth(): number {
      const textLength = Math.max(this.name.length, this.typeName.length)
      return textLength * 9 + 60
  }
}

@injectable()
export class ResourceContainerView extends ShapeView {
  render(model: Readonly<ResourceContainerNode>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
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