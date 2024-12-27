import { ContainerModule } from 'inversify'
import { configureModelElement } from 'sprotty'
import { AssemblyContextNode, AssemblyContextNodeView } from './AssemblyContextNode'
import { ProvidingAssemblyPort, ProvidingAssemblyPortView } from './ProvidingPort'
import { RequiringAssemblyPort, RequiringAssemblyPortView } from './RequiringPort'
import { SystemContainer, SystemContainerView } from './SystemContainer'

export const assemblyDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, 'node:assembly:assemblyContext', AssemblyContextNode, AssemblyContextNodeView)
    configureModelElement(context, 'port:assembly:providing', ProvidingAssemblyPort, ProvidingAssemblyPortView)
    configureModelElement(context, 'port:assembly:requiring', RequiringAssemblyPort, RequiringAssemblyPortView)
    configureModelElement(context, 'node:assembly:system', SystemContainer, SystemContainerView)
})
