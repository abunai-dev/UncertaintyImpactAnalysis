/** @jsx svg */
import type { VNode } from "snabbdom";
import { type IViewArgs, type RenderingContext, svg, ShapeView, SNodeImpl } from "sprotty";
import { getBasicType } from "sprotty-protocol";
import { portSnapper, snapPortsOfNode } from "../ports/PortSnapper";
import type { BaseNodeVariables } from "./schemes/BaseNode";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";

export class BaseNodeImpl extends SNodeImpl implements BaseNodeVariables {
  typeName: string = '';
  name: string = '';
  
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
      maxX = Math.max(maxX, child.bounds.x + child.bounds.width + 20)
      maxY = Math.max(maxY, child.bounds.y + child.bounds.height + 20)
    }
    return {
      x: this.position.x,
      y: this.position.y,
      width: maxX,
      height: maxY
    }
  }

  get getSelection() {
    return getSelectionMode(this.id)
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
      const symbol = this.renderSymbol()
      symbol.data = {
        ...symbol.data,
        class: {
          ...symbol.data?.class,
          'sprotty-symbol': true
        }
      }

      return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        {symbol}
        {context.renderChildren(model)}
    </g>
  }

  abstract renderSymbol(): VNode
}