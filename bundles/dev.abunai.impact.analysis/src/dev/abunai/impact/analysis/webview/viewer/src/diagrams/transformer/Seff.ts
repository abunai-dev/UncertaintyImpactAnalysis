import type { SEdge, SGraph, SNode } from "sprotty-protocol";
import { AbstractTransformer, getOfType, type ID, type JsonBase } from "./base";
import { buildStartNode, buildStopNode } from "../diagramElements/nodes/schemes/CircularNodes";
import { EDGES } from "../diagramElements/edges";
import { buildBaseNode } from "../diagramElements/nodes/schemes/BaseNode";
import { NODES } from "../diagramElements/nodes";
import { layouter } from "../layouting/layouter";
import { TypeRegistry } from "@/model/TypeRegistry";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";
import { buildEntryLevelSystemCall, type VariableUsage } from "../diagramElements/nodes/schemes/Seff";

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
    inputParameterUsages: VariableUsage[],
    outputParameterUsages: VariableUsage[],
    type: 'EntryLevelSystemCall',
    name: string,
  }

  export interface UnconcreteAction extends ActionBase {
    type: 'AbstractAction',
    typeName: string,
    name: string
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
    const contents: (SNode|SEdge)[] = []
    const typeRegistry = TypeRegistry.getInstance()

    for (const start of getOfType<Json.StartNode>(actions, 'Start')) {
      contents.push(buildStartNode(start.id))
    }
    for (const stop of getOfType<Json.StopNode>(actions, 'Stop')) {
      contents.push(buildStopNode(stop.id))
    }
    for (const entryLevelSystemCall of getOfType<Json.EntryLevelSystemCall>(actions, 'EntryLevelSystemCall')) {
      typeRegistry.registerComponent(entryLevelSystemCall.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      /** Todo: change */
      contents.push(buildEntryLevelSystemCall(
        entryLevelSystemCall.id,
        NODES.ENTRY_LEVEL_SYSTEM_CALL,
        entryLevelSystemCall.name,
        'EntryLevelSystemCall',
        entryLevelSystemCall.inputParameterUsages.map(this.transformVariableUsage),
        entryLevelSystemCall.outputParameterUsages.map(this.transformVariableUsage)
      ))
    }

    for (const unconcreteAction of getOfType<Json.UnconcreteAction>(actions, 'AbstractAction')) {
      typeRegistry.registerComponent(unconcreteAction.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      contents.push(buildBaseNode(unconcreteAction.id, NODES.UNCONCRETE_ACTION, unconcreteAction.name, unconcreteAction.typeName))
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

  transformVariableUsage(variableUsage: Json.VariableUsage): VariableUsage {
    return {
      topText: variableUsage.referenceName,
      bottomText: 'PlaceHolder'
    }
  }
}
