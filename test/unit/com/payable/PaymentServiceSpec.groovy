package com.payable

import grails.test.mixin.*
import spock.lang.*

@TestFor(PaymentService)
@Mock([PaymentWithImplements, Payment, PaymentLink])
class PaymentServiceSpec extends Specification {

	void "Should save a payment in a class with the Payable interface"() {
    given:
      def instance = new PaymentWithImplements().save()
      def payment = new Payment()
    when:
      def paymentLink = service.createPaymentForThisInstance(instance,payment)
    then:
      paymentLink.id
      paymentLink.type == "PaymentWithImplements"
      paymentLink.payments
      paymentLink.payments.first().id == payment.id
	}
  @FailsWith
  void "Should fail a payment when trying to add in a class with no poyable interface"(){
    given:
      def instance = new PaymentWithImplements().save()
      def payment = new Payment()
    when:
      def paymentLink = service.createPaymentForThisInstance(instance)
    then:
      !paymentLink.id
  }
}
