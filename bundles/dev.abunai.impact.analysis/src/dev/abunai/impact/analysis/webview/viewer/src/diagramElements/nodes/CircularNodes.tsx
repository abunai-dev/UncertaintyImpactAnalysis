/** @jsx svg */
import { SNode } from "sprotty-protocol";
import { NODES } from ".";
import { IViewArgs, RenderingContext, SChildElementImpl, ShapeView, svg } from "sprotty";
import { VNode } from "snabbdom";

const CIRCLE_SIZE = 30

export function buildStartNode(id: string): SNode {
  return {
    id,
    type: NODES.START,
    size: {
      width: CIRCLE_SIZE,
      height: CIRCLE_SIZE
    }
  }
}

export function buildStopNode(id: string): SNode {
  return {
    id,
    type: NODES.STOP,
    size: {
      width: CIRCLE_SIZE,
      height: CIRCLE_SIZE
    }
  }
}

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