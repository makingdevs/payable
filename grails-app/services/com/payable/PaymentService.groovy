package com.payable

class PaymentService {

  def createPaymentForThisInstance(instance,payment) {
    PaymentLink paymentLink = new PaymentLink()
    paymentLink.paymentRef = instance.id
    paymentLink.type = instance.class.simpleName
    paymentLink.addToPayments(payment)
    paymentLink.save()
    paymentLink
  }
}
