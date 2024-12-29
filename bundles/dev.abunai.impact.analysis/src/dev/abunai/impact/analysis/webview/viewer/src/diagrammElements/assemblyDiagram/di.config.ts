import { ContainerModule } from 'inversify'
import { configureModelElement, TYPES } from 'sprotty'
import { AssemblyContextNode, AssemblyContextNodeView } from './AssemblyContextNode'
import { ProvidingAssemblyPort, ProvidingAssemblyPortView } from './ProvidingPort'
import { RequiringAssemblyPort, RequiringAssemblyPortView } from './RequiringPort'
import { SystemContainer, SystemContainerView } from './SystemContainer'
import { AlwaysSnapPortsMoveMouseListener, PortAwareSnapper, portSnapper } from './PortSnapper'
import { AssemblyPort } from './Port'

export const assemblyDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, 'node:assembly:assemblyContext', AssemblyContextNode, AssemblyContextNodeView)
    configureModelElement(context, 'port:assembly:providing', ProvidingAssemblyPort, ProvidingAssemblyPortView)
    configureModelElement(context, 'port:assembly:requiring', RequiringAssemblyPort, RequiringAssemblyPortView)
    configureModelElement(context, 'node:assembly:system', SystemContainer, SystemContainerView)

    bind(TYPES.ISnapper).toConstantValue(portSnapper)
    bind(TYPES.MouseListener).to(AlwaysSnapPortsMoveMouseListener).inSingletonScope();
})
