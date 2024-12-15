/** @jsx svg */
import { PolylineEdgeView, SEdgeImpl, svg, RenderingContext } from "sprotty";
import { Point } from 'sprotty-protocol/lib/utils/geometry';

export class OpenArrowEdgeView extends PolylineEdgeView {
  override renderAdditionals(edge: SEdgeImpl, segments: Point[], context: RenderingContext) {
    const last = segments.length - 1;
    const p1 = segments[last - 1];
    const p2 = segments[last];
    const angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
    const arrowLength = 10;
    const arrowWidth = 8;
    const arrow = [
      { x: p2.x - arrowLength * Math.cos(angle - Math.PI / 6), y: p2.y - arrowLength * Math.sin(angle - Math.PI / 6) },
      p2,
      { x: p2.x - arrowLength * Math.cos(angle + Math.PI / 6), y: p2.y - arrowLength * Math.sin(angle + Math.PI / 6) }
    ];
    return [svg('polyline', {
      points: arrow.map(p => `${p.x},${p.y}`).join(' ')
    })];
  }
}