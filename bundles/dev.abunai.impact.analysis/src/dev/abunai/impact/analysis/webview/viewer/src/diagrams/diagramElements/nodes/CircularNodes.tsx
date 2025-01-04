/** @jsx svg */
import { type IViewArgs, type RenderingContext, SChildElementImpl, ShapeView, SNodeImpl, svg } from "sprotty";
import type { VNode } from "snabbdom";
import { CIRCLE_SIZE } from "./schemes/CircularNodes";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";

export class CircularNodeImpl extends SNodeImpl {
  get getSelection() {
      return getSelectionMode(this.id)
  }
}

export class StartNodeView extends ShapeView {

  render(model: Readonly<CircularNodeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return <g class-sprotty-node={true} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2} class-do-fill={true}></circle>
      </g>
  }
}

export class StopNodeView extends ShapeView {

  render(model: Readonly<CircularNodeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return <g class-sprotty-node={true} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2}></circle>
      <circle cx={CIRCLE_SIZE / 2} cy={CIRCLE_SIZE / 2} r={CIRCLE_SIZE / 2 - 4} class-do-fill={true}></circle>
      </g>
  }
}