import type { SLabel, SModelElement, SNode, SShapeElement } from "sprotty-protocol";
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
    children: [...(children || []), buildSizeLabel(id, Math.max(name.length, typeName.length + 4) * 10 + 60)]
  }
}

export function getWidth(m: SModelElement): number {
  return (m as unknown as Sizable).size?.width ?? 0
}

export function getHeight(m: SModelElement): number {
  return (m as unknown as Sizable).size?.height ?? 0
}

export function buildSizeLabel(id: string, width: number): SLabel {
  return {
    type: 'label:invisible',
    id: id+'invisibleLabel' + generateRandomUUID(),
    text: Array.from({length: Math.ceil(width/9)}, () => '0').join(''),
    position: {x: 0, y: 0},
    size: {width, height: 0}
  }
}

export function buildSizeNode(id: string, width: number, height: number): SShapeElement {
  return {
    type: 'node:invisible',
    id: id+'invisibleNode' + generateRandomUUID(),
    position: {x: 0, y: 0},
    size: {width, height}
  }
}

export interface Sizable {
  size?: {
    width: number
    height: number
  }
}

function generateRandomUUID(): string {
  return Math.random().toString(36).substring(0, 7)
}