import { injectable } from 'inversify'
import {
    type CommandExecutionContext,
    type CommandReturn,
    DeleteElementCommand,
    EditLabelMouseListener,
    SModelElementImpl
} from 'sprotty'
import type { Action } from 'sprotty-protocol'

//@injectable()
class EditLabelMouseListenerRemove extends EditLabelMouseListener {
    doubleClick(target: SModelElementImpl, event: MouseEvent): (Action | Promise<Action>)[] {
        return []
    }
}

//@injectable()
class DeleteElementCommandRemove extends DeleteElementCommand {
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

export { EditLabelMouseListenerRemove, DeleteElementCommandRemove }