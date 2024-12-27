import { ElkExtendedEdge, ElkNode, ElkPrimitiveEdge, ElkShape, LayoutOptions } from 'elkjs'
import { injectable } from 'inversify'
import { DefaultLayoutConfigurator, ElkLayoutEngine } from 'sprotty-elk'
import { SEdge, SGraph, SLabel, SModelIndex, SNode, SPort, SShapeElement } from 'sprotty-protocol'

@injectable()
export class DemoLayoutConfigurator extends DefaultLayoutConfigurator {
    // Configure graph-level options
    protected override graphOptions(sgraph: SGraph, index: SModelIndex): LayoutOptions {
        return {
            'org.eclipse.elk.algorithm': 'org.eclipse.elk.layered',
            'org.eclipse.elk.edgeRouting': 'POLYLINE',
            'org.eclipse.elk.hierarchyHandling': 'INCLUDE_CHILDREN',
            'org.eclipse.elk.direction': 'UP',
            'org.eclipse.elk.layered.considerModelOrder.strategy': 'PREFER_NODES'
        }
    }

    protected override nodeOptions(snode: SNode, index: SModelIndex): LayoutOptions {
        return {
            'org.eclipse.elk.nodeSize.constraints': 'MINIMUM_SIZE',
            'org.eclipse.elk.nodeLabels.placement': 'INSIDE V_TOP H_CENTER',
            'org.eclipse.elk.nodeLabels.padding': '[top=10, bottom=0, left=20, right=20]',
            'org.eclipse.elk.portAlignment.default': 'CENTER',
            'org.eclipse.elk.spacing.portsSurrounding': '[top=20, bottom=0, left=0, right=0]'
        }
    }
}

export class MyLayoutEngine extends ElkLayoutEngine {
    protected override applyEdge(sedge: SEdge, elkEdge: ElkExtendedEdge, index: SModelIndex): void {
        sedge.routingPoints = []
    }
}
