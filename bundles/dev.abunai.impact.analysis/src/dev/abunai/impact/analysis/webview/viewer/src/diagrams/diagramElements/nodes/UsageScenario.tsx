/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class UsageScenarioView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <line x1="10" y1="34" x2="34" y2="34"></line>

          <line x1="14" y1="34" x2="18" y2="28"></line>
          <line x1="22" y1="34" x2="18" y2="28"></line>
          <line x1="18" y1="28" x2="18" y2="20"></line>
          <line x1="14" y1="23" x2="22" y2="23"></line>
          <circle cx="18" cy="18" r="2"></circle>

          <line x1="28" x2="29" y1="34" y2="20"></line>
          <line x1="32" x2="31" y1="34" y2="20"></line>
          <ellipse cx="30" cy="16" rx="6" ry="6" class-do-fill></ellipse>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>