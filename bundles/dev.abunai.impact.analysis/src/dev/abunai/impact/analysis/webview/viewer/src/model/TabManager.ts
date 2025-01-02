import type { SGraph } from "sprotty-protocol"
import { nextTick } from "vue"


export class TabManager {
  public static INSTANCE: TabManager|undefined
  private tabIndex: number
  private indexChangeListeners: ChangeIndexListener[] = []
  private tabs: Tab[]
  private changeListeners: ChangeListener[] = []

  protected constructor() {
    this.tabIndex = 0
    this.tabs = []
  }

  public static getInstance() {
    if (TabManager.INSTANCE == undefined) {
      TabManager.INSTANCE = new TabManager()
    }
    return TabManager.INSTANCE
  }

  public setIndex(i: number) {
    let oldIndex = this.tabIndex
    if (i < 0) {
      this.tabIndex = 0
    }
    else if (i >= this.tabs.length) {
      this.tabIndex = this.tabs.length - 1
    } else {
      this.tabIndex = i
    }
    if (oldIndex != this.tabIndex) {
      this.indexChangeListeners.forEach(listener => {
        listener(this.tabIndex, oldIndex)
      })
    }
    if (this.tabs[oldIndex].autoClose) {
      nextTick(() => this.removeTab(this.tabs[oldIndex].uuid))
    }
  }

  public getTab(idx: number) {
    return this.tabs[idx]
  }

  public getUuids() {
    return this.tabs.map(tab => tab.uuid)
  }

  public addTab(name: string, content: SGraph, closable: boolean, autoClose: boolean) {
    let newUuid = this.generateUuid()
    this.tabs.push({name, closable, autoClose, uuid: newUuid, content})
    nextTick(() => this.changeListeners.forEach(listener => {
      listener()
    }))
    return newUuid
  }

  public removeTab(uuid: string) {
    let idx = this.tabs.findIndex(tab => tab.uuid == uuid)

    if (idx == -1) {
      return
    }

    if (!this.tabs[idx].closable) {
      return
    }

    if (this.tabs.length == 1) {
      return
    }

    if (idx == this.tabIndex) {
      if (idx == 0) {
        this.tabIndex = 1
      } else {
        this.tabIndex = idx - 1
      }
    }

    this.tabs.splice(idx, 1)
    nextTick(() => {
      this.changeListeners.forEach(listener => {
        listener()
      })
      this.indexChangeListeners.forEach(listener => {
        listener(this.tabIndex, idx)
      })
    })
    if (this.tabIndex > idx) {
      this.tabIndex--
    }
    
  }

  public addIndexChangeListener(listener: ChangeIndexListener) {
    this.indexChangeListeners.push(listener)
  }

  public addChangeListener(listener: ChangeListener) {
    this.changeListeners.push(listener)
  }

  private generateUuid() {
    return Math.random().toString(36).substring(7)
  }
}

type ChangeIndexListener = (newIndex: number, oldIndex: number) => void
type ChangeListener = () => void

interface Tab {
  closable: boolean
  autoClose: boolean
  uuid: string
  name: string
  content: SGraph
}