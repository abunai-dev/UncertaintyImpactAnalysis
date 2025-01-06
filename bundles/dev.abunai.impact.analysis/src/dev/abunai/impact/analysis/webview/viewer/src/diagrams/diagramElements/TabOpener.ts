import { TabManager } from "@/model/TabManager";
import { MouseListener, SModelElementImpl } from "sprotty";
import type { Action } from "sprotty-protocol";
import { SeffSignatureImpl } from "./nodes/BasicComponent";

export class TabOpenerMouseListener extends MouseListener {

  doubleClick(target: SModelElementImpl, event: MouseEvent): (Action | Promise<Action>)[] {
    if (target instanceof SeffSignatureImpl) {
      const newTab = TabManager.getInstance().addTab(target.text, target.graph, true, false)
      TabManager.getInstance().selectTab(newTab)
    }
    
    return []
  }

}