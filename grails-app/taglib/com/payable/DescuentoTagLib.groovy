package com.payable

class DescuentoTagLib {

  static namespace = "descuento"

  def cantidadAplicable = { attrs, body ->
    def descuentoRealPorcentaje = calcularDescuentoReal( attrs.pago )
    out << '$ ' + g.formatNumber(number:"${descuentoRealPorcentaje}", format:"###,##0.00", locale:"es_MX")
  }

  def porcentajeTotal = { attrs, body ->
    def descuentoTotal = sumaPorcentajeDeDescuentosDelPago( attrs.pago )
    out << '(' + g.formatNumber(number:"${descuentoTotal}", format:"###,##0", locale:"es_MX") + '%)'
  }

  def totalConDescuento = { attrs, body ->
    def pago = attrs.pago
    def descuentoRealPorcentaje = calcularDescuentoReal( pago )
    def totalConDescuento = pago.cantidadDePago - descuentoRealPorcentaje
    out << '$ ' + g.formatNumber(number:"${totalConDescuento}", format:"###,##0.00", locale:"es_MX")
  }

  private def calcularDescuentoReal(Pago pago) {
    def descuentoTotal = sumaPorcentajeDeDescuentosDelPago( pago )    
    (descuentoTotal / 100) * pago.cantidadDePago
  }

  private def sumaPorcentajeDeDescuentosDelPago( Pago pago ) {
    pago.descuentosAplicables.findAll{ it.descuentoAplicableStatus == DescuentoAplicableStatus.VIGENTE}*.descuento.sum(0) { it.porcentaje }
  }

}
