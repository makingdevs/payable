package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DiscountService)
@Mock([Organization,Discount,Payment])
class DiscountServiceSpec extends Specification {

  def "Get all the discounts linked to an organization"(){
    given: "A discount associated to an organization"
      def organization = new Organization()
      organization.name = "Escuela Superior de Cómputo"
      organization.save()  

      def discount = new Discount()
      discount.discountName = "Prepayment"
      discount.percentage = 10
      discount.organization = organization
      discount.save()
    when: "A call to the method searchDiscountOfAnOrganization"
      def stringToSearch = "pay"
      def discountOfInstitution = service.searchDiscountsOfAnOrganization(organization,stringToSearch)
    then:
      assert discountOfInstitution.size() == 1
  }

}
