import type { SModelElement } from "sprotty-protocol"
import { NODES } from ".."
import { type BaseNode, buildBaseNode } from "./BaseNode"

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



export function buildBranch(id: string, type: NODES, name: string, typeName: string, transitions: BaseNode[]): BaseNode {
  return {
    ...buildBaseNode(id, type, name, typeName, transitions),
  }
}

export interface BranchTransitionVariables {
  bottomText: string
}

export interface BranchTransition extends BaseNode, BranchTransitionVariables {}

export function buildBranchTransition(id: string, type: NODES, name: string, typeName: string, actions: SModelElement[], bottomText: string): BranchTransition {
  return {
    ...buildBaseNode(id, type, name, typeName, actions),
    bottomText,
    size: {
      width: Math.max(name.length, typeName.length, bottomText.length, 25) * 9 + 60,
      height: 60
    },
  }
}