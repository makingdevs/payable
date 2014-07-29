package com.payable

class Surcharge {

  BigDecimal percentage
  BigDecimal amount

  Date dateCreated
  Date lastUpdated
  
  Organization organization

  static constraints = {
    percentage nullable: true, min:0.0
    amount nullable: true, min:0.0
  }

  String toString(){
    "${percentage} % - \$ ¢{amount}" 
  }
}
