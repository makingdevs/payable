package com.payable

class Discount {
  
  String discountName
  BigDecimal percentage
  BigDecimal amount
  Integer previousDaysForCancelingDiscount = 0 

  Date dateCreated
  Date lastUpdated

  Organization organization

  static constraints = {
    discountName blank:false, size:1..150
    percentage nullable:true, min:0.0
    amount nullable:true, min:0.0
    previousDaysForCancelingDiscount min:0
  }

  String toString(){
    "${discountName} of ${percentage ?: 0} % - \$ ${amount ?: 0}" 
  }

}
