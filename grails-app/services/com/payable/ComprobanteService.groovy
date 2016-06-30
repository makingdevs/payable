package com.payable

import com.payable.TipoDePago

class ComprobanteService {

  def amazonService

  def agregarComprobanteAPago(Long pagoId, file) {
    Pago pago = Pago.get(pagoId)
    pago.comprobanteDePago = amazonService.uploadFile(file)
    pago.estatusDePago = EstatusDePago.PROCESO
    pago.save()
    pago
  }

  def aprobarPago(String transactionId, Date fechaDePago, def tipoPago) {
    def pago = Pago.findByTransactionId(transactionId)
    pago.tipoDePago = TipoDePago.getAt(tipoPago)
    pago.fechaDePago = fechaDePago
    pago.estatusDePago = EstatusDePago.PAGADO
    pago.descuentosAplicables.findAll { da ->
      da.descuentoAplicableStatus = DescuentoAplicableStatus.VIGENTE
    }*.descuentoAplicableStatus = DescuentoAplicableStatus.APLICADO
    pago.save()
    pago
  }

  def aprobarPago(String transactionId, Date fechaDePago, def tipoPago,String referencia){
    def pago = Pago.findByTransactionId(transactionId)
    pago.tipoDePago = TipoDePago.getAt(tipoPago)
    pago.fechaDePago = fechaDePago
    pago.referencia = referencia
    pago.estatusDePago = (pago.tipoDePago == TipoDePago.EFECTIVO ? EstatusDePago.PAGADO : EstatusDePago.PROCESO)
    pago.descuentosAplicables.findAll { da ->
      da.descuentoAplicableStatus = DescuentoAplicableStatus.VIGENTE
    }*.descuentoAplicableStatus = DescuentoAplicableStatus.APLICADO
    pago.save()
    pago
  }

  def aprobarPagoConciliacion(String transactionId, Date fechaDePago, def tipoPago, String referencia){
    def pago = Pago.findByTransactionId(transactionId)
    pago.tipoDePago = TipoDePago.getAt(tipoPago)
    pago.fechaDePago = fechaDePago
    pago.referencia = referencia
    pago.estatusDePago = EstatusDePago.PAGADO
    pago.descuentosAplicables.findAll { da ->
      da.descuentoAplicableStatus = DescuentoAplicableStatus.VIGENTE
    }*.descuentoAplicableStatus = DescuentoAplicableStatus.APLICADO
    pago.save()
    pago
  }

  def rechazarPago(String transactionId) {
    def pago = Pago.findByTransactionId(transactionId)
    if(pago.comprobanteDePago)
      amazonService.deleteFile(pago.comprobanteDePago.tokenize("/").last())
    pago.comprobanteDePago = null
    pago.estatusDePago = EstatusDePago.RECHAZADO
    pago.save()
    pago
  }

  def obtenerBytesDeComprobante(pagoId){
    def pago = Pago.get(pagoId)
    def url = new URL(pago.comprobanteDePago)
    url.getBytes()
  }

}
