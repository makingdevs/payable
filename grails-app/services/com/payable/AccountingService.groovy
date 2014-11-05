package com.payable

class AccountingService {
  
  def paymentService
  
  def createStatementForInstances(instances){
    def payments = paymentService.findAllPaymentsForTheInstances(instances)
    def (minimun,maximum) = getFirstAndLastDayOfMonth()
    def currentDate = new Date().clearTime()
      
    [paymentsExpired: payments.findAll{ payment -> payment.dueDate < currentDate && payment.paymentStatus == PaymentStatus.EXPIRED},
     paymentsOnTime: payments.findAll{ payment -> payment.dueDate >= currentDate && payment.dueDate <= maximum && payment.paymentStatus.CREATED},
     paymentsToBeMade: payments.findAll{ payment -> payment.dueDate >= currentDate && payment.paymentStatus == PaymentStatus.CREATED},
     monthlyPayments: payments.findAll{ payment -> payment.dueDate >= currentDate && payment.paymentStatus == PaymentStatus.PAID},
     rejectedPayments: payments.findAll{ payment -> payment.dueDate >= currentDate && payment.paymentStatus == PaymentStatus.REJECTED},
     paymentsInProcess: payments.findAll{ payment -> payment.paymentStatus == PaymentStatus.PROCESS },
     correctPayments: payments.findAll{ payment -> payment.paymentStatus == PaymentStatus.PAID && payment.dueDate <= maximum }]

  }

  private def getFirstAndLastDayOfMonth(){
    Calendar calendar = Calendar.getInstance()
    Calendar firstDayOfTheMonth = Calendar.getInstance()  
    firstDayOfTheMonth.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE))

    Calendar lastDayOfTheMonth = Calendar.getInstance()
    lastDayOfTheMonth.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE))

    [firstDayOfTheMonth.time, lastDayOfTheMonth.time]
  }

}
