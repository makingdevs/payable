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
    (minimum, maximum) = getFirstAndLastDayOfMonth()
    [
      pagosVencidos   : obtenerPagosDeUsuario(usuario, { pago -> pago.fechaDeVencimiento <= new Date() && pago.estatusDePago == EstatusDePago.VENCIDO } ), // pagosVencidos
      pagosEnTiempo   : obtenerPagosDeUsuario(usuario, { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && pagos.descuentos } ), // pagosDeUsuarioEnTiempoConDescuento
      pagosPorRealizar: obtenerPagosDeUsuario(usuario, { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && !pagos.descuentos } ), // pagosDeUsuarioEnTiempoSinDescuento
      pagoMensual     : obtenerPagosDeUsuario(usuario, { pago -> pago.lastUpdated >= minimum && pago.lastUpdated <= maximum && pago.estatusDePago == EstatusDePago.PAGADO } ) // pagosConciliadosFavorablemente
    ]
  }

  private def obtenerPagosDeUsuario(def usuario, Closure closure) {
    def pagos = findPagosInUsuario(usuario)
    def pagosResult = pagos.findAll { pago ->
      closure.call( pago )
    }
    pagosResult
  }

  private def getFirstAndLastDayOfMonth() {
    Calendar calendar = Calendar.getInstance()
    [calendar.getActualMinimum(Calendar.DATE), calendar.getActualMaximum(Calendar.DATE)]
  }

  private def findPagosInUsuario(usuario) {
    []
  }

}
