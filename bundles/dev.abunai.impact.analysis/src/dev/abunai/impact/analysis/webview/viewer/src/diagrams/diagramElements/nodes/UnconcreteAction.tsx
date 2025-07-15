/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode"
import { svg } from "sprotty";

export class UnconcreteActionView extends BaseNodeView {
  renderSymbol(): VNode {
    return <g></g>
  }

}