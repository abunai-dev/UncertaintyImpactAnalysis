import { SelectionManager } from "@/model/SelectionManager";
import { ArchitecturalElementTypeOptionList } from "@/model/Uncertainty/option/ArchitecturalElementTypeOptions";
import { injectable } from "inversify";
import { ActionDispatcher, Command, SelectCommand, type ActionHandlerRegistration, type CommandExecutionContext, type CommandReturn, type IActionHandler, type ICommand } from "sprotty";
import { SelectAction, type Action } from "sprotty-protocol";

export class MyActionDispatcher extends ActionDispatcher {

  protected async handleAction(action: Action): Promise<void> {
      return super.handleAction(action)
  }
}

export const actionDispatcher = new MyActionDispatcher()

export namespace RedrawAction {
    export const KIND = 'redraw'
    export function create() {
        return { kind: KIND }
    }
}

export interface RedrawAction extends Action {
    type: typeof RedrawAction.KIND
}

export class RedrawCommand extends Command {
    public static readonly KIND = RedrawAction.KIND
    constructor() {
        super()
    }
    blockUntil?: ((action: Action) => boolean) | undefined;
    execute(context: CommandExecutionContext): CommandReturn {
        return context.root
    }
    undo(context: CommandExecutionContext): CommandReturn {
        return context.root
    }
    redo(context: CommandExecutionContext): CommandReturn {
        return context.root
    }
}

export class CustomSelectCommand extends SelectCommand {
    public static readonly KIND = SelectAction.KIND
    constructor(public action: SelectAction) {
        super(action)
    }
    blockUntil?: ((action: Action) => boolean) | undefined;
    execute(context: CommandExecutionContext) {
          if (this.action.selectedElementsIDs.length == 1) {
              SelectionManager.getInstance().selectComponent(this.action.selectedElementsIDs[0])
          }
          if (this.action.selectedElementsIDs.length == 0 && this.action.deselectedElementsIDs.length == 1) {
                SelectionManager.getInstance().selectComponent(null)
            }
        return super.execute(context)
    }

}