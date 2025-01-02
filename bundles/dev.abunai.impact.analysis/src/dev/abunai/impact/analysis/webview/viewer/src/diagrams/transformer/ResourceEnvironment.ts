import type { SEdge, SModelElement } from "sprotty-protocol"
import { type BaseNode, buildBaseNode } from "../diagramElements/nodes/schemes/BaseNode"
import { EDGES } from "../diagramElements/edges"
import { FlatMapTransformer, getOfType, type JsonBase } from "./base"
import { NODES } from "../diagramElements/nodes"

namespace Json {
  export interface ResourceEnvironment extends JsonBase {
    type: "ResourceEnvironment",
    contents: (ResourceContainer|LinkingResource)[]
  }

  export interface ResourceContainer extends JsonBase {
    name: string,
    type: "ResourceContainer"
  }

  export interface LinkingResource extends JsonBase {
    name: string,
    connectedResourceContainer: string[],
    type: "LinkingResource"
  }
}

export type ResourceEnvironmentFileContent = Json.ResourceEnvironment[]

export class ResourceEnvironmentTransformer extends FlatMapTransformer<Json.ResourceEnvironment> {
  transformSingle(resourceEnvrironment: Json.ResourceEnvironment): SModelElement[] {
    const contents: (BaseNode|SEdge)[] = []
  for (const resourceContainer of getOfType<Json.ResourceContainer>(resourceEnvrironment.contents, 'ResourceContainer')) {
    contents.push(buildBaseNode(
      resourceContainer.id,
      NODES.RESOURCE_CONTAINER,
      resourceContainer.name,
      'ResourceContainer'
    ))
  }
  for (const linkingResource of getOfType<Json.LinkingResource>(resourceEnvrironment.contents, 'LinkingResource')) {
    contents.push(buildBaseNode(
      linkingResource.id,
      NODES.LINKING_RESOURCE,
      linkingResource.name,
      'LinkingResource'
    ))
    for (const resourceContainer of linkingResource.connectedResourceContainer) {
      contents.push({
        type: EDGES.LINE,
        id: linkingResource.id + resourceContainer,
        sourceId: linkingResource.id,
        targetId: resourceContainer
      })
    }
  }
  return contents
  }
}