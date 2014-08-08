package com.payable

class PaymentLinkService {

  def getClassesUsingPayable(){
    def classes = grailsApplication.getAllClasses().findAll{ clazz ->
      IPayable.isAssignableFrom(clazz.class) 
    } 
    classes 
  }

}
