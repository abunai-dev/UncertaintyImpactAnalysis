import { ContainerModule } from 'inversify'
import { TYPES } from 'sprotty'
import { ElkFactory, ILayoutConfigurator } from 'sprotty-elk'
import { CustomLayoutConfigurator, elkFactory, StraightEdgeLayoutEngine } from './layouter'

export const autoLayoutModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    /*bind(ElkFactory).toConstantValue(elkFactory)
    rebind(ILayoutConfigurator).to(CustomLayoutConfigurator)
    bind(TYPES.IModelLayoutEngine)
        .toDynamicValue(
            (ctx) =>
                new StraightEdgeLayoutEngine(ctx.container.get(ElkFactory), undefined, ctx.container.get(ILayoutConfigurator))
        )
        .inSingletonScope()*/
})