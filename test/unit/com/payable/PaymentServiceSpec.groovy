package com.payable

import grails.test.mixin.*
import spock.lang.*

@TestFor(PaymentService)
@Mock([PaymentWithNoImplements,PaymentWithImplements, Payment, PaymentLink])
class PaymentServiceSpec extends Specification {

	void "Should save a payment in a class with the Payable interface"() {
    given:
      def instance = new PaymentWithImplements().save()
      def payment = new Payment()
    when:
      def paymentLink = service.createPaymentsForThisInstance(instance,[payment])
    then:
      paymentLink.id
      paymentLink.type == "PaymentWithImplements"
      paymentLink.payments
      paymentLink.payments.first().id == payment.id
	}

  @FailsWith(RuntimeException)
  void "Should fail a payment when trying to add in a class with no poyable interface"(){
    given:
      def instance = new PaymentWithNoImplements().save()
      def payment = new Payment()
    when:
      def paymentLink = service.createPaymentsForThisInstance(instance,[payment])
    then:
      !paymentLink.id
  }


  def "Should save many payments in a class with the Payable interface"(){
    given:
      def instance = new PaymentWithImplements().save()
      def payments = [new Payment(),
                      new Payment(),
                      new Payment()]

    when:
      def paymentLink = service.createPaymentsForThisInstance(instance,payments)
    then:
      assert paymentLink.payments.size() == 3
  }

  def "Should get the payment of an instance"(){
   given:
    def instance = new PaymentWithImplements().save()
    def payment = new Payment()
    def paymentLink = new PaymentLink(paymentRef:instance.id,type:instance.class.simpleName)
    paymentLink.addToPayments(payment)
    paymentLink.save(validate:false)
   
   when:
    def paymentsForThisInstance = service.findAllPaymentsByInstance(instance)
      
   then:
    paymentsForThisInstance.size()
    paymentsForThisInstance.first().id == payment.id
  }

  def "Should get the payments of all instances"(){
    given:
      def instances = [new PaymentWithImplements().save(),new PaymentWithImplements().save()]
      def paymentLink1 = service.createPaymentsForThisInstance(instances[0],[new Payment()])  
      def paymentLink2 = service.createPaymentsForThisInstance(instances[1],[new Payment(),new Payment()])
    when:
      def payments = service.findAllPaymentsForTheInstances(instances)
    then:
      payments.size() == 3
  }
 
  def "Should get the payments grouped by status"(){
    given:
      def instances = [new PaymentWithImplements().save()]
      def paymentLink1 = service.createPaymentsForThisInstance(instances[0],
                        [new Payment(paymentStatus:PaymentStatus.EXPIRED),
                         new Payment(paymentStatus:PaymentStatus.REJECTED)])  
    when:
      def payments = service.findAllPaymentsGroupedByStatus(instances)
    then:
      payments.expiredPayments.size() == 1
      payments.rejectedPayments.size() == 1
  }

}
