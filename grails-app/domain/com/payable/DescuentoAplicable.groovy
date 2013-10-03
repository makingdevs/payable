package com.payable

class DescuentoAplicable {

  Date fechaDeExpiracion
  Descuento descuento

  DescuentoAplicableStatus descuentoAplicableStatus = DescuentoAplicableStatus.VIGENTE

  Date dateCreated
  Date lastUpdated

  static belongsTo = [pago:Pago]

  static constraints = {
    fechaDeExpiracion nullable:false
  }
}