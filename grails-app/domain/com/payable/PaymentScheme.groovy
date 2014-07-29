package com.payable

class PaymentScheme {

  BigDecimal paymentAmount
  Concept concept
  Surcharge surcharge
  
  static hasMany = [discounts : Discount]

  static constraints = {
    surcharge(nullable:true)
  }

}
