import { ContainerModule } from 'inversify'
import { LightDarkSwitch } from './lightDarkSwitch/LightDarkSwitch'
import { configureModelElement, configureViewerOptions, LocalModelSource, SGraphImpl, SGraphView, TYPES } from 'sprotty'
import { EDITOR_TYPES } from '../EditorTypes'

export const commonModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    bind(LightDarkSwitch).toSelf().inSingletonScope()
    bind(TYPES.IUIExtension).toService(LightDarkSwitch)
    bind(EDITOR_TYPES.DefaultUIElement).toService(LightDarkSwitch)

    bind(TYPES.ModelSource).to(LocalModelSource).inSingletonScope();

    const context = {bind, unbind, rebind, isBound}
    configureModelElement(context, 'graph', SGraphImpl, SGraphView)
    configureViewerOptions(context, {
        needsClientLayout: true
    })
})
