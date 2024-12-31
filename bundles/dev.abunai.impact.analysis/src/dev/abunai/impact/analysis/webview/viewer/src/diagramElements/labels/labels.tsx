/** @jsx svg */
import { VNode } from "snabbdom";
import { IViewArgs, RenderingContext, SChildElementImpl, ShapeView, SLabelImpl, SLabelView, svg } from "sprotty";
import { SLabel } from "sprotty-protocol";


/*
export class NameLabelView extends SLabelView {
  render(model: Readonly<SChildElementImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return <text class-sprotty-label={true} x={model.position.x} y={model.position.y} text-anchor="middle">{model.text}</text>
  }
}*/