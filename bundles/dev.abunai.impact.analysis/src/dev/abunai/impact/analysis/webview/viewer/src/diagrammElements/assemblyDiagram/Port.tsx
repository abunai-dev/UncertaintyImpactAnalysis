/** @jsx svg */
import { isBoundsAware, moveFeature, SPortImpl } from 'sprotty'
import { Bounds, SPort } from 'sprotty-protocol'

export interface AssemblyPortScheme extends SPort {
    name: string
}

export abstract class AssemblyPort extends SPortImpl {
    static DEFAULT_FEATURES: symbol[] = [moveFeature, ...SPortImpl.DEFAULT_FEATURES]
    public name: string

    abstract get bounds(): Bounds
    
    get sideFactor(): {x: 1|0|-1, y: 1|0|-1} {
        if (!isBoundsAware(this.parent)) {
            return {x: 1, y: 0};
        }
        const parentBounds = this.parent.bounds;
        if (this.position.x == 0) { 
            return {x: -1, y: 0};
        }
        if (this.position.x == parentBounds.width) {
            return {x: 1, y: 0};
        }
        if (this.position.y == 0) {
            return {x: 0, y: 1};
        }
        return {x: 0, y: -1};
    }
}