package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(GenerationOfPaymentService)
@Mock([PaymentWithImplements,Payment,Discount,Concept,Surcharge,Organization,PaymentLink])
class GenerationOfPaymentServiceSpec extends Specification {

  def "Generation of payment for a litter"(){
    setup:
      
      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentConcept: _paymentConcept,
        paymentAmount: _paymentAmount,
        discountIds: [],
        doublePayment: [],
        daysPaymentDue: 4,
        dueDate: _dueDate,
        organization: new Organization().save(validate:false),
        instances: [new PaymentWithImplements().save()]
      )
    and:
      def conceptServiceMock = mockFor(ConceptService)
      conceptServiceMock.demand.savePaymentConcept{ organization, paymentConcept ->
        new Concept(organization:organization,paymentConcept:paymentConcept).save(validate:false)
      }
      service.conceptService = conceptServiceMock.createMock()

    when:
      def payments = service.generatePaymentForGroup(paymentGroupCommand)

    then:
      assert payments.size() == _size
      assert payments.first().id > 0

    where:
     _paymentConcept | _paymentAmount | _dueDate      || _size  
     "PaymentConcept"|  100.00        | new Date()+7  || 1
  }
}
