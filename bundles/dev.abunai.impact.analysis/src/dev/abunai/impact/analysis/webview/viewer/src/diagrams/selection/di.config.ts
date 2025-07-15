import { ContainerModule } from 'inversify'
import {
    configureCommand,
    TYPES
} from 'sprotty'
import { CustomSelectCommand, RedrawCommand } from './Commands'

export const selectionModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    configureCommand({bind, isBound}, RedrawCommand )
    configureCommand({bind, isBound}, CustomSelectCommand)
})