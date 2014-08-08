package com.payable

class PaymentLinkTagLib {
 
  def paymentService 
  def classesList = { attrs, body ->
    out << render(template:"/paymentLink/list",model:[])
  }

}
