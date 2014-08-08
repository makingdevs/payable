package com.payable

class PaymentLinkTagLib {
 
  def paymentLinkService 

  def classesList = { attrs, body ->
    out << render(template:"/paymentLink/list",model:[paymentLinkService.getClassesUsingPayable()])
  }

}
