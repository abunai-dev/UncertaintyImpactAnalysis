/** @jsx svg */
import { injectable } from "inversify";
import { VNode } from "snabbdom";
import { IViewArgs, RenderingContext, ShapeView, SLabelImpl, SNodeImpl, svg, undoRedoModule } from "sprotty";
import { SNode } from "sprotty-protocol";

export interface AssemblyContextScheme extends SNode {
  name: string
  typeName: string
}

export class AssemblyContextNode extends SNodeImpl {

    public name: string
    public typeName: string

    public getWidth(): number {
      const textLength = Math.max(this.name.length, this.typeName.length)
      return textLength * 10 + 60
    }

}

@injectable()
export class AssemblyContextNodeView extends ShapeView {

  render(model: Readonly<AssemblyContextNode>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }
    return <g  class-sprotty-node={true} x={model.position.x ?? 0} y={model.position.y ?? 0}>
      <rect width={model.getWidth()} height={80}></rect>
      <text y="19" x="40">{'<<'+model.typeName+'>>'}</text>
      <text y="37" x="40">{ model.name }</text>
      
      {this.renderSymbol()}
      {context.renderChildren(model)}
    </g>
  }


  private renderSymbol(): VNode {
    return <g>
      <rect x="15" y="10" width="15" height="20"></rect>
      <rect x="11" y="14" width="8" height="4"></rect>
      <rect x="11" y="22" width="8" height="4"></rect>
    </g>
  }
}