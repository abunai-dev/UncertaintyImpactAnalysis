import { CategoryList } from '../Category'
import {
  architecturalElementTypeOptions,
  type ArchitecturalElementTypeOptionList
} from './ArchitecturalElementTypeOptions'
import {
  impactOnConfidentialityOptions,
  type ImpactOnConfidentialityOptionList
} from './ImpactOnConfidentialityOptions'
import { locationOptions, type LocationOptionList } from './LocationOptions'
import { manageabilityOptions, type ManageabilityOptionList } from './ManageabilityOption'
import { reducibleByAddOptions, type ReducibleByAddOptionList } from './ReducibleByAddOptions'
import { resolutionTimeOptions, type ResolutionTimeOptionList } from './ResolutionTimeOptions'
import {
  severityOfTheImpactOptions,
  type SeverityOfTheImpactOptionList
} from './SeverityOfTheImpactOptions'
import { typeOptions, type TypeOptionList } from './TypeOptions'

/**
 * Represents a value a class can have
 */
export interface CategoryOption {
  name: string
  icon?: string
}

/** List of all possible values of options of any category can have */
export type CategoryOptionList =
  | ArchitecturalElementTypeOptionList
  | ImpactOnConfidentialityOptionList
  | LocationOptionList
  | ManageabilityOptionList
  | ReducibleByAddOptionList
  | ResolutionTimeOptionList
  | SeverityOfTheImpactOptionList
  | TypeOptionList

/** Map with detailed information about all options in the classification */
export const categoryOptions = {
  [CategoryList.ARCHITECTURAL_ELEMENT_TYPE]: architecturalElementTypeOptions,
  [CategoryList.IMPACT_ON_CONFIDENTIALITY]: impactOnConfidentialityOptions,
  [CategoryList.LOCATION]: locationOptions,
  [CategoryList.MANAGEABILITY]: manageabilityOptions,
  [CategoryList.REDUCIBLE_BY_ADD]: reducibleByAddOptions,
  [CategoryList.RESOLUTION_TIME]: resolutionTimeOptions,
  [CategoryList.SEVERITY_OF_IMPACT]: severityOfTheImpactOptions,
  [CategoryList.TYPE]: typeOptions
} as Record<CategoryList, Record<CategoryOptionList, CategoryOption>>
