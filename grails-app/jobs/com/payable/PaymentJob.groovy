package com.payable

class PaymentJob {

  def applicableDiscountService
  def surchargeService  

  static triggers = {
    cron name: 'myTrigger', cronExpression: "0 0 1 * * ?"  
  }

  def execute(){
    log.debug "Recalculating payments with discounts and surcharges at ${new Date()}" 
    applicableDiscountService.expireDiscountsAndRecalculatePayment() 
    surchargeService.expirePaymentsAndCalculateAccumulatedSurcharge()
    log.debug "Finishing at ${new Date()}"
  }
}
