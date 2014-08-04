package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(PaymentSchemeService)
@Mock([Concept,Organization,Surcharge,Discount,PaymentScheme])
class PaymentSchemeServiceSpec extends Specification {

  def "Create a payment scheme for an organization"(){
    given:"an organization"
      Organization organization = new Organization(name:"Escuela Superior De Cómputo").save(validate:false)

    and: "a Surcharge"
      Surcharge surcharge = new Surcharge(amount:350).save(validate:false)

    and: "a Discount"
      Discount discount = new Discount(
        discountName:"Discount",
        amount:5000,
        previousDaysForCancelingDiscount:6,
        organization:organization).save(validate:false)

    and: 
      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentAmount:13000,
        paymentConcept:"School trip",
        surchargeId:surcharge.id,
        discountIds:[discount.id]) 
    when:
      def paymentScheme = service.savePaymentScheme(paymentGroupCommand) 

    then:
      assert paymentScheme.id > 0
      assert paymentScheme.surcharge.id > 0
      assert paymentScheme.paymentAmount == 13000
  }

}
