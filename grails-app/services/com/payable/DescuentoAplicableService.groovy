package com.payable

class DescuentoAplicableService {

  DescuentoAplicable generarParaPagoConVencimiento(Date fechaDeVencimiento, Long descuentoId){
    def descuento = Descuento.get(descuentoId)
    def descuentoAplicable =  new DescuentoAplicable()
    descuentoAplicable.fechaDeExpiracion = fechaDeVencimiento
    descuentoAplicable.descuento = descuento
    descuentoAplicable
  }
  
  def generarParaPagoConEsquemaDePagoConFechaReferencia(Long esquemaDePagoId, Date fechaReferencia, def fechasDeExpiracion){
    def descuentosAplicables = []
    def fechaDeExpiracion
    EsquemaDePago esquemaDePago = EsquemaDePago.get(esquemaDePagoId)
    esquemaDePago.descuentos.sort{ descuento -> descuento.id}.eachWithIndex{ descuento, i ->
      if(descuento.diasPreviosParaCancelarDescuento)
        fechaDeExpiracion = (fechaReferencia - descuento.diasPreviosParaCancelarDescuento)
      else
        fechaDeExpiracion = fechasDeExpiracion[i] 

      if(fechaDeExpiracion >= new Date())
        descuentosAplicables << generarParaPagoConVencimiento(fechaDeExpiracion,descuento.id)
    }
    descuentosAplicables
  }

  Pago agregarDescuentoAplicableAUnPago(DescuentoAplicable descuentoAplicable, Long pagoId){
    Pago pago = Pago.get(pagoId)
    if (descuentoAplicable.descuento.porcentaje)
      pago.descuentoAplicable += pago.cantidadDePago / 100 * descuentoAplicable.descuento.porcentaje
    else if ( descuentoAplicable.descuento.cantidad)
      pago.descuentoAplicable += descuentoAplicable.descuento.cantidad
    pago.addToDescuentosAplicables(descuentoAplicable)
    pago.save()
    pago
  }

  void expirarDescuentosRecalcularPagos(){
    def descuentosAplicables = DescuentoAplicable.withCriteria(){
      eq 'descuentoAplicableStatus',DescuentoAplicableStatus.VIGENTE
      le 'fechaDeExpiracion', new Date()
    }
    descuentosAplicables.each { dA ->
      dA.descuentoAplicableStatus = DescuentoAplicableStatus.EXPIRADO
      invalidarDescuentoAplicableAUnPago(dA,dA.pago.id)
    }
  }

  void invalidarDescuentoAplicableAUnPago(DescuentoAplicable descuentoAplicable, Long pagoId){
    Pago pago = Pago.get(pagoId)
    if (descuentoAplicable.descuento.porcentaje)
    pago.descuentoAplicable -= (pago.cantidadDePago / 100 * descuentoAplicable.descuento.porcentaje)
    else 
    pago.descuentoAplicable -= descuentoAplicable.descuento.cantidad
    pago.save()
  }
}
