/** @jsx svg */
import { injectable } from 'inversify'
import { IViewArgs, ShapeView, SPortImpl, RenderingContext, svg, moveFeature } from 'sprotty'
import { Bounds, SPort } from 'sprotty-protocol'
import { VNode } from 'snabbdom'

export class RequiringAssemblyPort extends SPortImpl {
    static DEFAULT_FEATURES: symbol[] = [moveFeature, ...SPortImpl.DEFAULT_FEATURES]
    public name: string

    override get bounds(): Bounds {
        return {
            x: this.position.x,
            y: this.position.y - 20,
            width: 4,
            height: 4
        }
    }
}

@injectable()
export class RequiringAssemblyPortView extends ShapeView {
    render(model: Readonly<RequiringAssemblyPort>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
            return undefined
        }
        return (
            <g class-sprotty-port={true}>
                <text y="-22">{model.name}</text>
                <line x1="2" y1="0" x2="2" y2="-12"></line>
                <path d="M 10 -20 A 8 8 0 0 1 -6 -20"></path>
            </g>
        )
    }
}
