package com.payable

class GenerationOfPaymentService {

  def conceptService

  def generatePaymentsForGroup(PaymentGroupCommand paymentGroupCommand){
    def concept = conceptService.savePaymentConcept(paymentGroupCommand.organization, paymentGroupCommand.paymentConcept)

    def payments = []

    paymentGroupCommand.instances.each{ instance ->
      if(!IPayable.class.isAssignableFrom(instance.class)){
        throw new Exception("IPayable is not assignable from ${instance.class}")
      }
      def paymentsForInstance = generatePaymentsForInstance(paymentGroupCommand) 
      def paymentLink = PaymentLink.findByPaymentRef(instance.id) ?: new PaymentLink(paymentRef:instance.id,type:instance.class.simpleName)

      paymentsForInstance.each{ paymentForInstance -> 
        paymentLink.addToPayments(paymentForInstance) 
        payments << paymentForInstance
      } 

      paymentLink.save()
    } 

    payments
  }
  
  private def generatePaymentsForInstance(PaymentGroupCommand paymentGroupCommand){
    def months = paymentGroupCommand.months
    def payments = []
    def surcharge = Surcharge.get(paymentGroupCommand.surchargeId)
    def dueDates = getDates(months,paymentGroupCommand.dueDate,paymentGroupCommand.daysPaymentDue)
    
    dueDates.each{ dueDate ->
      Payment payment = new Payment()
      payment.paymentConcept = paymentGroupCommand.paymentConcept
      payment.paymentAmount = paymentGroupCommand.paymentAmount 
      
      if(isDoublePaymentThisMonth(paymentGroupCommand.doublePayment,dueDate)){ 
        payment.paymentAmount *= 2
      }

      if(dueDate < new Date().clearTime())
        payment.paymentStatus = PaymentStatus.EXPIRED
        
      payment.dueDate = dueDate 
      payment.surcharge = surcharge
      payment.save()

      payments << payment
    }

    payments
  }

  private Boolean isDoublePaymentThisMonth(def doublePayment, def dueDate){
    Calendar cal = Calendar.getInstance()
    cal.setTime(dueDate)
    def month = cal.get(Calendar.MONTH)
    doublePayment.contains(month.toString()) 
  }

  private def getDates(def months,Date dueDate,Integer daysPaymentDue){
  
    def dates = []

    Calendar cal = Calendar.getInstance()
    cal.setTime(dueDate ?: new Date())
    def year = cal.get(Calendar.YEAR)
    def month = cal.get(Calendar.MONTH)
    def day = cal.get(Calendar.DAY_OF_MONTH)

    if(daysPaymentDue)
      day = daysPaymentDue

    if(!months){
      cal.set(year,month,day)
      dates.add(cal.getTime())
    }
    
    months.each{ m ->

      switch(m){
        case {m < month}:
          cal.set(year+1,month,day)
          dates.add(cal.getTime())
        break
        case {m > month}:
          cal.set(year,month,day)
          dates.add(cal.getTime())
        break
        case {m == month}:
          cal.set(year,month,day)
          dates.add(cal.getTime())
        break
      }
    }

    dates
  }
}
