/** @jsx svg */
import { ShapeView, SNodeImpl, type IViewArgs, type RenderingContext, svg } from "sprotty";
import type { BaseNodeVariables } from "./schemes/BaseNode";
import type { SetVariableVariables, VariableUsage } from "./schemes/Seff";
import type { VNode } from "snabbdom";
import { renderVariableUsage } from "./EntryLevelSystemCall";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";

export class SetVariableImpl extends SNodeImpl implements BaseNodeVariables, SetVariableVariables {
    variableUsages: VariableUsage[];
  typeName: string;
  name: string; 

   get getSelection() {
          return getSelectionMode(this.id)
      }
}

export class SetVariableView extends ShapeView {
  render(model: Readonly<SetVariableImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }

    return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        {
          model.variableUsages.map((input, index) => renderVariableUsage(input, 55 + index * 65, true, model.getSelection != SelectionModes.NONE))
        }

      <g class-sprotty-symbol={true}>
      </g>
    </g>
  }
}