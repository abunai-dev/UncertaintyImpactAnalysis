import type { SEdge, SGraph, SNode } from "sprotty-protocol";
import { AbstractTransformer, getOfType, type ID, type JsonBase } from "./base";
import { buildStartNode, buildStopNode } from "../diagramElements/nodes/schemes/CircularNodes";
import { EDGES } from "../diagramElements/edges";
import { buildBaseNode } from "../diagramElements/nodes/schemes/BaseNode";
import { NODES } from "../diagramElements/nodes";
import { layouter } from "../layouting/layouter";
import { TypeRegistry } from "@/model/TypeRegistry";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";

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

  async transform(actions: Json.ActionBase[]): Promise<SGraph> {

    return await layouter.layout({
      id: 'root',
      type: 'graph',
      children: this.transfromActions(actions)
    })
  }

  transfromActions(actions: Json.ActionBase[]) {
    const filteredActions = actions.filter(a => a!=null)
    const contents: (SNode|SEdge)[] = []
    const typeRegistry = TypeRegistry.getInstance()

    for (const start of getOfType<Json.StartNode>(filteredActions, 'Start')) {
      contents.push(buildStartNode(start.id))
    }
    for (const stop of getOfType<Json.StopNode>(filteredActions, 'Stop')) {
      contents.push(buildStopNode(stop.id))
    }
    for (const entryLevelSystemCall of getOfType<Json.EntryLevelSystemCall>(filteredActions, 'EntryLevelSystemCall')) {
      typeRegistry.registerComponent(entryLevelSystemCall.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
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
