import type { SGraph, SModelElement } from "sprotty-protocol";
import { layouter } from "../layouting/layouter";

export type ID = string

export interface JsonBase {
  id: ID,
  type: string
}

export abstract class AbstractTransformer<S extends JsonBase> {
  abstract transform(o: S[]): Promise<SGraph>
}

export abstract class MapTransformer<S extends JsonBase> extends AbstractTransformer<S> {
  async transform(o: S[]): Promise<SGraph> {
    return await layouter.layout({
      id: 'root',
      type: 'graph',
      children: o.map(this.transformSingle)
    })
  }

  abstract transformSingle(o: S): SModelElement
}

export abstract class FlatMapTransformer<S extends JsonBase> extends AbstractTransformer<S> {
  async transform(o: S[]): Promise<SGraph> {
    return await layouter.layout({
      id: 'root',
      type: 'graph',
      children: o.flatMap(this.transformSingle)
    })
  }

  protected abstract transformSingle(o: S): SModelElement[]
}

export function getOfType<T extends { type: string }>(arr: (T | { type: string })[], type: string): T[] {
  return arr.filter((a) => a.type == type) as T[]
}
