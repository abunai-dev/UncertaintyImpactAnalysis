import type { CategoryList } from "./Category"
import type { CategoryOptionList } from "./option/CategoryOption"

/**
 * Basic information about an uncertainty
 */
export interface BaseUncertainty {
  id: number
  name: string
  classes: Record<CategoryList, CategoryOptionList>
  keywords: string[]
}

/**
 * Elaborated information about an uncertainty
 */
export interface Uncertainty extends BaseUncertainty {
  description: string
  exampleText: string
  exampleImages: string[]
  relatedUncertainties: BaseUncertainty[]
  children: BaseUncertainty[]
  parent?: BaseUncertainty
  sourceReferenceLink?: string
}

/**
 * Uncertainty format from ARC3N
 */
export interface JsonUncertainty extends BaseUncertainty {
  description: string
  exampleText: string
  exampleImages: string[]
  relatedUncertainties: number[]
  children: number[]
  parent?: number
  sourceReferenceLink?: string
}