import ElkConstructor, { ElkExtendedEdge, LayoutOptions } from 'elkjs'
import { injectable } from 'inversify'
import { DefaultLayoutConfigurator, ElkFactory, ElkLayoutEngine } from 'sprotty-elk'
import { SEdge, SGraph, SModelIndex } from 'sprotty-protocol'

@injectable()
export class CustomLayoutConfigurator extends DefaultLayoutConfigurator {
    // Configure graph-level options
    protected override graphOptions(sgraph: SGraph, index: SModelIndex): LayoutOptions {
        return {
            'org.eclipse.elk.edgeRouting': 'POLYLINE',
            'org.eclipse.elk.hierarchyHandling': 'INCLUDE_CHILDREN',
            'org.eclipse.elk.direction': 'UP',
            'org.eclipse.elk.layered.considerModelOrder.strategy': 'PREFER_NODES',
            "org.eclipse.elk.algorithm": "org.eclipse.elk.layered",
            "org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers": "30.0",
            "org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers": "20.0",
            "org.eclipse.elk.port.borderOffset": "14.0",
            // Do not do micro layout for nodes, which includes the node dimensions etc.
            // These are all automatically determined by our dfd node views
            "org.eclipse.elk.omitNodeMicroLayout": "true",
            // Balanced graph > straight edges
            "org.eclipse.elk.layered.nodePlacement.favorStraightEdges": "true",
        }
    }
}

export const elkFactory: ElkFactory = () => new ElkConstructor({ algorithms: ['layered'] })

export class StraightEdgeLayoutEngine extends ElkLayoutEngine {
    protected override applyEdge(sedge: SEdge, elkEdge: ElkExtendedEdge, index: SModelIndex): void {
        sedge.routingPoints = []
    }
}
