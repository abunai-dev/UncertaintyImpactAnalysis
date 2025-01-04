import { actionDispatcher } from "../diagrams/selection/Commands"
import { ArchitecturalElementTypeOptionList } from "./Uncertainty/option/ArchitecturalElementTypeOptions"
import type { ActionDispatcher } from "sprotty"
import { TypeRegistry } from "./TypeRegistry"

export class SelectionManager {
  private static INSTANCE: SelectionManager
  private selectedUncertainty: number | null = null
  private selectUncertaintyListeners: UpdateListener<number|null>[] = []
  private selectedComponent: string | null = null
  private selectComponentListeners: UpdateListener<string|null>[] = []
  public dispatcher: ActionDispatcher|null = null
  private typeRegistry: TypeRegistry

  private constructor() {
    this.typeRegistry = TypeRegistry.getInstance()
  }

  public static getInstance(): SelectionManager {
    if (!SelectionManager.INSTANCE) {
      SelectionManager.INSTANCE = new SelectionManager()
    }
    return SelectionManager.INSTANCE
  }

  public selectUncertainty(uncertainty: number): void {
    if (this.selectedComponent !== null) {
      const uncertaintyType = this.typeRegistry.getUncertainty(uncertainty)
      const componentType = this.typeRegistry.getComponent(this.selectedComponent)
      if (uncertaintyType !== componentType) {
        return
      }
    }
    this.selectedUncertainty = this.getNewSelectionValue(this.selectedUncertainty, uncertainty)
    this.selectUncertaintyListeners.forEach(listener => listener(this.selectedUncertainty));
    this.triggerRerender();
    this.submit()
  }

  public getSelectedUncertaintyId(): number | null {
    return this.selectedUncertainty
  }

  public addSelectUncertaintyListener(listener: UpdateListener<number|null>): void {
    this.selectUncertaintyListeners.push(listener)
  }

  public selectComponent(component: string|null): void {
    if (component === null) {
      this.selectedComponent = null
      this.selectComponentListeners.forEach(listener => listener(this.selectedComponent));
      return
    }
    if (this.typeRegistry.getComponent(component) === undefined) {
      return
    }
    if (this.selectedUncertainty !== null) {
      const uncertaintyType = this.typeRegistry.getUncertainty(this.selectedUncertainty)
      const componentType = this.typeRegistry.getComponent(component)
      if (uncertaintyType !== componentType) {
        return
      }
    }
    this.selectedComponent = this.getNewSelectionValue(this.selectedComponent, component)
    this.selectComponentListeners.forEach(listener => listener(this.selectedComponent));
    this.triggerRerender();
    this.submit()
  }

  public getSelectedComponentId(): string | null {
    return this.selectedComponent
  }

  public addSelectComponentListener(listener: UpdateListener<string|null>): void {
    this.selectComponentListeners.push(listener)
  }

  private getNewSelectionValue<T>(currentValue: T, newValue: T): T|null {
    if (currentValue === newValue) {
      return null
    }
    return newValue
  }

  private submit() {
    if (this.selectedUncertainty && this.selectedComponent) {
      alert(`Selected uncertainty: ${this.selectedUncertainty}, selected component: ${this.selectedComponent}`)
      this.selectedUncertainty = null
      this.selectedComponent = null
      this.selectUncertaintyListeners.forEach(listener => listener(this.selectedUncertainty));
      this.selectComponentListeners.forEach(listener => listener(this.selectedComponent));
      this.triggerRerender();
    }
  }

  private triggerRerender() {
    if (this.dispatcher) {
      this.dispatcher.dispatch({kind: 'redraw'})
    }
  }
}

type UpdateListener<T> = (newValue: T) => void
