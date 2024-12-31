/** @jsx svg */
import { VNode } from "snabbdom";
import { IViewArgs, RenderingContext, svg, ShapeView, SNodeImpl } from "sprotty";
import { getBasicType, SModelElement, SNode } from "sprotty-protocol";
import { NODES } from "../nodes";
import { portSnapper, snapPortsOfNode } from "../ports/PortSnapper";

interface BaseNodeVariables {
  typeName: string,
  name: string
}

export interface BaseNode extends SNode, BaseNodeVariables {}

export function buildBaseNode(id: string, type: NODES, name: string, typeName: string, children?: SModelElement[]): BaseNode {
  return {
    id,
    type,
    name,
    typeName,
    children,
    size: {
      width: Math.max(name.length, typeName.length + 4) * 9 + 60,
      height: 80
    }
  }
}

export class BaseNodeImpl extends SNodeImpl implements BaseNodeVariables {
  typeName: string;
  name: string;
  
  get bounds() {
    if (!this.children || this.children.length === 0) {
      return {
        x: this.position.x,
        y: this.position.y,
        width: Math.max(this.name.length, this.typeName.length + 4) * 9 + 60,
        height: 80
      }
    }
    let maxX = Math.max(this.name.length, this.typeName.length + 4) * 9 + 60
    let maxY = 80
    for (const child of this.children.filter(c => getBasicType(c) != 'port')) {
      if (!hasBounds(child)) {
        continue
      }
      maxX = Math.max(maxX, child.bounds.x + child.bounds.width + 50)
      maxY = Math.max(maxY, child.bounds.y + child.bounds.height + 50)
    }
    if (this.type === NODES.ASSEMBLY_CONTEXT) console.log(maxX, maxY, super.bounds)
    return {
      x: this.position.x,
      y: this.position.y,
      width: maxX,
      height: maxY
    }
  }
}

function hasBounds(e: any): e is { bounds: { x: number, y: number, width: number, height: number } } {
  return e.bounds !== undefined 
}

export abstract class BaseNodeView extends ShapeView {

  render(model: Readonly<BaseNodeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
      if (!this.isVisible(model, context)) {
          return undefined
      }

      snapPortsOfNode(model, portSnapper)
   
      return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        {this.renderSymbol()}
        {context.renderChildren(model)}
    </g>
  }

  abstract renderSymbol(): VNode
}