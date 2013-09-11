package com.payable

class PagoService {

  def esquemaDePagoService

  def crearPago(Date fechaDeVencimiento, Long esquemaDePagoId){
    def esquemaDePago = EsquemaDePago.get(esquemaDePagoId)
    def descuentoAplicable = esquemaDePagoService.obtenerCantidadDeDescuentoAplicable(esquemaDePagoId)
    def pago = new Pago(
      fechaDeVencimiento:fechaDeVencimiento,
      cantidadDePago:esquemaDePago.cantidadDePago,
      conceptoDePago:esquemaDePago.concepto.descripcion,
      descuentoAplicable:descuentoAplicable,
      recargo:esquemaDePago.recargo)
    pago.descuentos = esquemaDePago.descuentos
    pago.save()
    pago
  }

  def obtenerPagoParaValidarComprobante(Long pagoId) {
    Pago.findById(pagoId,[fetch:['comprobanteDePago':'join']])
  }

  def estadoDeCuentaUsuario(def usuario) {
    [
      pagosVencidos:obtenerPagosVencidos(usuario),
      pagosEnTiempo:obtenerPagosDeUsuarioEnTiempoConDescuento(usuario,"EnTiempoConDescuento"),
      pagosPorRealizar:obtenerPagosDeUsuarioEnTiempoSinDescuento(usuario,"EnTiempoSinDescuento"),
      pagoMensual:obtenerPagosConciliadosFavorablemente(usuario)
    ]
  }

  private def obtenerPagosVencidos(def usuario) {
    def pagos = usuario.pagos
    def pagosVencidos = pagos.findAll { pago ->
      pago.fechaDeVencimiento <= new Date() && pago.estatusDePago == EstatusDePago.VENCIDO
    }
    pagosVencidos
  }

  private def obtenerPagosDeUsuarioEnTiempoConDescuento(def usuario) {
    def pagos = usuario.pagos
    def pagosEnTiempoConDescuento = pagos.findAll { pago ->
      pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && pagos.descuentos
    }
    pagosEnTiempoConDescuento
  }

  private def obtenerPagosDeUsuarioEnTiempoSinDescuento(def usuario) {
    def pagos = usuario.pagos
    def pagosEnTiempoSinDescuento = pagos.findAll { pago ->
      pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && !pagos.descuentos
    }
    pagosEnTiempoSinDescuento
  }

  private def obtenerPagosConciliadosFavorablemente(def usuario) {
    def pagos = usuario.pagos
    (minimum, maximum) = getFirstAndLastDayOfMonth()
    def pagosConciliadosFavorablemente = pagos.findAll { pago ->
      pago.lastUpdated >= minimum && pago.lastUpdated <= maximum && pago.estatusDePago == EstatusDePago.PAGADO

    }
    pagosConciliadosFavorablemente
  }

  private def getFirstAndLastDayOfMonth() {
    Calendar calendar = Calendar.getInstance()
    [calendar.getActualMinimum(Calendar.DATE), calendar.getActualMaximum(Calendar.DATE)]
  }

}
