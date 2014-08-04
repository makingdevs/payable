package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SurchargeService)
@Mock([Organization,Surcharge])
class SurchargeServiceSpec extends Specification {

  def "Save a surcharge if it doesn`t exist in an organization"(){
    given:"an organization and the amount of the Surcharge"
      def organization = new Organization()
      organization.name = "Escuela Superior de Cómputo"
      organization.save()
      def amount = 200 
    when:
      def surcharge = service.saveSurcharge([organization:organization,amount:amount])
    then:
      surcharge.id > 0
      assert surcharge.organization == organization
  }
}
