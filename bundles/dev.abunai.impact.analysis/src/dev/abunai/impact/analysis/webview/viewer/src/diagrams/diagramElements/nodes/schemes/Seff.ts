import type { SLabel, SModelElement, SNode, SShapeElement } from "sprotty-protocol"
import { NODES } from ".."
import { type BaseNode, buildBaseNode, buildSizeLabel, buildSizeNode, getHeight, getWidth, type Sizable } from "./BaseNode"

export interface EntryLevelSystemCallVariables {
  input: VariableUsage[]
  output: VariableUsage[]
}

export interface EntryLevelSystemCall extends BaseNode, EntryLevelSystemCallVariables {}

export function buildEntryLevelSystemCall(id: string, type: NODES, name: string, typeName: string, input: VariableUsage[], output: VariableUsage[]): EntryLevelSystemCall {
  return {
    ...buildBaseNode(id, type, name, typeName),
    input,
    output,
    size: {
      width: Math.max(name.length, typeName.length, ...input.map(i => i.topText.length), ...output.map(o => o.topText.length), 25) * 9 + 60,
      height: 100 + Math.max(input.length, 0.15) * 65 + Math.max(output.length, 0.15) * 65
    },
  }
}

export interface VariableUsage {
  topText: string
  bottomText: string
}

export interface SetVariableVariables {
  variableUsages: VariableUsage[]
}

export interface SetVariableAction extends BaseNode, SetVariableVariables {}

export function buildSetVariableAction(id: string, type: NODES, name: string, typeName: string, variableUsages: VariableUsage[]): SetVariableAction {
  return {
    ...buildBaseNode(id, type, name, typeName),
    variableUsages,
    size: {
      width: Math.max(name.length, typeName.length, ...variableUsages.map(v => v.topText.length), ...variableUsages.map(v => v.bottomText.length), 25) * 9 + 60,
      height: 60 + Math.max(variableUsages.length, 0.15) * 65
    },
  }
}



export function buildBranch(id: string, type: NODES, name: string, typeName: string, transitions: SModelElement[]): BaseNode {

  const childSizeSum = transitions.reduce((acc, transition) => acc + getHeight(transition) + 20, 0)
  const childSizeWidth = transitions.reduce((acc, transition) => acc + getWidth(transition) + 20, 0)

  return {
    ...buildBaseNode(id, type, name, typeName, [...transitions, buildSizeNode(id, 1, childSizeSum+70), buildSizeLabel(id, childSizeWidth)]),
  }
}

export interface BranchTransitionVariables {
  bottomText: string
}

export interface BranchTransition extends BaseNode, BranchTransitionVariables {}

export function buildBranchTransition(id: string, type: NODES, name: string, typeName: string, actions: SModelElement[], bottomText: string): BranchTransition {
  const sumOfChildrenWidths = actions.reduce((acc, action) => acc + getWidth(action) + 10, 0)
  const maxChildHeight = actions.reduce((acc, action) => Math.max(acc, (action as unknown as Sizable).size?.height ?? 0), 0)
  const textWidth = Math.max(name.length, typeName.length, bottomText.length, 25) * 9 + 60
  const maxWidth = Math.max(textWidth, sumOfChildrenWidths)
  return {
    ...buildBaseNode(id, type, name, typeName, [...actions, buildSizeLabel(id, maxWidth), buildSizeNode(id, 1, 100)]),
    bottomText,
    size: {
      width: maxWidth,
      height: maxChildHeight + 50
    },
  }
}

export interface BranchTransition2Variables {
  probability: number
}

export interface BranchTransition2 extends SNode, BranchTransition2Variables {}

export function buildBranchTransition2(id: string, type: NODES, probability: number, child: SModelElement): BranchTransition2 {
  return {
    id,
    type,
    probability,
    size: {
      width: getWidth(child) + 20,
      height: getHeight(child) + 60
    },
    children: [child]
  }
}

