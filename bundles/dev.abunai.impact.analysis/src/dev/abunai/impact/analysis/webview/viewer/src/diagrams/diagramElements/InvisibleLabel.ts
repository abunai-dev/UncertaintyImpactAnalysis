import type { VNode } from "snabbdom";
import { ShapeView, SNodeImpl, SShapeElementImpl, type IViewArgs, type RenderingContext } from "sprotty";

export class InvisibleLabelImpl extends SShapeElementImpl {
}

export class InvisibleNodeImpl extends SNodeImpl {
  
}

export class InvisibleView extends ShapeView {
  render(model: Readonly<SShapeElementImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
    return undefined
  }

}