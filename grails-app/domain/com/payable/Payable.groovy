package com.payable

class Payable {

  static hasMany = [pagos : Pago]

  static constraints = { }

  static mapping = {
    tablePerHierarchy false
  }

}
