/** @jsx svg */
import { SignatureNodeView } from "./SignatureNode";
import { svg } from "sprotty";

export class CompositeDataTypeView extends SignatureNodeView {
  renderSymbol() {
    return <g>
      
        <path d="M30 26 L25 31 L18 31 L13 26 L13 19 L18 14 L25 14 M30 19 L30 26" />

        <text x="26" y="23" style={{'font-size': '12px'}}>*</text>  

        <text x="17" y="26.3" style={{'font-size': '12px'}}>G</text>
      </g>
  }
}
//30,19 
/*

<line x1="26" y1="15" x2="29" y2="18" style={{'stroke-width': '0.5'}}></line>
        <line x1="26" y1="18" x2="29" y2="15" style={{'stroke-width': '0.5'}}></line>
        <line x1="27.5" x2="27.5" y1="14" y2="19" style={{'stroke-width': '0.5'}}></line>
        <line x1="25" x2="30" y1="16.5" y2="16.5" style={{'stroke-width': '0.5'}}></line>
        */