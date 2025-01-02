import type { SEdge, SModelElement, SNode } from "sprotty-protocol";
import { FlatMapTransformer, getOfType, type ID, type JsonBase } from "./base";
import { type ActionBase, SeffTransformer } from "./Seff";
import { NODES } from "../diagramElements/nodes";
import { EDGES } from "../diagramElements/edges";
import { buildSignatureNode } from "../diagramElements/nodes/schemes/SignatureNode";
import { buildBasicComponent } from "../diagramElements/nodes/schemes/BasicComponent";

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

  protected transformSingle(o: Json.Repository): SModelElement[] {
    const content: (SNode|SEdge)[] = []
    const seffTransformer = new SeffTransformer()

    for (const interfac of getOfType<Json.Interface>(o.contents, 'Interface')) {
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
      /** Todo: label and fix export */
      for (const contained of dataType.contained)
      content.push({
        type: EDGES.ARROW_OPEN,
        sourceId: dataType.id,
        targetId: contained,
        id: dataType.id + contained
      })
    }
    for (const component of getOfType<Json.BasicComponent>(o.contents, 'BasicComponent')) {
      content.push(buildBasicComponent(
        component.id,
        NODES.BASIC_COMPONENT,
        component.name,
        'BasicComponent',
        component.seffs.map(seff => {
          return {
            signature: seff.signature,
            actions: seffTransformer.transform(seff.actions)
          }
        })
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