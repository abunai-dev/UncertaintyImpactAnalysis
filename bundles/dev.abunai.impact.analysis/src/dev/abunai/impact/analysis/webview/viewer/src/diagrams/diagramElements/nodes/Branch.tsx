/** @jsx svg */
import { ShapeView, SNodeImpl, type IViewArgs, type RenderingContext, svg } from "sprotty";
import type { BaseNode, BaseNodeVariables } from "./schemes/BaseNode";
import type { BranchTransitionVariables } from "./schemes/Seff";
import type { VNode } from "snabbdom";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";
import { BaseNodeView } from "./BaseNode";

export class BranchView extends BaseNodeView {
    renderSymbol(): VNode {
        return <g></g>
    }
}

export class BranchTransitionImpl extends SNodeImpl implements BaseNodeVariables, BranchTransitionVariables {
    bottomText: string;
    typeName: string;
    name: string;

    get getSelection() {
        return getSelectionMode(this.id)
    }
}

abstract class BaseBranchTransitionView extends ShapeView {
    render(model: Readonly<BranchTransitionImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
            return undefined
        }

        return <g class-sprotty-node={true} class-selected-component={model.getSelection == SelectionModes.SELECTED} class-other-selected={model.getSelection == SelectionModes.OTHER}>
            <rect width={model.bounds.width} height={model.bounds.height}></rect>
            <text y="19" x="40">
                {'<<' + model.typeName + '>>'}
            </text>
            <text y="37" x="40">
                {model.name}
            </text>
            <line x1="0" y1="45" x2={model.bounds.width} y2="45"></line>
            <line x1="0" y1={model.bounds.height-30} x2={model.bounds.width} y2={model.bounds.height-30}></line>
            {this.renderBottomText(model)}
            {this.renderSymbol()}
            {context.renderChildren(model)}
        </g>
    }

    abstract renderBottomText(model: Readonly<BranchTransitionImpl>): VNode

    abstract renderSymbol(): VNode
}

export class ProbabilisticBranchTransitionView extends BaseBranchTransitionView {
    renderBottomText(model: Readonly<BranchTransitionImpl>): VNode {
        return <text x="40" y={model.bounds.height-10}>model.bottomText</text>
    }
    renderSymbol(): VNode {
        return <g class-sprotty-symbol={true}></g>
    }
}

export class GuardedBranchTransitionView extends BaseBranchTransitionView {
    renderBottomText(model: Readonly<BranchTransitionImpl>): VNode {
        return <text x="40" y={model.bounds.height-10}>model.bottomText</text>
    }
    renderSymbol(): VNode {
        return <g class-sprotty-symbol={true}></g>
    }
}