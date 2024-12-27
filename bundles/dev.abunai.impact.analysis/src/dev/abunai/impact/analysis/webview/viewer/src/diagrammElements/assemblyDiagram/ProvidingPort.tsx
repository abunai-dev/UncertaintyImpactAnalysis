/** @jsx svg */
import { injectable } from 'inversify'
import { IViewArgs, ShapeView, SPortImpl, RenderingContext, svg, moveFeature } from 'sprotty'
import { Bounds, SPort } from 'sprotty-protocol'
import { VNode } from 'snabbdom'

export class ProvidingAssemblyPort extends SPortImpl {
    static DEFAULT_FEATURES: symbol[] = [moveFeature, ...SPortImpl.DEFAULT_FEATURES]
    public name: string

    override get bounds(): Bounds {
        return {
            x: this.position.x,
            y: this.position.y - 28,
            width: 16,
            height: 16
        }
    }
}

@injectable()
export class ProvidingAssemblyPortView extends ShapeView {
    render(model: Readonly<ProvidingAssemblyPort>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
            return undefined
        }
        return (
            <g class-sprotty-port={true}>
                <text y="-32">{model.name}</text>
                <line x1="8" y1="0" x2="8" y2="-12"></line>
                <circle class-no-fill={true} cx="8" cy="-20" r="8"></circle>
            </g>
        )
    }
}
