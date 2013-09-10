package com.payable

class EsquemaDePagoService {

  def obtenerCantidadDeDescuentoAplicable(Long esquemaDePagoServiceId) {
    def esquemaDePago = EsquemaDePago.get(esquemaDePagoServiceId)
    esquemaDePago.descuentos.collect { d ->
      d.cantidad ?: esquemaDePago.cantidadDePago * d.porcentaje/100
    }.sum()
  }
}