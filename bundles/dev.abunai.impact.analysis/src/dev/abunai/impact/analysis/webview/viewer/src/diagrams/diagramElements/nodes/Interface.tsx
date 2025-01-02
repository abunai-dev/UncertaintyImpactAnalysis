/** @jsx svg */
import { SignatureNodeView } from "./SignatureNode";
import { svg } from "sprotty";

export class InterfaceView extends SignatureNodeView {
  renderSymbol() {
    return <g>
        <polygon points="18,14 25,14 30,19 30,26 25,31 18,31 13,26 13,19"></polygon>
        <line x1="21.5" y1="18" x2="21.5" y2="27"></line>
        <line x1="19" x2="24" y1="27" y2="27"></line>
        <line x1="19" x2="24" y1="18" y2="18"></line>
      </g>
  }
}