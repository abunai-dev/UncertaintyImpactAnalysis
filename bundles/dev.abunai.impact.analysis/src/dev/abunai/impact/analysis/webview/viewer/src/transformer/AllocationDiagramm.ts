import { ResourceContainerScheme } from "../diagrammElements/allocationDiagram/ResourceContainer"
import _json from './allocation.json'

type ID = string

namespace Json {
  export interface AssemblyContext {
    name: string,
    type: "AssemblyContext",
    id: ID
  }

  export interface ResourceContainer {
    name: string,
    contents: AssemblyContext[],
    type: "ResourceContainer",
    id: ID
  }

  export interface AllocationDiagramm {
    type: "AllocationDiagramm",
    id: ID,
    contents: ResourceContainer[]
  }
}

export function transform(): ResourceContainerScheme[] {
  return (_json as Json.AllocationDiagramm[]).flatMap(transformAllocation)
}

function transformAllocation(diagramm: Json.AllocationDiagramm): ResourceContainerScheme[] {
  return diagramm.contents.map(container => {
    return {
      id: container.id,
      name: container.name,
      typeName: 'ResourceContainer',
      type: 'node:allocation:resource_container',
      children: container.contents.map(assembly => {
        return {
          id: assembly.id,
          name: assembly.name,
          typeName: 'Allocation',
          type: 'node:assembly:assemblyContext',
          size: {
            width: 9 * assembly.name.length + 60,
            height: 80
          }
        }
      })
    }
  })
}