import { ContainerModule } from 'inversify'
import { configureModelElement } from 'sprotty'
import { ResourceContainerNode, ResourceContainerView } from './ResourceContainer'


export const allocationDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind }
    configureModelElement(context, 'node:allocation:resource_container', ResourceContainerNode, ResourceContainerView)
})
