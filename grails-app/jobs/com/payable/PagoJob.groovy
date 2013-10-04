package com.payable

class PagoJob {

  def descuentoAplicableService

  static triggers = {
    simple repeatInterval: 5000l
  }

  def execute() {
    descuentoAplicableService.expirarDescuentosRecalcularPagos()
  }
}
