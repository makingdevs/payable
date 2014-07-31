package com.payable

class ApplicableDiscountService {

  def generateForPaymentWithExpirationDate(def expirationDate,Long discountId){
    def discount = Discount.get(discountId)  
    def applicableDiscount = new ApplicableDiscount()
    applicableDiscount.expirationDate = expirationDate
    applicableDiscount.discount = discount
    applicableDiscount
  }

}
