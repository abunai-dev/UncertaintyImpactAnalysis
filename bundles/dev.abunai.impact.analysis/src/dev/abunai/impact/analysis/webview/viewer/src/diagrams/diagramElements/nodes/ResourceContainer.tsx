/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class ResourceContainerView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <rect x="14" y="11" width="18" height="18"></rect>
          <rect x="17" y="14" width="12" height="12"></rect>

          <line x1="14" y1="29" x2="10" y2="33"></line>
          <line x1="32" y1="29" x2="36" y2="33"></line>
          <line x1="10" y1="33" x2="36" y2="33"></line>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>