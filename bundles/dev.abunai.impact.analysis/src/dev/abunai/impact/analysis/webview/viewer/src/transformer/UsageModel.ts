import { SModelElement } from "sprotty-protocol";
import { FlatMapTransformer, JsonBase } from "./base";
import { ActionBase, SeffTransformer } from "./Seff";
import { NODES } from "../diagramElements/nodes";
import { buildBaseNode } from "../diagramElements/nodes/BaseNode";

namespace Json {
  export interface UsageModel extends JsonBase {
    type: 'UsageModel',
    contents: UsageScenario[]
  }

  export interface UsageScenario extends JsonBase {
    type: 'UsageScenario',
    name: string,
    contents: ScenarioBehaviour[]
  }

  export interface ScenarioBehaviour extends JsonBase {
    type: 'ScenarioBehaviour'
    name: string,
    contents: ActionBase[]
  }
}

export type UsageModelFileContent = Json.UsageModel[]

export class UsageModelTransformer extends FlatMapTransformer<Json.UsageModel> {

  protected transformSingle(usageModel: Json.UsageModel): SModelElement[] {
    const seffTransformer = new SeffTransformer()
    return usageModel.contents.map(usageScenario => buildBaseNode(
        usageScenario.id,
        NODES.USAGE_SCENARIO,
        usageScenario.name,
        'UsageScenario',
        usageScenario.contents.map(scenarioBehaviour => buildBaseNode(
          scenarioBehaviour.id,
          NODES.SCENARIO_BEHAVIOUR,
          scenarioBehaviour.name,
          'ScenarioBehaviour',
          seffTransformer.transform(scenarioBehaviour.contents)
        ))
      )
    )
  }

}