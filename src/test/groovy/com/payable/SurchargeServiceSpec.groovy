package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SurchargeService)
@Mock([Organization,Surcharge,Payment])
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

  void "Expire the payments if the dueDate is less than today and calculate the Surcharge"(){
    given: "A payment with a Surcharge"
      def payment = _payment
      payment.surcharge = _surcharge
      payment.save(validate:false) 

    when:
      service.expirePaymentsAndCalculateAccumulatedSurcharge()

    then:
      payment.accumulatedSurcharges == _accumulatedSurcharges

    where:
      _payment                                                 | _surcharge                     || _accumulatedSurcharges
      new Payment(dueDate: new Date()-7,paymentAmount:2000)    | new Surcharge(amount:100)      || 100
      new Payment(dueDate: new Date()-7,paymentAmount:5890)    | new Surcharge(percentage:10)   || 589
      new Payment(dueDate: new Date(),paymentAmount:4000)      | null                           || 0
  }

}
