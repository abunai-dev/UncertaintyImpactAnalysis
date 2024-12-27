import { inject, injectable } from 'inversify'
import {
    CommandExecutionContext,
    CommandReturn,
    DeleteElementCommand,
    EditLabelMouseListener,
    SModelElementImpl
} from 'sprotty'
import { Action } from 'sprotty-protocol'

@injectable()
export class EditLabelMouseListenerRemove extends EditLabelMouseListener {
    doubleClick(target: SModelElementImpl, event: MouseEvent): (Action | Promise<Action>)[] {
        return []
    }
}

@injectable()
export class DeleteElementCommandRemove extends DeleteElementCommand {
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
