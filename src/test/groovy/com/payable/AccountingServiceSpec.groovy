package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(AccountingService)
@Mock([Payment,PaymentWithImplements,PaymentWithNoImplements,PaymentLink])
class AccountingServiceSpec extends Specification {

  def "Create statement for a group of instances"(){
    given:
      def instances = [new PaymentWithImplements().save(),new PaymentWithImplements().save()]
      def paymentLinkForinstance1 = new PaymentLink(paymentRef:instances[0].id,type:instances[0].class.simpleName)
      def paymentLinkForinstance2 = new PaymentLink(paymentRef:instances[1].id,type:instances[1].class.simpleName)
      def payments = [new Payment(
        paymentDate: new Date(),
        dueDate: new Date(),
        paymentAmount: 100,
        paymentConcept:"Payment Concept"
      ), new Payment(
        paymentDate: new Date(),
        dueDate: new Date(),
        paymentAmount: 100,
        paymentConcept:"Payment Concept"
      )]

      paymentLinkForinstance1.addToPayments(payments[0])
      paymentLinkForinstance1.save(validate:false)
      paymentLinkForinstance2.addToPayments(payments[1])
      paymentLinkForinstance2.save(validate:false)

    and:
      def paymentServiceMock = mockFor(PaymentService)
      paymentServiceMock.demand.findAllPaymentsForTheInstances{ i ->  payments}
      service.paymentService = paymentServiceMock.createMock()
    when:
      def statement = service.createStatementForInstances(instances)   
    then:
      statement
      !statement.paymentsExpired
      statement.paymentsToBeMade
      statement.paymentsOnTime
  }
}
