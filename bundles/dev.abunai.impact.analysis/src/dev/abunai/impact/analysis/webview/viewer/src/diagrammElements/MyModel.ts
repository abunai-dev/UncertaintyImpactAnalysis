import { LocalModelSource } from 'sprotty'
import { Action, Match, SModelIndex, SModelRoot } from 'sprotty-protocol'

export class MyModel extends LocalModelSource {
    protected doSubmitModel(
        newRoot: SModelRoot,
        update: boolean | Match[],
        cause?: Action,
        index?: SModelIndex
    ): Promise<void> {
        return super.doSubmitModel(newRoot, update, cause, index)
        
    }
}
