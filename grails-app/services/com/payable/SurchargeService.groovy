package com.payable

class SurchargeService {

  def saveSurcharge(def params){
    Surcharge surcharge = Surcharge.findAllByOrganization(params.organization) ?: new Surcharge(params).save()
  }
 
  def expirePaymentsAndCalculateAccumulatedSurcharge(){

    def payments = Payment.withCriteria{
      lt("dueDate",new Date())   
      inList("paymentStatus",[PaymentStatus.CREATED,PaymentStatus.REJECTED])
    }
    
    payments.each{ payment ->
      if(payment.surcharge)
        payment.accumulatedSurcharges = calculateAccumulatedSurcharge(payment.surcharge, payment.paymentAmount)
      payment.paymentStatus = PaymentStatus.EXPIRED 
      payment.save()
    }

    payments
  }

  def calculateAccumulatedSurcharge(Surcharge surcharge, def paymentAmount){
    if(surcharge.percentage) 
      return ((paymentAmount * surcharge.percentage) / 100)
    
    surcharge.amount
  }

}
