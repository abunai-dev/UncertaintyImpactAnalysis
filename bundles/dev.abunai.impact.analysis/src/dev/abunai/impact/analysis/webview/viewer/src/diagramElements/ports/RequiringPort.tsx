/** @jsx svg */
import { injectable } from 'inversify'
import { IViewArgs, ShapeView, svg, RenderingContext } from 'sprotty'
import { VNode } from 'snabbdom'
import { AssemblyPortImpl } from './Port'
import { Bounds } from 'sprotty-protocol'


export class RequiringAssemblyPortImpl extends AssemblyPortImpl {
    override get bounds(): Bounds {
            const base = {
                x: this.position.x,
                y: this.position.y,
                width: 4,
                height: 4
            }
            const sideFactor = this.sideFactor
            if (sideFactor.x == 0 && sideFactor.y == 1) {
                base.y -= 20
            }
            if (sideFactor.x == 0 && sideFactor.y == -1) {
                base.y += 12
                base.x -= 4
            }
            if (sideFactor.x == 1 && sideFactor.y == 0) {
                base.x += 12
            }
            if (sideFactor.x == -1 && sideFactor.y == 0) {
                base.x -= 20
                base.y -= 4
            }
    
            return base
        }
}

@injectable()
export class RequiringAssemblyPortView extends ShapeView {

    render(model: Readonly<AssemblyPortImpl>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(model, context)) {
            return undefined
        }
        const lineX = model.sideFactor.y * 2 + model.sideFactor.x * 12;
        const lineY = model.sideFactor.x * 2 + model.sideFactor.y * -12;

        const textX = model.sideFactor.x == 0 ? 0 : 5;
        const textY = model.sideFactor.y == 0 ? -4 : model.sideFactor.y * -32;
        return (
            <g class-sprotty-port={true}>
                <text x={textX} y={textY}>{model.name}</text>
                <line x1={model.sideFactor.x == 0 ? 2*model.sideFactor.y : 0} 
                y1={model.sideFactor.y == 0 ? 2*model.sideFactor.x : 0} x2={lineX} y2={lineY}></line>
                <path d={this.getPath(model)}></path>
                
            </g>
        )
    }

    getPath(model: Readonly<AssemblyPortImpl>) {
        const sideFactor = model.sideFactor
        if (sideFactor.x == 0 && sideFactor.y == 1) {
            return `M 10 -20 A 8 8 0 0 1 -6 -20`
        }
        if (sideFactor.x == 0 && sideFactor.y == -1) {
            return `M 6 20 A 8 8 0 0 0 -10 20`
        }
        // M 20 10 A 8 8 0 0 1 20 -6 M -20 6 A 8 8 0 0 0 -20 -10
        if (sideFactor.x == 1 && sideFactor.y == 0) {
            return `M 20 10 A 8 8 0 0 1 20 -6`
        }
        if (sideFactor.x == -1 && sideFactor.y == 0) {
            return `M -20 6 A 8 8 0 0 0 -20 -10`
        }
        
        return `M 10 -20 A 8 8 0 0 1 -6 -20`
    }
}
