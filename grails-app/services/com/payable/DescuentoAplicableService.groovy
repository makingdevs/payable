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
}
