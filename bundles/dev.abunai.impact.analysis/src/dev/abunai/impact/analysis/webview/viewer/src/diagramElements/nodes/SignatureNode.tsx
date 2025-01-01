/** @jsx svg */
import { IViewArgs, RenderingContext, svg, ShapeView, SNodeImpl } from "sprotty";
import { NODES } from ".";
import { BaseNode, BaseNodeVariables } from "./BaseNode";
import { VNode } from "snabbdom";

interface SignatureNodeVariables {
  signatures: string[]
}

interface SignatureNode extends BaseNode, SignatureNodeVariables {}

export function buildSignatureNode(id: string, type: NODES, name: string, typeName: string, signatures: string[]): SignatureNode {
  return {
    id,
    type,
    name,
    typeName,
    signatures,
    size: {
      width: Math.max(name.length, typeName.length + 4, ...signatures.map(s => s.length - 6)) * 9 + 40,
      height: 60 + Math.max(signatures.length * 18, 10)
    }
  }
}

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