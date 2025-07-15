export class NameRegistry {
    private static INSTANCE: NameRegistry
    private names: Map<string, string> = new Map()
    
    private constructor() {}
    
    public static getInstance(): NameRegistry {
        if (!NameRegistry.INSTANCE) {
        NameRegistry.INSTANCE = new NameRegistry()
        }
        return NameRegistry.INSTANCE
    }
    
    public addName(id:string, name: string): void {
        this.names.set(id, name)
    }
    
    public getName(id: string): string|undefined {
        return this.names.get(id)
    }
}