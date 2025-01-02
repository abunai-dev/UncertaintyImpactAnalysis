import { ContainerModule } from "inversify"
import { configureModelElement, TYPES } from "sprotty"
import { ProvidingAssemblyPortImpl, ProvidingAssemblyPortView } from "./ProvidingPort"
import { RequiringAssemblyPortImpl, RequiringAssemblyPortView } from "./RequiringPort"
import { PORTS } from "."
import { AlwaysSnapPortsMoveMouseListener, portSnapper } from "./PortSnapper"

export const portModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, PORTS.PROVIDING, ProvidingAssemblyPortImpl, ProvidingAssemblyPortView)
    configureModelElement(context, PORTS.REQUIRING, RequiringAssemblyPortImpl, RequiringAssemblyPortView)

    bind(TYPES.ISnapper).toConstantValue(portSnapper)
    bind(TYPES.MouseListener).to(AlwaysSnapPortsMoveMouseListener).inSingletonScope();
})