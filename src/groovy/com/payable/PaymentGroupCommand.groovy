package com.payable

class PaymentGroupCommand {
  
  Long surchargeId
  BigDecimal paymentAmount
  String paymentConcept
  Date dueDate
  Integer daysPaymentDue
  Organization organization 
  def instances = []
  def discountIds = []
  def months = []
  def doublePayment = [] 

} 
