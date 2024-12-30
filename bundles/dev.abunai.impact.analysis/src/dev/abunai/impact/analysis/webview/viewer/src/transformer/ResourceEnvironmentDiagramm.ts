import _json from './resourceEnvironment.json'
import { ResourceContainerScheme } from "../diagrammElements/allocationDiagram/ResourceContainer"
import { LinkingResourceScheme } from "../diagrammElements/resourceEnvironment/LinkingResource"
import { SEdge, SModelElement } from "sprotty-protocol"
import { getOfType } from "./common"

type ID = string

namespace Json {
  export interface ResourceEnvironment {
    type: "ResourceEnvironment",
    id: ID,
    contents: (ResourceContainer|LinkingResource)[]
  }

  export interface ResourceContainer {
    name: string,
    type: "ResourceContainer",
    id: ID
  }

  export interface LinkingResource {
    name: string,
    connectedResourceContainer: string[],
    type: "LinkingResource",
    id: ID
  }
}

export function transform(): SModelElement[] {
  return (_json as Json.ResourceEnvironment[]).flatMap(transformResourceEnvrionment)
}

function transformResourceEnvrionment(resourceEnvrironment: Json.ResourceEnvironment): (ResourceContainerScheme|LinkingResourceScheme|SEdge)[] {
  const contents: (ResourceContainerScheme|LinkingResourceScheme|SEdge)[] = []
  for (const resourceContainer of getOfType<Json.ResourceContainer>(resourceEnvrironment.contents, 'ResourceContainer')) {
    contents.push({
      typeName: 'ResourceContainer',
      name: resourceContainer.name,
      type: 'node:allocation:resource_container',
      id: resourceContainer.id
    })
  }
  for (const linkingResource of getOfType<Json.LinkingResource>(resourceEnvrironment.contents, 'LinkingResource')) {
    contents.push({
      typeName: 'LinkingResource',
      name: linkingResource.name,
      type: 'node:resource_environment:linking_resource',
      id: linkingResource.id
    })
    for (const resourceContainer of linkingResource.connectedResourceContainer) {
      contents.push({
        type: 'edge:line',
        id: linkingResource.id + resourceContainer,
        sourceId: linkingResource.id,
        targetId: resourceContainer
      })
    }
  }
  return contents
}
