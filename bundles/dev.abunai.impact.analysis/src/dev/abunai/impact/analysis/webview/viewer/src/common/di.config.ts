import { ContainerModule } from 'inversify'
import { LightDarkSwitch } from './lightDarkSwitch/LightDarkSwitch'
import { TYPES } from 'sprotty'
import { EDITOR_TYPES } from '../EditorTypes'

export const commonModule = new ContainerModule((bind) => {
    bind(LightDarkSwitch).toSelf().inSingletonScope()
    bind(TYPES.IUIExtension).toService(LightDarkSwitch)
    bind(EDITOR_TYPES.DefaultUIElement).toService(LightDarkSwitch)
})
