package com.payable

class GrupoPagoCommand {

  Long recargoId
  BigDecimal cantidadDePago
  String conceptoDePago
  Date fechaDeVencimiento

  List<Long> descuentoIds

  Organizacion organizacion
  Set<Payable> payables

  def meses = []
  def pagoDoble = []

}