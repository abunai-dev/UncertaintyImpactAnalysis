import { SEdge, SModelElement, SNode } from "sprotty-protocol";
import { AbstractTransformer, getOfType, ID, JsonBase } from "./base";
import { buildStartNode, buildStopNode } from "../diagramElements/nodes/CircularNodes";
import { EDGES } from "../diagramElements/edges";
import { buildBaseNode } from "../diagramElements/nodes/BaseNode";
import { NODES } from "../diagramElements/nodes";

namespace Json {

  export interface ActionBase extends JsonBase {
    successor: ID|null
  }

  export interface StartNode extends ActionBase {
    type: 'Start'
  }

  export interface StopNode extends ActionBase {
    type: 'Stop'
  }

  export interface EntryLevelSystemCall extends ActionBase {
    type: 'EntryLevelSystemCall',
    name: string,
  }

  export interface VariableUsage extends JsonBase {
    type: 'VariableUsage',
    referenceName: string
  }
}

export type ActionBase = Json.ActionBase

export class SeffTransformer extends AbstractTransformer<Json.ActionBase> {

  transform(actions: Json.ActionBase[]): SModelElement[] {
    const contents: (SNode|SEdge)[] = []

    for (const start of getOfType<Json.StartNode>(actions, 'Start')) {
      contents.push(buildStartNode(start.id))
    }
    for (const stop of getOfType<Json.StopNode>(actions, 'Stop')) {
      contents.push(buildStopNode(stop.id))
    }
    for (const entryLevelSystemCall of getOfType<Json.EntryLevelSystemCall>(actions, 'EntryLevelSystemCall')) {
      contents.push(buildBaseNode(
        entryLevelSystemCall.id,
        NODES.LINKING_RESOURCE,
        entryLevelSystemCall.name,
        'EntryLevelSystemCall'
      ))
    }


    for (const action of actions) {
      if (action.successor != null) {
        contents.push({
          id: action.id + action.successor,
          type: EDGES.ARROW_OPEN,
          sourceId: action.id,
          targetId: action.successor
        })
      }
    }
    return contents
  }

}