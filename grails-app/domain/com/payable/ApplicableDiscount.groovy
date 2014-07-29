package com.payable

class ApplicableDiscount {

  Date expirationDate 
  Discount discount

  ApplicableDiscountStatus applicableDiscountStatus = ApplicableDiscountStatus.VALID

  Date dateCreated
  Date lastUpdated

  static belongsTo = [payment:Payment]
  
  static constraints = {
    expirationDate nullable:false
  }
}
