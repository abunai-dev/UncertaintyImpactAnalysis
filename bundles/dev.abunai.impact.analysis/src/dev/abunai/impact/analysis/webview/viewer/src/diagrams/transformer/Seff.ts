import type { SEdge, SGraph, SModelElement, SNode } from "sprotty-protocol";
import { AbstractTransformer, getOfType, type ID, type JsonBase } from "./base";
import { buildStartNode, buildStopNode } from "../diagramElements/nodes/schemes/CircularNodes";
import { EDGES } from "../diagramElements/edges";
import { buildBaseNode, type BaseNode } from "../diagramElements/nodes/schemes/BaseNode";
import { NODES } from "../diagramElements/nodes";
import { layouter } from "../layouting/layouter";
import { TypeRegistry } from "@/model/TypeRegistry";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";
import { buildBranch, buildBranchTransition, buildBranchTransition2, buildEntryLevelSystemCall, buildSetVariableAction, type VariableUsage } from "../diagramElements/nodes/schemes/Seff";
import { NameRegistry } from "@/model/NameRegistry";

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

  export interface ExternalCall extends ActionBase {
    inputParameterUsages: VariableUsage[],
    outputParameterUsages: VariableUsage[],
    type: 'ExternalCall',
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

  export interface Branch extends ActionBase {
    type: 'Branch',
    name: string,
    transitions: (TransitionBranch|BranchTransition)[]
  }

  export interface TransitionBranch extends JsonBase {
    name: string,
    actions: ActionBase[]
  }

  export interface ProbabilisticTransition extends TransitionBranch {
    type: 'ProbabilisticBranchTransition'
    probability: number
  }

  export interface GuardedTransition extends TransitionBranch {
    type: 'GuardedBranchTransition'
    condition: string
  }

  export interface BranchTransition extends JsonBase {
    type: 'BranchTransition',
    name: string,
    behaviour: ScenarioBehaviour,
    probability: number
  }

  export interface ScenarioBehaviour extends JsonBase {
    type: 'ScenarioBehaviour',
    name: '',
    contents: ActionBase[]
  }

  export interface SetVariable extends ActionBase {
    type: 'SetVariable',
    name: string
    variableUsages: VariableUsage[]
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
        const nameRegistry = NameRegistry.getInstance()

    for (const start of getOfType<Json.StartNode>(actions, 'Start')) {
      contents.push(buildStartNode(start.id))
    }
    for (const stop of getOfType<Json.StopNode>(actions, 'Stop')) {
      contents.push(buildStopNode(stop.id))
    }
    for (const entryLevelSystemCall of getOfType<Json.EntryLevelSystemCall>(actions, 'EntryLevelSystemCall')) {
      typeRegistry.registerComponent(entryLevelSystemCall.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      nameRegistry.addName(entryLevelSystemCall.id, entryLevelSystemCall.name)
      contents.push(buildEntryLevelSystemCall(
        entryLevelSystemCall.id,
        NODES.ENTRY_LEVEL_SYSTEM_CALL,
        entryLevelSystemCall.name,
        'EntryLevelSystemCall',
        entryLevelSystemCall.inputParameterUsages.map(this.transformVariableUsage),
        entryLevelSystemCall.outputParameterUsages.map(this.transformVariableUsage)
      ))
    }
    for (const externalCall of getOfType<Json.ExternalCall>(actions, 'ExternalCall')) {
      typeRegistry.registerComponent(externalCall.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      nameRegistry.addName(externalCall.id, externalCall.name)
      contents.push(buildEntryLevelSystemCall(
        externalCall.id,
        NODES.ENTRY_LEVEL_SYSTEM_CALL,
        externalCall.name,
        'ExternalCallAction',
        externalCall.inputParameterUsages.map(this.transformVariableUsage),
        externalCall.outputParameterUsages.map(this.transformVariableUsage)
      ))
    }

    for (const setVariable of getOfType<Json.SetVariable>(actions, 'SetVariable')) {
      typeRegistry.registerComponent(setVariable.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      nameRegistry.addName(setVariable.id, setVariable.name)
      contents.push(buildSetVariableAction(setVariable.id, NODES.SET_VARIABLE, setVariable.name, 'SetVariableAction', setVariable.variableUsages.map(this.transformVariableUsage)))
    }

    for (const branch of getOfType<Json.Branch>(actions, 'Branch')) {
      typeRegistry.registerComponent(branch.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
      nameRegistry.addName(branch.id, branch.name)
      contents.push(buildBranch(branch.id, NODES.BRANCH, branch.name, 'Branch', branch.transitions.map(this.transformBranchTransition)))
    }

    for (const unconcreteAction of getOfType<Json.UnconcreteAction>(actions, 'AbstractAction')) {
      contents.push(buildBaseNode(unconcreteAction.id, NODES.UNCONCRETE_ACTION, unconcreteAction.name, unconcreteAction.typeName.split('.').pop() ?? 'UnknownAction'))
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

  transformBranchTransition(transition: Json.TransitionBranch|Json.BranchTransition): SModelElement {
    const transformer = new SeffTransformer()
    let type = NODES.UNCONCRETE_ACTION
    let bottomText = ''
    if (transition.type === 'ProbabilisticBranchTransition') {
      bottomText = (Math.round((transition as Json.ProbabilisticTransition).probability * 1000) / 1000).toString()
      type = NODES.PROBABILISTIC_BRANCH_TRANSITION
    } else if (transition.type === 'GuardedBranchTransition') {
      bottomText = (transition as Json.GuardedTransition).condition
      type = NODES.GUARDED_BRANCH_TRANSITION
    } else if (transition.type === 'BranchTransition') {
      console.log(transition)
      const behaviour = (transition as Json.BranchTransition).behaviour
      const a = buildBranchTransition2(transition.id, NODES.BRANCH_TRANSTION, (transition as Json.BranchTransition).probability, buildBaseNode(behaviour.id, NODES.SCENARIO_BEHAVIOUR, behaviour.name, '', transformer.transfromActions(behaviour.contents)))
      console.log(a)
      return a
    } 
    else {
      throw new Error('Unknown transition type')
    }

    return buildBranchTransition(transition.id, type, transition.name, transition.type, transformer.transfromActions(transition.actions), bottomText)
  }
}
