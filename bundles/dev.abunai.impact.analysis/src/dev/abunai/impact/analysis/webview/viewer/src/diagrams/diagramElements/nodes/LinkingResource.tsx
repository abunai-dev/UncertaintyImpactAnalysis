/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class LinkingResourceView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <line x1="10" y1="10" x2="16" y2="16"></line>
          <line x1="10" y1="10" x2="10" y2="18"></line>
          <line x1="10" y1="10" x2="17" y2="10"></line>

          <line x1="34" y1="10" x2="28" y2="16"></line>
          <line x1="34" y1="10" x2="34" y2="18"></line>
          <line x1="34" y1="10" x2="27" y2="10"></line>
          
          <line x1="10" y1="34" x2="16" y2="28"></line>
          <line x1="10" y1="34" x2="10" y2="26"></line>
          <line x1="10" y1="34" x2="17" y2="34"></line>

          <line x1="34" y1="34" x2="28" y2="28"></line>
          <line x1="34" y1="34" x2="34" y2="26"></line>
          <line x1="34" y1="34" x2="27" y2="34"></line>

          <rect x="16" y="16" width="12" height="12"></rect>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>