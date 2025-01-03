import type { CategoryOption } from './CategoryOption'
import colors from 'tailwindcss/colors'

/** List of all options of the Category Resolution Time */
export enum ResolutionTimeOptionList {
  REQUIREMENTS_TIME = 'Requirements',
  DESIGN_TIME = 'Design',
  REALIZATION_TIME = 'Realization',
  RUNTIME = 'Runtime'
}

/** Map with detailed information about all options of the Category Resolution Time */
const resolutionTimeOptions = {
  [ResolutionTimeOptionList.REQUIREMENTS_TIME]: { name: 'Requirements Time' },
  [ResolutionTimeOptionList.DESIGN_TIME]: { name: 'Design Time' },
  [ResolutionTimeOptionList.REALIZATION_TIME]: { name: 'Realization Time' },
  [ResolutionTimeOptionList.RUNTIME]: { name: 'Runtime' }
} as Record<
  ResolutionTimeOptionList,
  CategoryOption
>
resolutionTimeOptions[ResolutionTimeOptionList.REQUIREMENTS_TIME].icon = 'fa-list'
resolutionTimeOptions[ResolutionTimeOptionList.DESIGN_TIME].icon = 'fa-pen'
resolutionTimeOptions[ResolutionTimeOptionList.REALIZATION_TIME].icon = 'fa-code-simple'
resolutionTimeOptions[ResolutionTimeOptionList.RUNTIME].icon = 'fa-clock'

resolutionTimeOptions[ResolutionTimeOptionList.REQUIREMENTS_TIME].color = colors.blue
resolutionTimeOptions[ResolutionTimeOptionList.DESIGN_TIME].color = colors.teal
resolutionTimeOptions[ResolutionTimeOptionList.REALIZATION_TIME].color = colors.orange
resolutionTimeOptions[ResolutionTimeOptionList.RUNTIME].color = colors.violet
export { resolutionTimeOptions }
