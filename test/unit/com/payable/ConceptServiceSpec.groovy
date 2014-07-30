package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ConceptService)
@Mock([Organization,Concept])
class ConceptServiceSpec extends Specification {
  
  def "Get a list of concepts linked to an organization"(){
    given: "2 concepts will be created to an Organization according to the User"
      def organization = new Organization()
      organization.name = "Escuela Superior de Cómputo"
      organization.save()

      def concept1 = new Concept()
      def concept2 = new Concept()
      concept1.description = "Tuition"
      concept1.organization = organization
      concept1.save(flush:true) 

      concept2.description = "First tuition"
      concept2.organization = organization
      concept2.save(flush:true)

      def description = "iti"
    when: "A call to the method searchConceptsOfInstitution will be done"
      def institutionConcepts = service.searchConceptsOfInstitution(organization,description)

    then: "The concepts quantity must be equal to 2"
      assert institutionConcepts.size() == 2
  }
  
  def "When a concept of a generated payment is saved and it doesn´t exist must be created"(){
    given: "An organization and a concept description"
      def organization = new Organization()
      organization.name = "Escuela Superior de Cómputo"
      organization.save()
      String conceptDescription = "Inexistent Concept"

    when:"A call to the service that verifies the concept existence will be done"
      def savedConcept = service.savePaymentConcept(organization,conceptDescription)

    then:
      assert savedConcept.id == 1 
      assert savedConcept.description == "Inexistent Concept"
  }
  
  def "Should not save a duplicate payment concept by institution"(){
    given: ""
      String conceptDescription = "Existent concept"
      
      def organization = new Organization()
      organization.name = "Escuela Superior De Cómputo"
      organization.save()

      Concept concept = new Concept()
      concept.description = conceptDescription
      concept.organization = organization
      concept.save()

    when: "A call to the service that verifies the exsistence of the concept will be done"
      def savedConcept = service.savePaymentConcept(organization,conceptDescription)
    
    then:
      assert savedConcept.id == concept.id
      assert savedConcept.description == concept.description
  } 
}
