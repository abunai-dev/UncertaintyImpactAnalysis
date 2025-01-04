import type { ArchitecturalElementTypeOptionList } from "./Uncertainty/option/ArchitecturalElementTypeOptions";

export class TypeRegistry {
    private static instance: TypeRegistry;
    private components: Map<string, ArchitecturalElementTypeOptionList>;
    private uncertainties: Map<number, ArchitecturalElementTypeOptionList>;

    private constructor() {
        this.components = new Map();
        this.uncertainties = new Map();
    }

    public static getInstance(): TypeRegistry {
        if (!TypeRegistry.instance) {
            TypeRegistry.instance = new TypeRegistry();
        }
        return TypeRegistry.instance;
    }

    public registerComponent(id: string, value: ArchitecturalElementTypeOptionList): void {
        this.components.set(id, value);
    }

    public getComponent(id: string): ArchitecturalElementTypeOptionList | undefined {
        return this.components.get(id);
    }

    public registerUncertainty(id: number, value: ArchitecturalElementTypeOptionList): void {
        this.uncertainties.set(id, value);
    }

    public getUncertainty(id: number): ArchitecturalElementTypeOptionList | undefined {
        return this.uncertainties.get(id);
    }
}