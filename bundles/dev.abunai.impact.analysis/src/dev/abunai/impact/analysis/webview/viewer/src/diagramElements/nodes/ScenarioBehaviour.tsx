/** @jsx svg */
import { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class ScenarioBehaviourView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <rect x="10" y="10" width="24" height="24"></rect>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>