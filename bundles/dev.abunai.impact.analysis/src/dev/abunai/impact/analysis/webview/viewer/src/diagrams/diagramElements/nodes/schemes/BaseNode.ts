import type { SModelElement, SNode } from "sprotty-protocol";
import type { NODES } from "..";

export interface BaseNodeVariables {
  typeName: string,
  name: string
}

export interface BaseNode extends SNode, BaseNodeVariables {}

export function buildBaseNode(id: string, type: NODES, name: string, typeName: string, children?: SModelElement[]): BaseNode {
  return {
    id,
    type,
    name,
    typeName,
    size: {
      width: Math.max(name.length, typeName.length + 4) * 9 + 60,
      height: 80
    },
    children
  }
}