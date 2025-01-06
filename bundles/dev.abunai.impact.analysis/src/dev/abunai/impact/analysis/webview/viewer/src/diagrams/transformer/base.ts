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
    const children = await Promise.all(o.map(this.transformSingle))
    return await layouter.layout({
      id: 'root',
      type: 'graph',
      children
    })
  }

  abstract transformSingle(o: S): Promise<SModelElement>
}

export abstract class FlatMapTransformer<S extends JsonBase> extends AbstractTransformer<S> {
  async transform(o: S[]): Promise<SGraph> {
    const all = await Promise.all(o.map(this.transformSingle))
    const children = all.flat()
    return await layouter.layout({
      id: 'root',
      type: 'graph',
      children
    })
  }

  protected abstract transformSingle(o: S): Promise<SModelElement[]>
}

export function getOfType<T extends { type: string }>(arr: (T | { type: string })[], type: string): T[] {
  return arr.filter((a) => a.type == type) as T[]
}
