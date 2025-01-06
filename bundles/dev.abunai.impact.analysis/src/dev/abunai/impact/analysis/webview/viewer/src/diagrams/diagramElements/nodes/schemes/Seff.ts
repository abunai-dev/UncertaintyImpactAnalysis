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