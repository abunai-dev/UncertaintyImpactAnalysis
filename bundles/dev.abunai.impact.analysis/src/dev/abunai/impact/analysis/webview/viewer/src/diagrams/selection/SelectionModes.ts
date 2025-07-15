import { SelectionManager } from "@/model/SelectionManager";
import { TypeRegistry } from "@/model/TypeRegistry";

export enum SelectionModes {
  NONE, SELECTED, OTHER
}

export function getSelectionMode(id: string): SelectionModes {
  const selectedUncertainty = SelectionManager.getInstance().getSelectedUncertaintyId()
  if (selectedUncertainty !== null) {
    const typeRegistry = TypeRegistry.getInstance() 
    return typeRegistry.getUncertainty(selectedUncertainty) === typeRegistry.getComponent(id) ? SelectionModes.NONE : SelectionModes.OTHER
  }

  const selected = SelectionManager.getInstance().getSelectedComponentId()
  if (selected === null) {
    return SelectionModes.NONE
  }
  if (selected === id) {
    return SelectionModes.SELECTED
  }
  return SelectionModes.OTHER
}