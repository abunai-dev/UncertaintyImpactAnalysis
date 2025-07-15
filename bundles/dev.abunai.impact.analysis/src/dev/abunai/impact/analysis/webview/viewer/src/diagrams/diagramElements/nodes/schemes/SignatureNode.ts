import type { NODES } from ".."
import type { BaseNode } from "./BaseNode"

export interface SignatureNodeVariables {
  signatures: string[]
}

interface SignatureNode extends BaseNode, SignatureNodeVariables {}

export function buildSignatureNode(id: string, type: NODES, name: string, typeName: string, signatures: string[]): SignatureNode {
  return {
    id,
    type,
    name,
    typeName,
    signatures,
    size: {
      width: Math.max(name.length, typeName.length + 4, ...signatures.map(s => s.length - 6)) * 9 + 40,
      height: 60 + Math.max(signatures.length * 18, 10)
    }
  }
}