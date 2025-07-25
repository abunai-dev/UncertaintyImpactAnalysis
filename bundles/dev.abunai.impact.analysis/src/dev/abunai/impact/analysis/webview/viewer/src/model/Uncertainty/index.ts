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