/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeView } from "./BaseNode";
import { svg } from "sprotty";

export class SystemView extends BaseNodeView {

  renderSymbol(): VNode {
    return <g>
          <rect x="14" y="10" width="14" height="24"></rect>

          <line x1="14" y1="16" x2="10" y2="16"></line>
          <rect x="6" y="14" width="4" height="4"></rect>
          <line x1="14" y1="26" x2="10" y2="26"></line>
          <rect x="6" y="24" width="4" height="4"></rect>

          <line x1="28" y1="16" x2="32" y2="16"></line>
          <path d=" M 34 18 A 2 2 90 0 1 34 14" />
          <line x1="28" y1="26" x2="32" y2="26"></line>
          <path d=" M 34 28 A 2 2 90 0 1 34 24" />

          <text x="15" y="28">S</text>
      </g>
  }

} 