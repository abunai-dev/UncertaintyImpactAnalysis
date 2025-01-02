import type { SModelElement } from "sprotty-protocol"
import { buildBaseNode, type BaseNode } from "./BaseNode"
import type { NODES } from ".."

export interface BasicComponentVariables {
  seffs: Seff[]
}

export interface BasicComponent extends BaseNode, BasicComponentVariables {}

export interface Seff {
  signature: string
  actions: SModelElement[]
}

export function buildBasicComponent(id: string, type: NODES, name: string, typeName: string, seffs: Seff[]): BasicComponent {
  return {
    ...buildBaseNode(id, type, name, typeName),
    seffs,
    size: { 
      width: Math.max(name.length, typeName.length + 4, ...seffs.map(s => s.signature.length), 29) * 9 + 60, 
      height: 190 + Math.max(seffs.length * 18, 10) 
    }
  }
}