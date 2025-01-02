import { DeleteElementCommand, EditLabelMouseListener } from 'sprotty'
import { DeleteElementCommandRemove, EditLabelMouseListenerRemove } from './hookOverwrites'
import { ContainerModule } from 'inversify'

export const unbindHookModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    rebind(EditLabelMouseListener).to(EditLabelMouseListenerRemove)
    rebind(DeleteElementCommand).to(DeleteElementCommandRemove)
})
