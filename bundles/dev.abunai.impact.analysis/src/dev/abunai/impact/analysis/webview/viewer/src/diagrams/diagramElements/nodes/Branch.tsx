/** @jsx svg */
import { ShapeView, SNodeImpl, type IViewArgs, type RenderingContext, svg } from "sprotty";
import type { BaseNode, BaseNodeVariables } from "./schemes/BaseNode";
import type { BranchTransitionVariables } from "./schemes/Seff";
import type { VNode } from "snabbdom";
import { getSelectionMode, SelectionModes } from "@/diagrams/selection/SelectionModes";
import { BaseNodeImpl, BaseNodeView } from "./BaseNode";

export class BranchView extends BaseNodeView {
    renderSymbol(): VNode {
        return <g class-sprotty-symbol={true}>
            <line x1="15" x2="22" y1="22" y2="15"></line>
            <line x1="29" x2="22" y2="15" y1="22"></line>
            <line x1="29" x2="22" y2="29" y1="22"></line>
            <line x1="15" x2="22" y2="29" y1="22"></line>

            <line x1="22" x2="22" y1="15" y2="10"></line>
            <line x1="25.5" y1="25.5" x2="30" y2="30"></line>
            <line x1="18.5" y1="25.5" x2="14" y2="30"></line>

            <text x="19" y="25" font-size="9">?</text>
        </g>
    }
}

export class BranchTransitionImpl extends BaseNodeImpl implements BranchTransitionVariables {
    bottomText: string;
    typeName: string;
    name: string;

    get bounds() {
        const bounds = super.bounds
        bounds.height += 30
        return bounds
    }

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
        return <text x="40" y={model.bounds.height-10}>{model.bottomText}</text>
    }
    renderSymbol(): VNode {
        return <g class-sprotty-symbol={true}>
            <line y1="10" x1="22" y2="15" x2="14"></line>
            <line y1="10" x1="22" y2="15" x2="30"></line>
            <line y1="15" x1="14" x2="22" y2="20"></line>
            <line y1="15" x1="30" y2="20" x2="22"></line>

            <line y1="15" x1="14" x2="14" y2="25"></line>
            <line y1="15" x1="30" x2="30" y2="25"></line>

            <line y1="25" x1="14" y2="30" x2="22"></line>
            <line y1="25" x1="30" y2="30" x2="22"></line>

            <line y1="20" x1="22" x2="22" y2="30"></line>

            <line y1="27" y2="32" x1="18" x2="13"></line>
            <line y1="27" y2="32" x1="26" x2="31"></line>
        </g>
    }
}

export class GuardedBranchTransitionView extends BaseBranchTransitionView {
    renderBottomText(model: Readonly<BranchTransitionImpl>): VNode {
        return <text x="12" y={model.bounds.height-10}>Cond: {model.bottomText}</text>
    }
    renderSymbol(): VNode {
        return <g class-sprotty-symbol={true}></g>
    }
}