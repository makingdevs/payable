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

  def findAllPaymentsGroupedByStatus(instances){
    def paymentsGroupedByStatus = [:]
    def payments = findAllPaymentsForTheInstances(instances) 
    
    paymentsGroupedByStatus.createdPayments = payments.findAll{ it?.paymentStatus == PaymentStatus.CREATED }  
    paymentsGroupedByStatus.paymentsInProcess = payments.findAll{ it?.paymentStatus == PaymentStatus.PROCESS }
    paymentsGroupedByStatus.reconciliedPayments = payments.findAll { it?.paymentStatus == PaymentStatus.PAID } 
    paymentsGroupedByStatus.rejectedPayments = payments.findAll { it?.paymentStatus == PaymentStatus.REJECTED }
    paymentsGroupedByStatus.expiredPayments = payments.findAll { it?.paymentStatus == PaymentStatus.EXPIRED }

    paymentsGroupedByStatus 
  } 
}
