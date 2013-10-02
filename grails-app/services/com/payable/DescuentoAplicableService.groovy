package com.payable

class DescuentoAplicableService {

  DescuentoAplicable generarParaPagoConVencimiento(Date fechaDeVencimiento, Long descuentoId){
    def descuento = Descuento.get(1L)
    def descuentoAplicable =  new DescuentoAplicable()
    descuentoAplicable.fechaDeExpiracion = fechaDeVencimiento
    descuentoAplicable.descuento = descuento
    descuentoAplicable
  }

  def generarParaPagoConEsquemaDePagoConFechaReferencia(Long esquemaDePagoId, Date fechaReferencia){
    def descuentosAplicables = []
    EsquemaDePago esquemaDePago = EsquemaDePago.get(esquemaDePagoId)
    esquemaDePago.descuentos.each{ d ->
      descuentosAplicables << new DescuentoAplicable(
        fechaDeExpiracion:(fechaReferencia - d.diasPreviosParaCancelarDescuento),
        descuento:d
      )
    }
    descuentosAplicables
  }
}
