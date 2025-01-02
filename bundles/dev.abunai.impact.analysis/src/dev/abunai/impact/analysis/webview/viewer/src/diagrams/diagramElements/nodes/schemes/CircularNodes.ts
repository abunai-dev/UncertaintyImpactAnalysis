import type { SNode } from "sprotty-protocol"
import { NODES } from ".."

export const CIRCLE_SIZE = 30

export function buildStartNode(id: string): SNode {
  return {
    id,
    type: NODES.START,
    size: {
      width: CIRCLE_SIZE,
      height: CIRCLE_SIZE
    }
  }
}

export function buildStopNode(id: string): SNode {
  return {
    id,
    type: NODES.STOP,
    size: {
      width: CIRCLE_SIZE,
      height: CIRCLE_SIZE
    }
  }
}