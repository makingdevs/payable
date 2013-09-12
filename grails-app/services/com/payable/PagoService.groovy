package com.payable

import java.lang.reflect.ParameterizedType

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
    def pagos = findAllPagosInUsuario(usuario)
    def (minimum, maximum) = getFirstAndLastDayOfMonth()
    [
      pagosVencidos    : pagos.findAll { pago -> pago.fechaDeVencimiento <= new Date() && pago.estatusDePago == EstatusDePago.VENCIDO }, // pagosVencidos
      pagosEnTiempo    : pagos.findAll { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && pago.descuentos }, // pagosDeUsuarioEnTiempoConDescuento
      pagosPorRealizar : pagos.findAll { pago -> pago.fechaDeVencimiento >= new Date() && pago.estatusDePago == EstatusDePago.CREADO && !pago.descuentos }, // pagosDeUsuarioEnTiempoSinDescuento
      pagoMensual      : pagos.findAll { pago -> pago.lastUpdated >= minimum && pago.lastUpdated <= maximum && pago.estatusDePago == EstatusDePago.PAGADO } // pagosConciliadosFavorablemente
    ]
  }

  private def findAllPagosInUsuario(def usuario) {
    if(usuario instanceof Payable)
        return usuario.pagos
    
    def relationships = usuario.properties.findAll { k, v -> v instanceof List }
    def pagos = []
    
    relationships.each { k, v ->
        def field = usuario.class.getDeclaredField( k )
        ParameterizedType pt = (ParameterizedType) field.getGenericType()
        Class<?> payableListClass = (Class<?>) pt.getActualTypeArguments().first()
        if( payableListClass in Payable )
            pagos = v*.pagos.flatten()
    }
    
    pagos
  }

  private def getFirstAndLastDayOfMonth() {
    Calendar calendar = Calendar.getInstance()
    [calendar.getActualMinimum(Calendar.DATE), calendar.getActualMaximum(Calendar.DATE)]
  }

}
