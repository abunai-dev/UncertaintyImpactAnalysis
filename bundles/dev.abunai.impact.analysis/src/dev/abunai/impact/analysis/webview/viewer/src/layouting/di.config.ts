import { ContainerModule } from 'inversify'
import { TYPES, configureCommand } from 'sprotty'
import { ElkFactory, ElkLayoutEngine, ILayoutConfigurator } from 'sprotty-elk'
import { DemoLayoutConfigurator, MyLayoutEngine } from './layouter'
import ElkConstructor from 'elkjs/lib/elk.bundled'

const elkFactory: ElkFactory = () => new ElkConstructor({ algorithms: ['layered'] })

export const autoLayoutModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    bind(ElkFactory).toConstantValue(elkFactory)
    rebind(ILayoutConfigurator).to(DemoLayoutConfigurator)
    bind(TYPES.IModelLayoutEngine)
        .toDynamicValue(
            (ctx) =>
                new MyLayoutEngine(ctx.container.get(ElkFactory), undefined, ctx.container.get(ILayoutConfigurator))
        )
        .inSingletonScope()
})
