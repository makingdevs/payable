package com.payable

class DescuentoAplicableService {

  DescuentoAplicable generarParaPagoConVencimiento(Date fechaDeVencimiento, Long descuentoId){
    def descuento = Descuento.get(1L)
    def descuentoAplicable =  new DescuentoAplicable()
    descuentoAplicable.fechaDeVencimiento = fechaDeVencimiento
    descuentoAplicable.descuento = descuento
    descuentoAplicable
  }
}
