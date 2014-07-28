package com.payable

class Payment {
 
  Date paymentDate 
  Date dueDate

  BigDecimal paymentAmount
  BigDecimal accumulatedSurcharges = 0
  BigDecimal applicableDiscount = 0
  
  String reference
  String paymentConcept
  String transactionId = UUID.randomUUID().toString().replaceAll('-', '').substring(0,20)

  Date dateCreated
  Date lastUpdated  

  static constraints = {
    paymentConcept size:1..100,blank:false
    paymentAmount min:1.0
    accumulatedSurcharges()
    applicableDiscount() 
    paymentDate nullable:true
    transactionId size:20..20
    reference nullable:true
  }

}
