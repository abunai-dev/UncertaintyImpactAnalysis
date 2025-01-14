import ElkConstructor, { type ElkExtendedEdge, type ElkNode, type ElkShape, type LayoutOptions } from 'elkjs'
import { injectable } from 'inversify'
import { DefaultLayoutConfigurator, ElkFactory, ElkLayoutEngine, ILayoutPostprocessor } from 'sprotty-elk'
import { type SEdge, type SGraph, type SModelElement, SModelIndex, type SShapeElement } from 'sprotty-protocol'

@injectable() 
class CustomLayoutConfigurator extends DefaultLayoutConfigurator {
    // Configure graph-level options
    protected override graphOptions(sgraph: SGraph, index: SModelIndex): LayoutOptions {
        return {
            'org.eclipse.elk.edgeRouting': 'POLYLINE',
            'org.eclipse.elk.hierarchyHandling': 'INCLUDE_CHILDREN',
            'org.eclipse.elk.direction': 'DOWN',
            'org.eclipse.elk.layered.considerModelOrder.strategy': 'PREFER_NODES',
            "org.eclipse.elk.algorithm": "org.eclipse.elk.layered",
            "org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers": "30.0",
            "org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers": "20.0",
            "org.eclipse.elk.port.borderOffset": "14.0",
            // Do not do micro layout for nodes, which includes the node dimensions etc.
            // These are all automatically determined by our dfd node views
            "org.eclipse.elk.omitNodeMicroLayout": "true",
            // Balanced graph > straight edges
            "org.eclipse.elk.layered.nodePlacement.favorStraightEdges": "true"
        }
    }
}

export { CustomLayoutConfigurator }

export const elkFactory: ElkFactory = () => new ElkConstructor({ algorithms: ['layered'] })

export class StraightEdgeLayoutEngine extends ElkLayoutEngine {
    protected override applyEdge(sedge: SEdge, elkEdge: ElkExtendedEdge, index: SModelIndex): void {
        sedge.routingPoints = []
    }
}

export class MoveDownPostProcessor implements ILayoutPostprocessor {
    postprocess(elkGraph: ElkNode, sgraph: SGraph, index: SModelIndex): void {
        if (!elkGraph.children) return
        for (const child of elkGraph.children) {
            this.move(child)
        }
    }

    private move(node: ElkNode) {
        if (node.y) {
            node.y = node.y + 40
        } 
        if (node.children) {
            for (const child of node.children) {
                this.move(child)
            }
        }
    }

}

export const layouter = new StraightEdgeLayoutEngine(elkFactory, undefined, new CustomLayoutConfigurator(), undefined, new MoveDownPostProcessor())


