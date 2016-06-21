package com.payable

class PaymentGroupCommand {
  
  Long surchargeId
  BigDecimal paymentAmount
  String paymentConcept
  Date dueDate
  Integer daysPaymentDue

  
  Organization organization 
  def instances = []

  List<Long> discountIds 
  List<Date> expirationDatesForDiscounts

  def months = []
  def doublePayment = [] 

} 
