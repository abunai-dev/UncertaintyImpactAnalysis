import { ContainerModule } from "inversify";
import { configureModelElement } from "sprotty";
import { AssemblyContextNode, AssemblyContextNodeView } from "./AssemblyContextNode";

export const assemblyDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
  const context = {bind, unbind, isBound, rebind}
  configureModelElement(context, 'node:assembly:assemblyContext', AssemblyContextNode, AssemblyContextNodeView)
})