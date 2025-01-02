/** @jsx svg */
import { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class ScenarioBehaviourView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
        <polygon points="30,14 25,30 14,30 14,18"></polygon>
        <ellipse cx="12" cy="15" rx="2" ry="3"></ellipse>
        <circle cx="32" cy="12" r="2" class-do-fill></circle>
        <circle cx="25" cy="30" r="2" class-do-fill></circle>
        <circle cx="14" cy="30" r="2" class-do-fill></circle>
        <circle cx="16" cy="20" r="2" class-do-fill></circle>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>