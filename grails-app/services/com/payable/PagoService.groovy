package com.payable

import java.lang.reflect.ParameterizedType

class PagoService {

  def esquemaDePagoService
  def recargoService

  def crearPago(Date fechaDeVencimiento, Long esquemaDePagoId){
    def esquemaDePago = EsquemaDePago.get(esquemaDePagoId)
    def pago = new Pago(
      fechaDeVencimiento:fechaDeVencimiento,
      cantidadDePago:esquemaDePago.cantidadDePago,
      conceptoDePago:esquemaDePago.concepto.descripcion,
      recargo:esquemaDePago.recargo)
    pago.save()
    pago
  }

  def obtenerPagoParaValidarComprobante(Long pagoId) {
    Pago.findById(pagoId,[fetch:['comprobanteDePago':'join']])
  }

  def estadoDeCuentaUsuario(def usuario) {
    def pagos = findAllPagosInUsuario(usuario)
    def (minimum, maximum) = getFirstAndLastDayOfMonth()
    [
      pagosVencidos    : pagos.findAll { pago -> pago.fechaDeVencimiento <= new Date() && pago.estatusDePago == EstatusDePago.VENCIDO }, // pagosVencidos
      pagosEnTiempo    : pagos.findAll { pago -> pago.fechaDeVencimiento >= new Date() && pago.fechaDeVencimiento <= maximum && pago.estatusDePago == EstatusDePago.CREADO }, // pagosDeUsuarioEnTiempoConDescuento
      pagosPorRealizar : pagos.findAll { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO  }, // pagosDeUsuarioEnTiempoSinDescuento
      pagoMensual      : pagos.findAll { pago -> pago.fechaDePago <= minimum && pago.estatusDePago == EstatusDePago.PAGADO }, // pagosConciliadosFavorablemente
      pagosRechazados  : pagos.findAll { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.RECHAZADO},
      pagosProcesados  : pagos.findAll { pago -> pago.estatusDePago == EstatusDePago.PROCESO},
      pagoCorrectos    : pagos.findAll { pago -> pago.estatusDePago == EstatusDePago.PAGADO && pago.fechaDePago <= maximum}
    ]
  }

  private def findAllPagosInUsuario(def usuario) {
    // def relationships = usuario.properties.findAll { k, v -> v instanceof Set }
    def pagos = []
    //relationships.each { k, v ->
    //  def field = usuario.class.getDeclaredField( k )
    //  ParameterizedType pt = (ParameterizedType) field.getGenericType()
    //  Class<?> payableListClass = (Class<?>) pt.getActualTypeArguments().first()
    //  if( payableListClass in Payable ) {
    //    pagos = v*.pagos.flatten()
    //  }
    //}
    pagos
  }

  private def getFirstAndLastDayOfMonth() {
    Calendar calendar = Calendar.getInstance()

    Calendar primerDiaDelMes = Calendar.getInstance()
    primerDiaDelMes.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE))

    Calendar ultimoDiaDelMes = Calendar.getInstance()
    ultimoDiaDelMes.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE))

    [primerDiaDelMes.time, ultimoDiaDelMes.time]
  }

}
