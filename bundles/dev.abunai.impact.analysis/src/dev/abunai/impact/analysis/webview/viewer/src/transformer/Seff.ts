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
    const filteredActions = actions.filter(a => a!=null)
    const contents: (SNode|SEdge)[] = []

    for (const start of getOfType<Json.StartNode>(filteredActions, 'Start')) {
      contents.push(buildStartNode(start.id))
    }
    for (const stop of getOfType<Json.StopNode>(filteredActions, 'Stop')) {
      contents.push(buildStopNode(stop.id))
    }
    for (const entryLevelSystemCall of getOfType<Json.EntryLevelSystemCall>(filteredActions, 'EntryLevelSystemCall')) {
      /** Todo: change */
      contents.push(buildBaseNode(
        entryLevelSystemCall.id,
        NODES.LINKING_RESOURCE,
        entryLevelSystemCall.name,
        'EntryLevelSystemCall'
      ))
    }


    for (const action of filteredActions) {
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