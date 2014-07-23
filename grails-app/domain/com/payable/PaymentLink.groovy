package com.payable

class PaymentLink {

  Long paymentRef
  String type

  static hasMany = [payments:Payment]

  static constraints = {
    paymentRef min:0L
    type blank:false
  }
}
