/** @jsx svg */
import { type IViewArgs, type RenderingContext, svg, ShapeView, SNodeImpl } from "sprotty";
import type { BaseNodeVariables } from "./schemes/BaseNode";
import type { VNode } from "snabbdom";
import type { SignatureNodeVariables } from "./schemes/SignatureNode";

export class SignnatureNodeImpl extends SNodeImpl implements SignatureNodeVariables, BaseNodeVariables {
  typeName: string;
  name: string;
  signatures: string[];
}

export abstract class SignatureNodeView extends ShapeView {
  render(model: Readonly<SignnatureNodeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }

    return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        <line x1="0" y1="45" x2={model.bounds.width} y2="45"></line>

        {
          model.signatures.map((signature, index) => {
            return <text y={65 + index * 18} x="8">
                {signature}
            </text>
          })
        }

        {this.renderSymbol()}
    </g>
  }

  abstract renderSymbol(): VNode

}