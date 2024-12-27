import { SEdge } from 'sprotty-protocol'
import { AssemblyContextScheme } from '../diagrammElements/assemblyDiagram/AssemblyContextNode'
import _jsonContent from './system.json'
import { AssemblyPort } from '../diagrammElements/assemblyDiagram/Port'
import { SystemContainerScheme } from '../diagrammElements/assemblyDiagram/SystemContainer'

type ID = string

namespace Json {
    export type FileContent = System[]

    export interface System {
        id: ID
        name: string
        type: 'System'
        contents: (AssemblyContext | AssemblyConnector | ProvidedDelegationConnector | OperationProvided)[]
    }
    export interface AssemblyContext {
        name: string
        type: 'AssemblyContext'
        id: ID
    }
    export interface AssemblyConnector {
        providingRole: string
        requiredRole: string
        providingAssebly: ID
        requiredAssembly: ID
        type: 'AssemblyConnector'
        id: ID
    }
    export interface ProvidedDelegationConnector {
        assemblyContext: ID
        outerProvidedRole: ID
        type: 'ProvidedDelegationConnector'
        id: ID
    }
    export interface OperationProvided {
        name: string
        providingRole: string
        type: 'OperationProvided'
        id: ID
    }
}

export function transform() {
    const jsonContent = _jsonContent as Json.FileContent
    //return transfromSystem(jsonContent[0]).children
    return jsonContent.map(transfromSystem)
}

function transfromSystem(system: Json.System): SystemContainerScheme {
    const assemblyContexts: Record<ID, AssemblyContextScheme> = {}
    const edges: SEdge[] = []
    const outsidePorts: AssemblyPort[] = []
    for (const child of getOfType<Json.AssemblyContext>(system.contents, 'AssemblyContext')) {
        assemblyContexts[child.id] = {
            id: child.id,
            name: child.name,
            typeName: 'AssemblyContext',
            type: 'node:assembly:assemblyContext',
            children: []
        }
    }
    for (const port of getOfType<Json.OperationProvided>(system.contents, 'OperationProvided')) {
        outsidePorts.push({
            type: 'port:assembly:providing',
            id: port.id,
            name: port.providingRole
        })
    }
    for (const edge of getOfType<Json.AssemblyConnector>(system.contents, 'AssemblyConnector')) {
        const providingPort: AssemblyPort = {
            type: 'port:assembly:providing',
            id: edge.providingAssebly + edge.id,
            name: edge.providingRole
        }
        const requieringPort: AssemblyPort = {
            type: 'port:assembly:requiring',
            id: edge.requiredAssembly + edge.id,
            name: edge.requiredRole
        }
        assemblyContexts[edge.providingAssebly].children.push(providingPort)
        assemblyContexts[edge.requiredAssembly].children.push(requieringPort)
        edges.push({
            type: 'edge:open',
            id: edge.id,
            sourceId: requieringPort.id,
            targetId: providingPort.id
        })
    }
    for (const edge of getOfType<Json.ProvidedDelegationConnector>(system.contents, 'ProvidedDelegationConnector')) {
        const providingPort: AssemblyPort = {
            type: 'port:assembly:providing',
            id: edge.id + edge.assemblyContext,
            name: 'PlaceHolderName'
        }
        assemblyContexts[edge.assemblyContext].children.push(providingPort)
        edges.push({
            type: 'edge:open',
            id: edge.id,
            targetId: edge.id + edge.assemblyContext,
            sourceId: edge.outerProvidedRole
        })
    }
    return {
        id: system.id,
        type: 'node:assembly:system',
        name: 'System',
        typeName: 'System',
        children: [...Object.values(assemblyContexts), ...outsidePorts, ...edges]
    }
}

function getOfType<T extends { type: string }>(arr: (T | { type: string })[], type: string): T[] {
    return arr.filter((a) => a.type == type) as T[]
}
