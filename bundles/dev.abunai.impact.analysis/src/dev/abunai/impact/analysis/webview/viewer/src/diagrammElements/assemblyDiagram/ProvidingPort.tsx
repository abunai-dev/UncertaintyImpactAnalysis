/** @jsx svg */
import { injectable } from "inversify";
import { IViewArgs, ShapeView, SPortImpl, RenderingContext, svg, moveFeature } from "sprotty";
import { Bounds, SPort } from "sprotty-protocol";
import { VNode } from "snabbdom";

export interface AssemblyPort extends SPort {
    name: string
}

export class ProvidingAssemblyPort extends SPortImpl {

}

@injectable()
export class ProvidingAssemblyPortView extends ShapeView {

  render(model: Readonly<ProvidingAssemblyPort>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }
    return <g  class-sprotty-port={true}>
      <line x1="0" y1="0" x2="0" y2="-12"></line>
      <circle class-no-fill={true} cx="0" cy="-20" r="8"></circle>
    </g>
  }

}