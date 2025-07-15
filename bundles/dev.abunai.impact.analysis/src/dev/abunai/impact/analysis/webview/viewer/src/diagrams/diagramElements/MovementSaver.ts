import { TabManager } from "@/model/TabManager";
import { MoveCommand, SChildElementImpl, SetViewportCommand, SGraphImpl, SModelRootImpl, SParentElementImpl, type CommandExecutionContext, type CommandResult, type CommandReturn } from "sprotty";
import type { SGraph, SModelElement } from "sprotty-protocol";
import { injectable } from "inversify";

@injectable()
export class MovementSaver extends MoveCommand {

  execute(context: CommandExecutionContext): CommandReturn {
      const result = super.execute(context);

      // test if result is a promise
      if (result instanceof Promise) {
          return result.then(model => {
            save(model);
              return model;
          });
      } else if ((result as SModelRootImpl).children !== undefined) {
        save(result as SModelRootImpl);
      } else {
        save((result as CommandResult).model);
      }

      return result;
  }

  async undo(context: CommandExecutionContext): Promise<SModelRootImpl> {
    const model = await super.redo(context);
    save(model);
    return model;
  }

  async redo(context: CommandExecutionContext): Promise<SModelRootImpl> {
    const model = await super.redo(context);
    save(model);
    return model;
  }
}

export class ZoomSaver extends SetViewportCommand {
  
  execute(context: CommandExecutionContext): CommandReturn {
    const result = super.execute(context);

    // test if result is a promise
    if (result instanceof Promise) {
        return result.then(model => {
          save(model);
            return model;
        });
    } else if ((result as SModelRootImpl).children !== undefined) {
      save(result as SModelRootImpl);
    } else {
      save((result as CommandResult).model);
    }

    return result;
  }

  undo(context: CommandExecutionContext): CommandReturn {
    const result = super.undo(context);

    // test if result is a promise
    if (result instanceof Promise) {
        return result.then(model => {
          save(model);
            return model;
        });
    } else if ((result as SModelRootImpl).children !== undefined) {
      save(result as SModelRootImpl);
    } else {
      save((result as CommandResult).model);
    }

    return result;
  }

  redo(context: CommandExecutionContext): CommandReturn {
    const result = super.redo(context);

    // test if result is a promise
    if (result instanceof Promise) {
        return result.then(model => {
          save(model);
            return model;
        });
    } else if ((result as SModelRootImpl).children !== undefined) {
      save(result as SModelRootImpl);
    } else {
      save((result as CommandResult).model);
    }

    return result;
  }

  handle(result: CommandReturn): CommandReturn {
    if (result instanceof Promise) {
      return result.then(model => {
        save(model);
          return model;
      });
    } else if ((result as SModelRootImpl).children !== undefined) {
      save(result as SModelRootImpl);
    } else {
      save((result as CommandResult).model);
    }

    return result;
  }
}

function save(model: SModelRootImpl) {
  const graph: SGraph = {
    type: 'graph',
    id: model.id,
    children: model.children.map(transform),
    zoom: (model as SGraphImpl).zoom,
    scroll: (model as SGraphImpl).scroll
  }
  TabManager.getInstance().setSelectedContentNonChanging(graph);
}

function transform(model: SChildElementImpl): SModelElement {
  const result = {
    ...model,
    children: model.children.map(transform),
    parent: undefined
  }

  return result
}