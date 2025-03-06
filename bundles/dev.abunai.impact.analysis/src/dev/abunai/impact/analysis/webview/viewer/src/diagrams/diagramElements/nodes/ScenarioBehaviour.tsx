/** @jsx svg */
import type { VNode } from "snabbdom";
import { BaseNodeImpl, BaseNodeView } from "./BaseNode";
import { svg, type IViewArgs, type RenderingContext } from "sprotty";
import { SelectionModes } from "@/diagrams/selection/SelectionModes";

export class ScenarioBehaviourView extends BaseNodeView {
  render(model: Readonly<BaseNodeImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
      if (!this.isVisible(model, context)) {
          return undefined
      }
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
        { model.name !== '' ? [<text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>,
        <text y="37" x="40">
            {model.name}
        </text>] : undefined}

        {symbol}
        {context.renderChildren(model)}
    </g>
  }
  
  renderSymbol(): VNode {
    return <g>
        <polygon points="30,14 25,30 14,30 14,18"></polygon>
        <ellipse cx="12" cy="15" rx="2" ry="3"></ellipse>
        <circle cx="32" cy="12" r="2" class-do-fill></circle>
        <circle cx="25" cy="30" r="2" class-do-fill></circle>
        <circle cx="14" cy="30" r="2" class-do-fill></circle>
        <circle cx="16" cy="20" r="2" class-do-fill></circle>
      </g>
  }

} 

// <rect x="10" y="10" width="24" height="24"></rect>