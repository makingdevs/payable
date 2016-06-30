package com.payable

class Pago {

  Date fechaDePago
  Date fechaDeVencimiento

  BigDecimal cantidadDePago
  BigDecimal recargosAcumulados = 0
  BigDecimal descuentoAplicable = 0

  String referencia
  String conceptoDePago
  String transactionId = UUID.randomUUID().toString().replaceAll('-', '').substring(0,20)

  TipoDePago tipoDePago = TipoDePago.TRANSFERENCIA_BANCARIA
  EstatusDePago estatusDePago = EstatusDePago.CREADO

  String comprobanteDePago

  Recargo recargo

  static hasMany = [descuentosAplicables : DescuentoAplicable]

  Date dateCreated
  Date lastUpdated

  static constraints = {
    conceptoDePago size:1..100,blank:false
    cantidadDePago min:1.0
    recargosAcumulados()
    descuentoAplicable()
    fechaDePago nullable: true
    transactionId size:20..20
    comprobanteDePago nullable:true
    recargo nullable:true
    referencia nullable:true
  }

}
