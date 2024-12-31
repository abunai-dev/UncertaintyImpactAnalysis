import { ContainerModule } from "inversify"
import { configureModelElement, SNodeImpl } from "sprotty"
import { BaseNodeImpl } from "./BaseNode"
import { LinkingResourceView } from "./LinkingResource"
import { AssemblyContextView } from "./AssemblyContext"
import { ResourceContainerView } from "./ResourceContainer"
import { SystemView } from "./System"
import { NODES } from "."
import { StartNodeView, StopNodeView } from "./CircularNodes"
import { ScenarioBehaviourView } from "./ScenarioBehaviour"
import { UsageScenarioView } from "./UsageScenario"

export const nodeModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, NODES.LINKING_RESOURCE, BaseNodeImpl, LinkingResourceView)
    configureModelElement(context, NODES.ASSEMBLY_CONTEXT, BaseNodeImpl, AssemblyContextView)
    configureModelElement(context, NODES.RESOURCE_CONTAINER, BaseNodeImpl, ResourceContainerView)
    configureModelElement(context, NODES.SYSTEM, BaseNodeImpl, SystemView)
    configureModelElement(context, NODES.START, SNodeImpl, StartNodeView)
    configureModelElement(context, NODES.STOP, SNodeImpl, StopNodeView)
    configureModelElement(context, NODES.SCENARIO_BEHAVIOUR, BaseNodeImpl, ScenarioBehaviourView)
    configureModelElement(context, NODES.USAGE_SCENARIO, BaseNodeImpl, UsageScenarioView)
})
