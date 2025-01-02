/** @jsx svg */
import { type IViewArgs, type RenderingContext, SChildElementImpl, ShapeView, svg } from "sprotty";
import type { VNode } from "snabbdom";
import { CIRCLE_SIZE } from "./schemes/CircularNodes";


export class StartNodeView extends ShapeView {

  render(model: Readonly<SChildElementImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return <g class-sprotty-node={true}>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2} class-do-fill={true}></circle>
      </g>
  }
}

export class StopNodeView extends ShapeView {

  render(model: Readonly<SChildElementImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return <g class-sprotty-node={true}>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2}></circle>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2 - 4} class-do-fill={true}></circle>
      </g>
  }
}