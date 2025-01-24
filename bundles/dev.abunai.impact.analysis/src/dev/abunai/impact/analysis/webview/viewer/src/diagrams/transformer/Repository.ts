import type { SEdge, SModelElement, SNode } from "sprotty-protocol";
import { FlatMapTransformer, getOfType, type ID, type JsonBase } from "./base";
import { type ActionBase, SeffTransformer } from "./Seff";
import { NODES } from "../diagramElements/nodes";
import { EDGES } from "../diagramElements/edges";
import { buildSignatureNode } from "../diagramElements/nodes/schemes/SignatureNode";
import { buildBasicComponent } from "../diagramElements/nodes/schemes/BasicComponent";
import { TypeRegistry } from "@/model/TypeRegistry";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";
import { NameRegistry } from "@/model/NameRegistry";

namespace Json {
  export interface Repository extends JsonBase {
    type: 'Repository'
    contents: any[]
  }

  export interface BasicComponent extends JsonBase {
    type: 'BasicComponent'
    name: string,
    seffs: SeffSignature[]
    required: ComponentInterfaceConnection[]
    provided: ComponentInterfaceConnection[]
  }

  export interface SeffSignature extends JsonBase {
    signature: string
    type: 'Seff'
    actions: ActionBase[]
  }

  export interface ComponentInterfaceConnection {
    label: string
    goalInterface: ID
  }

  export interface Interface extends JsonBase {
    type: 'Interface'
    name: string
    signatures: string[]
  }

  export interface CompositeDataType extends JsonBase {
    type: 'CompositeDataType'
    signatures: string[]
    contained: string[]
    name: string
  }
}

export type RepositoryFileContent = Json.Repository[]

export class RepositoryTransformer extends FlatMapTransformer<Json.Repository> {

  protected async transformSingle(o: Json.Repository): Promise<SModelElement[]> {
    const typeRegistry = TypeRegistry.getInstance()
        const nameRegistry = NameRegistry.getInstance()
    const content: (SNode|SEdge)[] = []
    const seffTransformer = new SeffTransformer()

    for (const interfac of getOfType<Json.Interface>(o.contents, 'Interface')) {
      typeRegistry.registerComponent(interfac.id, ArchitecturalElementTypeOptionList.INTERFACE)
      nameRegistry.addName(interfac.id, interfac.name)
      content.push(buildSignatureNode(
        interfac.id,
        NODES.INTERFACE,
        interfac.name,
        'Interface',
        interfac.signatures
      ))
    }
    for (const dataType of getOfType<Json.CompositeDataType>(o.contents, 'CompositeDataType')) {
      content.push(buildSignatureNode(
        dataType.id,
        NODES.COMPOSITE_DATA_TYPE,
        dataType.name,
        'CompositeDataType',
        dataType.signatures
      ))
      /** Todo: label */
      for (const contained of dataType.contained)
      content.push({
        type: EDGES.ARROW_OPEN,
        sourceId: dataType.id,
        targetId: contained,
        id: dataType.id + contained
      })
    }
    for (const component of getOfType<Json.BasicComponent>(o.contents, 'BasicComponent')) {
      const seffs = await Promise.all(component.seffs.map(async seff => {
        typeRegistry.registerComponent(seff.id, ArchitecturalElementTypeOptionList.BEHAVIOR_DESCRIPTION)
        nameRegistry.addName(seff.id, seff.signature)
        return {
          id: seff.id,
          signature: seff.signature,
          graph: await seffTransformer.transform(seff.actions)
        }
      }))
      content.push(buildBasicComponent(
        component.id,
        NODES.BASIC_COMPONENT,
        component.name,
        'BasicComponent',
        seffs
      ))
      /** Todo: add labels */
      for (const r of component.required) {
        content.push({
          type: EDGES.ARROW_OPEN,
          sourceId: component.id,
          targetId: r.goalInterface,
          id: component.id + r.goalInterface + r.label
        })
      }
      /** Todo: add labels */
      for (const p of component.provided) {
        content.push({
          type: EDGES.ARROW_OPEN,
          sourceId: component.id,
          targetId: p.goalInterface,
          id: component.id + p.goalInterface + p.label
        })
      }
    }

    return content
  }
}