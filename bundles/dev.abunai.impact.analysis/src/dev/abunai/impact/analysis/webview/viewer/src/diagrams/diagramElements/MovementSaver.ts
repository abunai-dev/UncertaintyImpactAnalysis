import { TabManager } from "@/model/TabManager";
import { MoveCommand, SChildElementImpl, SModelRootImpl, SParentElementImpl, type CommandExecutionContext, type CommandResult, type CommandReturn } from "sprotty";
import type { SGraph, SModelElement } from "sprotty-protocol";
import { injectable } from "inversify";

@injectable()
export class MovementSaver extends MoveCommand {

  execute(context: CommandExecutionContext): CommandReturn {
      const result = super.execute(context);

      // test if result is a promise
      if (result instanceof Promise) {
          return result.then(model => {
              this.saveMovement(model);
              return model;
          });
      } else if ((result as SModelRootImpl).children !== undefined) {
          this.saveMovement(result as SModelRootImpl);
      } else {
        this.saveMovement((result as CommandResult).model);
      }

      return result;
  }

  async undo(context: CommandExecutionContext): Promise<SModelRootImpl> {
    const model = await super.redo(context);
    this.saveMovement(model);
    return model;
  }

  async redo(context: CommandExecutionContext): Promise<SModelRootImpl> {
    const model = await super.redo(context);
    this.saveMovement(model);
    return model;
  }

  saveMovement(model: SModelRootImpl) {
    const graph: SGraph = {
      type: 'graph',
      id: model.id,
      children: model.children.map(MovementSaver.transform)
    }
    TabManager.getInstance().setSelectedContentNonChanging(graph);
  }

  static transform(model: SChildElementImpl): SModelElement {
    const result = {
      ...model,
      children: model.children.map(MovementSaver.transform),
      parent: undefined
    }

    return result
  }
}