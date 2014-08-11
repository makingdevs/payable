package com.payable

class PagoJob {

  def descuentoAplicableService
  def recargoService

  static triggers = {
    cron name: 'myTrigger', cronExpression: "0 0 1 * * ?"
  }

  def execute() {
    log.debug "Comenzando a recalcular pagos con descuentos a las ${new Date()}"    
    descuentoAplicableService.expirarDescuentosRecalcularPagos()
    recargoService.exprirarPagosYCalcularRecargo()
    log.debug "Terminando de recalcular pagos con descuentos a las ${new Date()}"
  }
}
