import { type BaseNode, buildBaseNode } from "../diagramElements/nodes/schemes/BaseNode";
import { NODES } from "../diagramElements/nodes";
import { FlatMapTransformer, type JsonBase } from "./base";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";
import { TypeRegistry } from "@/model/TypeRegistry";

namespace Json {
  export interface AssemblyContext extends JsonBase {
    type: 'AssemblyContext',
    name: string
  }

  export interface ResourceContainer extends JsonBase {
    type: 'ResourceContainer',
    name: string,
    contents: AssemblyContext[]
  }

  export interface AllocationDiagram extends JsonBase {
    type: 'AllocationDiagramm',
    contents: ResourceContainer[]
  }
}

export type AllocationFileContent = Json.AllocationDiagram[]

export class AllocationTransformer extends FlatMapTransformer<Json.AllocationDiagram> {
  transformSingle(diagram: Json.AllocationDiagram): BaseNode[] {
    const typeRegistry = TypeRegistry.getInstance()
    return diagram.contents.map(container => {
      typeRegistry.registerComponent(container.id, ArchitecturalElementTypeOptionList.EXTERNAL_RESOURCE)
      return buildBaseNode(
        container.id,
        NODES.RESOURCE_CONTAINER,
        container.name,
        'ResourceContainer',
        container.contents.map(assembly => buildBaseNode(
          assembly.id,
          NODES.ASSEMBLY_CONTEXT,
          assembly.name,
          'Allocation'
        ))
      )
    })
  }

}