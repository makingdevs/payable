package com.payable

class GrupoPagoCommand {

  Long recargoId
  BigDecimal cantidadDePago
  String conceptoDePago
  Date fechaDeVencimiento
  Integer diasVencimientoPago

  List<Long> descuentoIds

  Organizacion organizacion
  Set<Payable> payables

  def meses = []
  def pagoDoble = []


}