import { SModelElement } from "sprotty-protocol";

export type ID = string

export interface JsonBase {
  id: ID,
  type: string
}

export abstract class AbstractTransformer<S extends JsonBase> {
  abstract transform(o: S[]): SModelElement[]
}

export abstract class MapTransformer<S extends JsonBase> extends AbstractTransformer<S> {
  transform(o: S[]): SModelElement[] {
      return o.map(this.transformSingle)
  }

  abstract transformSingle(o: S): SModelElement
}

export abstract class FlatMapTransformer<S extends JsonBase> extends AbstractTransformer<S> {
  transform(o: S[]): SModelElement[] {
      return o.flatMap(this.transformSingle)
  }

  protected abstract transformSingle(o: S): SModelElement[]
}

export function getOfType<T extends { type: string }>(arr: (T | { type: string })[], type: string): T[] {
  return arr.filter((a) => a.type == type) as T[]
}
