/** List of all categories of the classification */
export enum CategoryList {
  LOCATION = 'Location',
  ARCHITECTURAL_ELEMENT_TYPE = 'ArchitecturalElementType',
  TYPE = 'Type',
  MANAGEABILITY = 'Manageability',
  RESOLUTION_TIME = 'ResolutionTime',
  REDUCIBLE_BY_ADD = 'ReducibleByADD',
  IMPACT_ON_CONFIDENTIALITY = 'ImpactOnConfidentiality',
  SEVERITY_OF_IMPACT = 'SeverityOfTheImpact'
}

/** List of all categories of the classification in the order they should be displayed */
export const categoryOrder = [
  CategoryList.LOCATION,
  CategoryList.ARCHITECTURAL_ELEMENT_TYPE,
  CategoryList.TYPE,
  CategoryList.MANAGEABILITY,
  CategoryList.RESOLUTION_TIME,
  CategoryList.REDUCIBLE_BY_ADD,
  CategoryList.IMPACT_ON_CONFIDENTIALITY,
  CategoryList.SEVERITY_OF_IMPACT
]