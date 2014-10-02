package com.payable

class PaymentService {

  def createPaymentsForThisInstance(instance,payments) {
    if(!IPayable.class.isAssignableFrom(instance.class)){
      throw new RuntimeException("IPayable is not assignable from ${instance.class}")
    }
    PaymentLink paymentLink = PaymentLink.findByPaymentRefAndType(instance.id,instance.class.simpleName) ?: new PaymentLink(
      paymentRef:instance.id,
      type:instance.class.simpleName
    )
    
    payments.each{ payment -> paymentLink.addToPayments(payment) }
    paymentLink.save()
    paymentLink
  }

  def findAllPaymentsByInstance(instance){
    if(!IPayable.class.isAssignableFrom(instance.class)){
      throw new RuntimeException("IPayable is not assignable from ${instance.class}")
    }
    def paymentLink = PaymentLink.findByPaymentRefAndType(instance.id,instance.class.simpleName)
    paymentLink?.payments
  }

  def findAllPaymentsForTheInstances(instances){
    def payments = []
    instances.each{ instance ->
      payments += findAllPaymentsByInstance(instance) 
    } 
    
    payments  
  }   
}
