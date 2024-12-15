/** @jsx svg */
import { injectable } from "inversify";
import { IViewArgs, ShapeView, SPortImpl, RenderingContext, svg, moveFeature } from "sprotty";
import { Bounds, SPort } from "sprotty-protocol";
import { VNode } from "snabbdom";

export interface AssemblyPort extends SPort {
    name: string
}

export class ProvidingAssemblyPort extends SPortImpl {
  override get bounds(): Bounds {
    return {
      x: -4,
      y: -24,
      width: 10,
      height: 10
    }
  }
}

@injectable()
export class ProvidingAssemblyPortView extends ShapeView {

  render(model: Readonly<ProvidingAssemblyPort>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }
    return <g  class-sprotty-port={true}>
      <line x1="4" y1="12" x2="4" y2="24"></line>
      <circle class-no-fill={true} cx="4" cy="4" r="8"></circle>
    </g>
  }

}