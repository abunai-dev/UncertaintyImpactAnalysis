/** @jsx svg */
import { injectable } from "inversify";
import { VNode } from "snabbdom";
import { IViewArgs, RectangularNodeView, RenderingContext, SChildElementImpl, ShapeView, SNodeImpl, svg } from "sprotty";

export class AssemblyContextNode extends SNodeImpl {

    public name: string = "123"


    public getWidth(): number {
      return this.name.length * 10 + 20
    }

}

@injectable()
export class AssemblyContextNodeView extends ShapeView {

  render(model: Readonly<AssemblyContextNode>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }
    return <g  class-sprotty-node={true}>
      <rect x="0" y="0" width={model.getWidth()} height={80}></rect>
      <text class-sprotty-label={true} x={model.getWidth()/2} y="31">{model.name}</text>
    </g>
  }

}