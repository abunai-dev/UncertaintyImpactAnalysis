/** @jsx svg */
import { IViewArgs, RenderingContext, SChildElementImpl, ShapeView, SNodeImpl, svg } from "sprotty";
import { BaseNodeVariables } from "./schemes/BaseNode";
import { EntryLevelSystemCallVariables, VariableUsage } from "./schemes/Seff";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";
import { VNode } from "snabbdom";

export class EntryLevelSystemCallImpl extends SNodeImpl implements BaseNodeVariables, EntryLevelSystemCallVariables {
  input: VariableUsage[];
  output: VariableUsage[];
  typeName: string;
  name: string; 

   get getSelection() {
          return getSelectionMode(this.id)
      }
}

export class EntryLevelSystemCallView extends ShapeView {
  render(model: Readonly<EntryLevelSystemCallImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    if (!this.isVisible(model, context)) {
      return undefined
    }

    const startOfOutput = 73 + model.input.length * 65

    return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        <line x1="0" y1="45" x2={model.bounds.width} y2="45"></line>
        <text x="8" y="65">InputVariableUsageCompartment</text>
        {
          model.input.map((input, index) => renderVariableUsage(input, 75 + index * 65, false, model.getSelection != SelectionModes.NONE))
        }
        <line x1="0" x2={model.bounds.width} y1={startOfOutput} y2={startOfOutput}></line>
        <text x="8" y={startOfOutput+20}>OutputVariableUsageCompartment</text>
        {
          model.output.map((output, index) => renderVariableUsage(output, startOfOutput + 32 + index * 65, true, model.getSelection != SelectionModes.NONE))
        }
    </g>
  }
}

function renderVariableUsage(variableUsage: VariableUsage, baseY: number, drawSeperatorLine: boolean, passive: boolean): VNode {
  const width = Math.max(variableUsage.topText.length + 2, variableUsage.bottomText.length) * 9 + 16
  return <g class-always-passive={passive}>
    <rect x="8" y={baseY} width={width} height="50"></rect>
    <text x="12" y={baseY+19}>{variableUsage.topText}</text>
    <text x="12" y={baseY+44}>{variableUsage.bottomText}</text>
    {drawSeperatorLine ? <line x1="8" y1={baseY+25} x2={width+8} y2={baseY+25}></line> : null}
  </g>
  return result
}