package com.payable

class Payment {

  Date paymentDate
  Date dueDate

  BigDecimal paymentAmount
  BigDecimal accumulatedSurcharges = 0
  BigDecimal accumulatedDiscount  = 0

  String reference
  String paymentConcept
  String transactionId = UUID.randomUUID().toString().replaceAll('-', '').substring(0,20)

  PaymentType paymentType = PaymentType.WIRE_TRANSFER
  PaymentStatus paymentStatus = PaymentStatus.CREATED

  String proofOfPayment

  Surcharge surcharge

  static hasMany = [applicableDiscounts : ApplicableDiscount]

  Date dateCreated
  Date lastUpdated

  static constraints = {
    paymentConcept size:1..100,blank:false
    paymentAmount min:1.0
    accumulatedSurcharges()
    accumulatedDiscount()
    paymentDate nullable:true
    transactionId size:20..20
    proofOfPayment nullable:true
    surcharge nullable:true
    reference nullable:true
  }

}
