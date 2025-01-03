import type { CategoryOption } from './CategoryOption'
import colors from 'tailwindcss/colors'

/** List of all options of the Category Type */
export enum TypeOptionList {
  STATISTICAL_UNCERTAINTY = 'Statistical',
  SCENARIO_UNCERTAINTY = 'Scenario',
  RECOGNIZED_UNCERTAINTY = 'Recognized'
}

/** Map with detailed information about all options of the Category Type */
const typeOptions = {
  [TypeOptionList.STATISTICAL_UNCERTAINTY]: { name: 'Statistical Uncertainty' },
  [TypeOptionList.SCENARIO_UNCERTAINTY]: { name: 'Scenario Uncertainty' },
  [TypeOptionList.RECOGNIZED_UNCERTAINTY]: { name: 'Recognized Uncertainty' }
} as Record<TypeOptionList, CategoryOption>
typeOptions[TypeOptionList.STATISTICAL_UNCERTAINTY].icon = 'fa-chart-simple'
typeOptions[TypeOptionList.SCENARIO_UNCERTAINTY].icon = 'fa-list-ol'
typeOptions[TypeOptionList.RECOGNIZED_UNCERTAINTY].icon = 'fa-seal-question'

typeOptions[TypeOptionList.STATISTICAL_UNCERTAINTY].color = colors.lime
typeOptions[TypeOptionList.SCENARIO_UNCERTAINTY].color = colors.indigo
typeOptions[TypeOptionList.RECOGNIZED_UNCERTAINTY].color = colors.rose
export { typeOptions }
