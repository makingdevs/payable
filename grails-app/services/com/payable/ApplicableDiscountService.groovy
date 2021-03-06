package com.payable

class ApplicableDiscountService {

  def generateApplicableDiscountForPaymentWithExpirationDate(def expirationDate,Long discountId){
    def discount = Discount.get(discountId)  
    def applicableDiscount = new ApplicableDiscount(expirationDate:expirationDate,
                                                    discount:discount)
    applicableDiscount.save()
    applicableDiscount
  }

  def generateApplicableDiscountsForPaymentWithPaymentSchemeAndReferenceDate(Long paymentSchemaId,Date referenceDate, def expirationDates = []){
    def applicableDiscounts = []
    def expirationDate

    PaymentScheme paymentScheme = PaymentScheme.get(paymentSchemaId) 
    paymentScheme.discounts?.sort{ discount -> discount.id}.eachWithIndex{ discount, i ->
      if(expirationDates)
        expirationDate = expirationDates[i]
      else if(discount.previousDaysForCancelingDiscount)
        expirationDate = (referenceDate - discount.previousDaysForCancelingDiscount) 
        
      if(referenceDate > expirationDate && expirationDate?.clearTime() > new Date().clearTime())
        applicableDiscounts << generateApplicableDiscountForPaymentWithExpirationDate(expirationDate,discount.id)  
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

  def expireDiscountsAndRecalculatePayment(){
    
    def applicableDiscounts = ApplicableDiscount.withCriteria(){
      eq 'applicableDiscountStatus',ApplicableDiscountStatus.VALID 
      ge 'expirationDate', new Date()
    }

    applicableDiscounts.each{ applicableDiscount ->
      applicableDiscount.applicableDiscountStatus = ApplicableDiscountStatus.EXPIRED
      invalidateApplicableDiscountToAPayment(applicableDiscount,applicableDiscount.payment.id)
    } 
  } 
  
  void invalidateApplicableDiscountToAPayment(ApplicableDiscount applicableDiscount,Long paymentId){
    Payment payment = Payment.get(paymentId)

    if(applicableDiscount.discount.percentage)
      payment.accumulatedDiscount -= (payment.paymentAmount * applicableDiscount.discount.percentage)  / 100
    else if(applicableDiscount.discount.amount)
      payment.accumulatedDiscount -= applicableDiscount.discount.amount
    
    payment.save()
  }

}
