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


}
