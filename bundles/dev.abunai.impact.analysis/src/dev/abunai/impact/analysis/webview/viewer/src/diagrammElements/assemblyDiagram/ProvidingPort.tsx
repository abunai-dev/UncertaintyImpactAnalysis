/** @jsx svg */
import { injectable } from 'inversify'
import { IViewArgs, ShapeView, svg, RenderingContext } from 'sprotty'
import { VNode } from 'snabbdom'
import { AssemblyPort } from './Port'
import { Bounds } from 'sprotty-protocol'


export class ProvidingAssemblyPort extends AssemblyPort {
    override get bounds(): Bounds {
            const base = {
                x: this.position.x,
                y: this.position.y,
                width: 16,
                height: 16
            }
            const sideFactor = this.sideFactor
            if (sideFactor.x == 0 && sideFactor.y == 1) {
                base.y -= 28
            }
            if (sideFactor.x == 0 && sideFactor.y == -1) {
                base.y += 12
                base.x -= 16
            }
            if (sideFactor.x == 1 && sideFactor.y == 0) {
                base.x += 12
            }
            if (sideFactor.x == -1 && sideFactor.y == 0) {
                base.x -= 28
                base.y -= 16
            }
    
            return base
        }
}

@injectable()
export class ProvidingAssemblyPortView extends ShapeView {

        render(model: Readonly<AssemblyPort>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
            if (!this.isVisible(model, context)) {
                return undefined
            }
            const circleX = model.sideFactor.y * 8 + model.sideFactor.x * 20;
            const circleY = model.sideFactor.x * 8 + model.sideFactor.y * -20;
            const lineX = model.sideFactor.y * 8 + model.sideFactor.x * 12;
            const lineY = model.sideFactor.x * 8 + model.sideFactor.y * -12;
    
            const textX = model.sideFactor.x == 0 ? 0 : 5;
            const textY = model.sideFactor.y == 0 ? -4 : model.sideFactor.y * -32;
            return (
                <g class-sprotty-port={true}>
                    <text x={textX} y={textY}>{model.name}</text>
                    <line x1={model.sideFactor.x == 0 ? 8*model.sideFactor.y : 0} 
                    y1={model.sideFactor.y == 0 ? 8*model.sideFactor.x : 0} x2={lineX} y2={lineY}></line>
                    <circle class-no-fill={true} cx={circleX} cy={circleY} r="8"></circle>
                    
                </g>
            )
        }
}
