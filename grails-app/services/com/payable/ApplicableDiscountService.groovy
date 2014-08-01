package com.payable

class ApplicableDiscountService {

  def generateForPaymentWithExpirationDate(def expirationDate,Long discountId){
    def discount = Discount.get(discountId)  
    def applicableDiscount = new ApplicableDiscount()
    applicableDiscount.expirationDate = expirationDate
    applicableDiscount.discount = discount
    applicableDiscount
  }

  def generateForPaymentWithPaymentSchemeWithReferenceDate(Long paymentSchemaId,Date referenceDate){
    def applicableDiscounts = []
    PaymentScheme paymentScheme = PaymentScheme.get(paymentSchemaId) 
    log.error "Reference date ${referenceDate}"
    paymentScheme.discounts.each{ discount ->
      def expirationDate = (referenceDate - discount.previousDaysForCancelingDiscount) 
      if(expirationDate.clearTime() < new Date().clearTime())
        applicableDiscounts << generateForPaymentWithExpirationDate(expirationDate,discount.id)  
    }

    applicableDiscounts
  }

  Payment addApplicableDiscountToAPayment(ApplicableDiscount applicableDiscount,Long paymentId){
    def payment = Payment.get(paymentId)
    if(applicableDiscount.discount.percentage)  
      payment.accumulatedDiscount += payment.paymentAmount / 100 * applicableDiscount.discount.percentage
    else if(applicableDiscount.discount.amount)
      payment.accumulatedDiscount += applicableDiscount.discount.amount

    payment.addToApplicableDiscounts(applicableDiscount)
    payment.save()
    payment

  }
  
  def invalidateApplicableDiscountToAPayment(ApplicableDiscount applicableDiscount,Long paymentId){
    Payment payment = Payment.get(paymentId)
    if(applicableDiscount.discount.percentage)
      payment.accumulatedDiscount -= (payment.paymentAmount * applicableDiscount.discount.percentage)  / 100
    else
      payment.accumulatedDiscount -= applicableDiscount.discount.amount
    
    payment.save()
  }

}
