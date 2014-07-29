package com.payable

class ConceptService {
  
  static transactional = true
  
  def searchConceptsOfInstitution(Organization organization, def description){
    Concept.withCriteria{
      like('description',"%${description}%")
      eq ('organization',organization) 
    }
  } 
}
