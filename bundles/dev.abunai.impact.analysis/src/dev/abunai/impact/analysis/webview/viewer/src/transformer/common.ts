export function getOfType<T extends { type: string }>(arr: (T | { type: string })[], type: string): T[] {
  return arr.filter((a) => a.type == type) as T[]
}
