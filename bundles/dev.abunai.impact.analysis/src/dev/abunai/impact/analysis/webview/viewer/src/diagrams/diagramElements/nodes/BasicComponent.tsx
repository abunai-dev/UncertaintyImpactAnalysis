/** @jsx svg */
import { type IViewArgs, type RenderingContext, svg, ShapeView, SNodeImpl } from "sprotty";
import type { BaseNodeVariables } from "./schemes/BaseNode";
import type { VNode } from "snabbdom";
import type { BasicComponentVariables, Seff } from "./schemes/BasicComponent";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";



export class BasicComponentImpl extends SNodeImpl implements BaseNodeVariables, BasicComponentVariables {
  seffs: Seff[];
  typeName: string;
  name: string; 

   get getSelection() {
          return getSelectionMode(this.id)
      }
}

export class BasicComponentView extends ShapeView {

  render(model: Readonly<BasicComponentImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
      return undefined
    }

    const lastSeffY = 93 + (model.seffs.length - 1) * 18

    return <g class-sprotty-node={true} x={model.bounds.x} y={model.bounds.y} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
        <rect width={model.bounds.width} height={model.bounds.height}></rect>
        <text y="19" x="40">
            {'<<' + model.typeName + '>>'}
        </text>
        <text y="37" x="40">
            {model.name}
        </text>

        <line x1="0" y1="45" x2={model.bounds.width} y2="45"></line>
        <text x="8" y="65">SEFFCompartment</text>
        <line x1="0" y1="73" x2={model.bounds.width} y2="73"></line>

        {
          model.seffs.map((signature, index) => {
            return <g transform={`translate(0, ${93 + index * 18})`}>
              <g>
                <polygon points="16,-9 13,-1 8,-1 8,-7"></polygon>
                <ellipse cx="7" cy="-8.5" rx="1" ry="1.5"></ellipse>
                <circle cx="17" cy="-10" r="1" class-do-fill></circle>
                <circle cx="13" cy="-1" r="1" class-do-fill></circle>
                <circle cx="8" cy="-1" r="1" class-do-fill></circle>
                <circle cx="9" cy="-6" r="1" class-do-fill></circle>
              </g>
              <text x="22">
                  {signature.signature}
              </text>
            </g>
          })
        }

        <line x1="0" x2={model.bounds.width} y1={lastSeffY + 8} y2={lastSeffY+8}></line>
        <text x="8" y={lastSeffY+28}>PassiveResourcesCompartment</text>
        <line x1="0" x2={model.bounds.width} y1={lastSeffY+34} y2={lastSeffY+34}></line>
        <line x1="0" x2={model.bounds.width} y1={lastSeffY+48} y2={lastSeffY+48}></line>
        <text x="8" y={lastSeffY+68}>ComponentParameterCompartment</text>
        <line x1="0" x2={model.bounds.width} y1={lastSeffY+76} y2={lastSeffY+76}></line>
        <text x="8" y={lastSeffY+96}>ResourceRequiredRoles</text>
        <line x1="0" x2={model.bounds.width} y1={lastSeffY+102} y2={lastSeffY+102}></line>

        <g class-sprotty-symbol={true}>
        <rect x="15" y="10" width="15" height="20"></rect>
          <rect x="11" y="14" width="8" height="4"></rect>
          <rect x="11" y="22" width="8" height="4"></rect>
        </g>
    </g>
  }

}