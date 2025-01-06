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

      <g class-sprotty-symbol={true}>
        <circle cx="15" cy="22" r="5" class-do-fill={true}></circle>
        <line x1="20" x2="25" y1="22" y2="22"></line>
        <rect x="25" y="19" height="6" width="2" class-do-fill={true}></rect>
        <path d=" M 33 31 A 10 10 120 0 1 33 13" />
      </g>
    </g>
  }
}

function renderVariableUsage(variableUsage: VariableUsage, baseY: number, drawSeperatorLine: boolean, passive: boolean): VNode {
  const width = Math.max(variableUsage.topText.length + 2, variableUsage.bottomText.length) * 9 + 16
  return <g class-always-passive={passive}>
    <rect x="8" y={baseY} width={width} height="50"></rect>
    <text x="32" y={baseY+19}>{variableUsage.topText}</text>
    <text x="12" y={baseY+44}>{variableUsage.bottomText}</text>
    {drawSeperatorLine ? <line x1="8" y1={baseY+25} x2={width+8} y2={baseY+25}></line> : null}

    <text x="14" y={baseY+16} style={{'font-size': '11px'}}>$x</text>
    <line x1="18" x2="22" y1={baseY+5} y2={baseY+5}></line>
    <line x1="12" x2="16" y1={baseY+5} y2={baseY+5}></line>
    <line x1="24" x2="28" y1={baseY+5} y2={baseY+5}></line>

    
    <line x1="18" x2="22" y1={baseY+21} y2={baseY+21}></line>
    <line x1="12" x2="16" y1={baseY+21} y2={baseY+21}></line>
    <line x1="24" x2="28" y1={baseY+21} y2={baseY+21}></line>

    <line x1="12" x2="12" y1={baseY+5} y2={baseY+9}></line>
    <line x1="12" x2="12" y1={baseY+11} y2={baseY+15}></line>
    <line x1="12" x2="12" y1={baseY+17} y2={baseY+21}></line>

    
    <line x1="28" x2="28" y1={baseY+5} y2={baseY+9}></line>
    <line x1="28" x2="28" y1={baseY+11} y2={baseY+15}></line>
    <line x1="28" x2="28" y1={baseY+17} y2={baseY+21}></line>
  </g>
  return result
}