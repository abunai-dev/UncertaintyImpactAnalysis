/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class AssemblyContextView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <rect x="15" y="10" width="15" height="20"></rect>
          <rect x="11" y="14" width="8" height="4"></rect>
          <rect x="11" y="22" width="8" height="4"></rect>
      </g>
  }

} 