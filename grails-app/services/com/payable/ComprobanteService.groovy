package com.payable

import org.grails.s3.S3Asset
import com.payable.TipoDePago

class ComprobanteService {

  def s3AssetService

  def agregarComprobanteAPago(Long pagoId, file) {
    S3Asset receipt = new S3Asset()
    receipt.options.addAsync = 'false'
    Pago pago = Pago.get(pagoId)
    def tmp = s3AssetService.getNewTmpLocalFile(file.contentType)
    file.transferTo(tmp)
    receipt.newFile(tmp)
    receipt.mimeType = file.contentType
    s3AssetService.put(receipt)
    pago.comprobanteDePago = receipt
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

  def rechazarPago(String transactionId) {
    def pago = Pago.findByTransactionId(transactionId)
    if(pago.comprobanteDePago)
      s3AssetService.delete(pago.comprobanteDePago)
    pago.comprobanteDePago = null
    pago.estatusDePago = EstatusDePago.RECHAZADO
    pago.save()
    pago
  }

  def obtenerBytesDeComprobante(pagoId){
    def pago = Pago.get(pagoId)
    def url = new URL(pago.comprobanteDePago.url())
    url.getBytes()
  }

}
