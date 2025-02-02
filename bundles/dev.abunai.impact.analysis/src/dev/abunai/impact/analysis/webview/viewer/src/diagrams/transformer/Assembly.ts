import type { SEdge } from "sprotty-protocol"
import { type BaseNode, buildBaseNode } from "../diagramElements/nodes/schemes/BaseNode"
import type { AssemblyPort } from "../diagramElements/ports/Port"
import { getOfType, type ID, type JsonBase, MapTransformer } from "./base"
import { EDGES } from "../diagramElements/edges"
import { PORTS } from "../diagramElements/ports"
import { NODES } from "../diagramElements/nodes"
import { TypeRegistry } from "@/model/TypeRegistry"
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions"
import { NameRegistry } from "@/model/NameRegistry"

namespace Json {
  export interface System extends JsonBase {
      name: string
      type: 'System'
      contents: (AssemblyContext | AssemblyConnector | ProvidedDelegationConnector | OperationProvided)[]
  }
  export interface AssemblyContext extends JsonBase {
      name: string
      type: 'AssemblyContext'
  }
  export interface AssemblyConnector extends JsonBase {
      providingRole: string
      requiredRole: string
      providingAssebly: ID
      requiredAssembly: ID
      type: 'AssemblyConnector'
  }
  export interface ProvidedDelegationConnector extends JsonBase {
      assemblyContext: ID
      outerProvidedRole: ID
      type: 'ProvidedDelegationConnector'
  }
  export interface OperationProvided extends JsonBase {
      name: string
      providingRole: string
      type: 'OperationProvided'
  }
}

export type AssemblyFileContent = Json.System[]

export class AssemblyTransformer extends MapTransformer<Json.System> {

  async transformSingle(system: Json.System): Promise<BaseNode> {
    const typeRegistry = TypeRegistry.getInstance()
        const nameRegistry = NameRegistry.getInstance()
    const assemblyContexts: Record<ID, BaseNode> = {}
        const edges: SEdge[] = []
        const outsidePorts: AssemblyPort[] = []
        for (const child of getOfType<Json.AssemblyContext>(system.contents, 'AssemblyContext')) {
            typeRegistry.registerComponent(child.id, ArchitecturalElementTypeOptionList.COMPONENT)
            nameRegistry.addName(child.id, child.name)
            assemblyContexts[child.id] = buildBaseNode(
                child.id,
                NODES.ASSEMBLY_CONTEXT,
                child.name,
                'AssemblyContext',
                []
            )
        }
        for (const port of getOfType<Json.OperationProvided>(system.contents, 'OperationProvided')) {
            outsidePorts.push({
                type: PORTS.PROVIDING,
                id: port.id,
                name: port.providingRole
            })
        }
        for (const edge of getOfType<Json.AssemblyConnector>(system.contents, 'AssemblyConnector')) {
            const providingPort: AssemblyPort = {
                type: PORTS.PROVIDING,
                id: edge.providingAssebly + edge.id,
                name: edge.providingRole
            }
            const requieringPort: AssemblyPort = {
                type: PORTS.REQUIRING,
                id: edge.requiredAssembly + edge.id,
                name: edge.requiredRole
            }
            assemblyContexts[edge.providingAssebly].children!.push(providingPort)
            assemblyContexts[edge.requiredAssembly].children!.push(requieringPort)

            typeRegistry.registerComponent(edge.id, ArchitecturalElementTypeOptionList.CONNECTOR)
            nameRegistry.addName(edge.id, edge.requiredRole + ' -> ' + edge.providingRole)
            edges.push({
                type: EDGES.ARROW_OPEN,
                id: edge.id,
                sourceId: requieringPort.id,
                targetId: providingPort.id
            })
        }
        for (const edge of getOfType<Json.ProvidedDelegationConnector>(system.contents, 'ProvidedDelegationConnector')) {
            typeRegistry.registerComponent(edge.id + edge.assemblyContext, ArchitecturalElementTypeOptionList.CONNECTOR)
            nameRegistry.addName(edge.id + edge.assemblyContext, 'PlaceHolderName')
            const providingPort: AssemblyPort = {
                type: PORTS.PROVIDING,
                id: edge.id + edge.assemblyContext,
                name: 'PlaceHolderName'
            }
            assemblyContexts[edge.assemblyContext].children!.push(providingPort)
            edges.push({
                type: EDGES.ARROW_OPEN,
                id: edge.id,
                targetId: edge.id + edge.assemblyContext,
                sourceId: edge.outerProvidedRole
            })
        }
        return buildBaseNode(
            system.id,
            NODES.SYSTEM,
            'System',
            'System',
            [...Object.values(assemblyContexts), ...outsidePorts, ...edges]
        )
  }
}