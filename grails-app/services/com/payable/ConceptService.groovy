package com.payable

class ConceptService {
  
  static transactional = true
  
  def searchConceptsOfInstitution(Organization organization, def description){
    Concept.withCriteria{
      like('description',"%${description}%")
      eq ('organization',organization) 
    }
  } 

  def savePaymentConcept(Organization organization,conceptDescription){ 
    Concept concept = Concept.findByDescriptionAndOrganization(conceptDescription,organization) ?: new Concept(description:conceptDescription,
                      organization:organization).save()
    concept
  }
}
