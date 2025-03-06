import { ContainerModule } from "inversify"
import { configureModelElement } from "sprotty"
import { BaseNodeImpl } from "./BaseNode"
import { LinkingResourceView } from "./LinkingResource"
import { AssemblyContextView } from "./AssemblyContext"
import { ResourceContainerView } from "./ResourceContainer"
import { SystemView } from "./System"
import { NODES } from "."
import { CircularNodeImpl, StartNodeView, StopNodeView } from "./CircularNodes"
import { ScenarioBehaviourView } from "./ScenarioBehaviour"
import { UsageScenarioView } from "./UsageScenario"
import { SignnatureNodeImpl } from "./SignatureNode"
import { InterfaceView } from "./Interface"
import { CompositeDataTypeView } from "./CompositeDataType"
import { BasicComponentImpl, BasicComponentView, SeffSignatureImpl, SeffSignatureView } from "./BasicComponent"
import { EntryLevelSystemCallImpl, EntryLevelSystemCallView } from "./EntryLevelSystemCall"
import { UnconcreteActionView } from "./UnconcreteAction"
import { SetVariableImpl, SetVariableView } from "./SetVariable"
import { BranchTransition2Impl, BranchTransition2View, BranchTransitionImpl, BranchView, GuardedBranchTransitionView, ProbabilisticBranchTransitionView } from "./Branch"

export const nodeModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, NODES.LINKING_RESOURCE, BaseNodeImpl, LinkingResourceView)
    configureModelElement(context, NODES.ASSEMBLY_CONTEXT, BaseNodeImpl, AssemblyContextView)
    configureModelElement(context, NODES.RESOURCE_CONTAINER, BaseNodeImpl, ResourceContainerView)
    configureModelElement(context, NODES.SYSTEM, BaseNodeImpl, SystemView)
    configureModelElement(context, NODES.START, CircularNodeImpl, StartNodeView)
    configureModelElement(context, NODES.STOP, CircularNodeImpl, StopNodeView)
    configureModelElement(context, NODES.SCENARIO_BEHAVIOUR, BaseNodeImpl, ScenarioBehaviourView)
    configureModelElement(context, NODES.USAGE_SCENARIO, BaseNodeImpl, UsageScenarioView)
    configureModelElement(context, NODES.INTERFACE, SignnatureNodeImpl, InterfaceView)
    configureModelElement(context, NODES.COMPOSITE_DATA_TYPE, SignnatureNodeImpl, CompositeDataTypeView)
    configureModelElement(context, NODES.BASIC_COMPONENT, BasicComponentImpl, BasicComponentView)
    configureModelElement(context, NODES.ENTRY_LEVEL_SYSTEM_CALL, EntryLevelSystemCallImpl, EntryLevelSystemCallView)
    configureModelElement(context, NODES.UNCONCRETE_ACTION, BaseNodeImpl, UnconcreteActionView)
    configureModelElement(context, NODES.SEFF_SIGNATURE_LABEL, SeffSignatureImpl, SeffSignatureView)
    configureModelElement(context, NODES.SET_VARIABLE, SetVariableImpl, SetVariableView)
    configureModelElement(context, NODES.BRANCH, BaseNodeImpl, BranchView)
    configureModelElement(context, NODES.PROBABILISTIC_BRANCH_TRANSITION, BranchTransitionImpl, ProbabilisticBranchTransitionView)
    configureModelElement(context, NODES.GUARDED_BRANCH_TRANSITION, BranchTransitionImpl, GuardedBranchTransitionView)
    configureModelElement(context, NODES.BRANCH_TRANSTION, BranchTransition2Impl, BranchTransition2View)
})
