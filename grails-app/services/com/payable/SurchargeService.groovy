package com.payable

class SurchargeService {

  def saveSurcharge(def params){
    Surcharge surcharge = Surcharge.findAllByOrganization(params.organization) ?: new Surcharge(params).save()
  }
}
