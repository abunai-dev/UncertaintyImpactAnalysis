import type { CategoryOption } from './CategoryOption'
import colors from 'tailwindcss/colors'

/** List of all options of the Category Impact on Confidentiality */
export enum ImpactOnConfidentialityOptionList {
  DIRECT = 'Direct',
  INDIRECT = 'Indirect',
  NONE = 'impactNone'
}

/** Map with detailed detailed information all options of the Category Impact on Confidentiality */
const impactOnConfidentialityOptions = {
  [ImpactOnConfidentialityOptionList.DIRECT]: { name: 'Direct' },
  [ImpactOnConfidentialityOptionList.INDIRECT]: { name: 'Indirect' },
  [ImpactOnConfidentialityOptionList.NONE]: { name: 'None' }
} as Record<
  ImpactOnConfidentialityOptionList,
  CategoryOption
>
impactOnConfidentialityOptions[ImpactOnConfidentialityOptionList.DIRECT].icon = 'icon-impact-direct'
impactOnConfidentialityOptions[ImpactOnConfidentialityOptionList.INDIRECT].icon =
  'icon-impact-indirect'
impactOnConfidentialityOptions[ImpactOnConfidentialityOptionList.NONE].icon = 'icon-impact-none'
export { impactOnConfidentialityOptions }
