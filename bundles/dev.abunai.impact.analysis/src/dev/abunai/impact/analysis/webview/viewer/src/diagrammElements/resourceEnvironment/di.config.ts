import { ContainerModule } from "inversify"
import { configureModelElement } from "sprotty"
import { LinkingResourceNode, LinkingResourceView } from "./LinkingResource"

export const resourceEnvironmentModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, 'node:resource_environment:linking_resource', LinkingResourceNode, LinkingResourceView)
})
