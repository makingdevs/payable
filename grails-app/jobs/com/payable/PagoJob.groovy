package com.payable

class PagoJob {

  def descuentoAplicableService

  static triggers = {
    cron name: 'myTrigger', cronExpression: "0 0 12 * * ?"
  }

  def execute() {
    log.debug "Comenzando a recalcular pagos con descuentos a las ${new Date()}"
    descuentoAplicableService.expirarDescuentosRecalcularPagos()
    log.debug "Terminando de recalcular pagos con descuentos a las ${new Date()}"
  }
}
