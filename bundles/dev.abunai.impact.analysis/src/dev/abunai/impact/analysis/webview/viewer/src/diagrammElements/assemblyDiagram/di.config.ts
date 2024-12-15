import { ContainerModule } from "inversify";
import { configureModelElement } from "sprotty";
import { AssemblyContextNode, AssemblyContextNodeView } from "./AssemblyContextNode";
import { ProvidingAssemblyPort, ProvidingAssemblyPortView } from "./ProvidingPort";

export const assemblyDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
  const context = {bind, unbind, isBound, rebind}
  configureModelElement(context, 'node:assembly:assemblyContext', AssemblyContextNode, AssemblyContextNodeView)
  configureModelElement(context, 'port:assembly:providing', ProvidingAssemblyPort, ProvidingAssemblyPortView)
})