package com.payable

class DescuentoAplicableService {

  DescuentoAplicable generarParaPagoConVencimiento(Date fechaDeVencimiento, Long descuentoId){
    def descuento = Descuento.get(descuentoId)
    def descuentoAplicable =  new DescuentoAplicable()
    descuentoAplicable.fechaDeExpiracion = fechaDeVencimiento
    descuentoAplicable.descuento = descuento
    descuentoAplicable
  }

  def generarParaPagoConEsquemaDePagoConFechaReferencia(Long esquemaDePagoId, Date fechaReferencia){
    def descuentosAplicables = []
    EsquemaDePago esquemaDePago = EsquemaDePago.get(esquemaDePagoId)
    esquemaDePago.descuentos.each{ d ->
      descuentosAplicables << generarParaPagoConVencimiento((fechaReferencia - d.diasPreviosParaCancelarDescuento),d.id)
    }
    descuentosAplicables
  }

  Pago agregarDescuentoAplicableAUnPago(DescuentoAplicable descuentoAplicable, Long pagoId){
    Pago pago = Pago.get(pagoId)
    pago.descuentoAplicable += pago.cantidadDePago / 100 * descuentoAplicable.descuento.porcentaje
    pago.addToDescuentosAplicables(descuentoAplicable)
    pago.save()
    pago
  }

  void expirarDescuentosRecalcularPagos(){
    def pagos = Pago.withCriteria(){
      descuentosAplicables{
        eq 'descuentoAplicableStatus',DescuentoAplicableStatus.VIGENTE
        le 'fechaDeExpiracion', new Date()
      }
    }
    println pagos
  }
}
