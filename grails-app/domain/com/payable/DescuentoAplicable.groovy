package com.payable

class DescuentoAplicable {

  Date fechaDeVencimiento
  Descuento descuento

  DescuentoAplicableStatus status = DescuentoAplicableStatus.VIGENTE

  Date dateCreated
  Date lastUpdated

  static belongsTo = [pago:Pago]

  static constraints = {
  }
}
