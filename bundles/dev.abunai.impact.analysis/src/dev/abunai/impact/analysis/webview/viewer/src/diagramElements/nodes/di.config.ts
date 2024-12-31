import { ContainerModule } from "inversify"
import { configureModelElement } from "sprotty"
import { BaseNodeImpl } from "./BaseNode"
import { LinkingResourceView } from "./LinkingResource"
import { AssemblyContextView } from "./AssemblyContext"
import { ResourceContainerView } from "./ResourceContainer"
import { SystemView } from "./System"
import { NODES } from "."

export const nodeModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, NODES.LINKING_RESOURCE, BaseNodeImpl, LinkingResourceView)
    configureModelElement(context, NODES.ASSEMBLY_CONTEXT, BaseNodeImpl, AssemblyContextView)
    configureModelElement(context, NODES.RESOURCE_CONTAINER, BaseNodeImpl, ResourceContainerView)
    configureModelElement(context, NODES.SYSTEM, BaseNodeImpl, SystemView)
})
