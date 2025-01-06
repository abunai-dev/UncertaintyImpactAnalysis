import type { SGraph, SLabel, SModelElement } from "sprotty-protocol"
import { buildBaseNode, type BaseNode } from "./BaseNode"
import { NODES } from ".."


export interface BasicComponent extends BaseNode {}

export interface Seff {
  signature: string
  id: string,
  graph: SGraph
}

export interface SeffLabel extends SLabel {
  graph: SGraph
}

export function buildBasicComponent(id: string, type: NODES, name: string, typeName: string, seffs: Seff[]): BasicComponent {
  return {
    ...buildBaseNode(id, type, name, typeName, seffs.map((s, index) => ({
      id: s.id,
      graph: s.graph,
      text: s.signature,
      type: NODES.SEFF_SIGNATURE_LABEL,
      position: { x: 0, y: 93 + index * 18}
    } as SeffLabel))),
    size: { 
      width: Math.max(name.length, typeName.length + 4, ...seffs.map(s => s.signature.length), 29) * 9 + 60, 
      height: 190 + Math.max(seffs.length * 18, 10) 
    }
  }
}