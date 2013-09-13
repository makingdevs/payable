package com.payable

class GeneracionDePagoService {

  def conceptoService

  def generaPagoParaGrupo( GrupoPagoCommand grupoPagoCommand ) {
    conceptoService.buscarOSalvarConceptoDePago(grupoPagoCommand.organizacion, grupoPagoCommand.conceptoDePago)

    def listaDeDescuentosParaAplicar = Descuento.getAll(grupoPagoCommand.descuentoIds)
    def descuentos = []

    listaDeDescuentosParaAplicar.each { descuentoParaAplicar ->
      def fechasDescuentos = obtenerFechas(grupoPagoCommand.meses, descuentoParaAplicar.fechaDeVencimiento)
      fechasDescuentos.each { fecha ->
        Descuento descuento = new Descuento()
        descuento.nombreDeDescuento = descuentoParaAplicar.nombreDeDescuento
        descuento.porcentaje = descuentoParaAplicar?.porcentaje
        descuento.cantidad = descuentoParaAplicar?.cantidad
        descuento.fechaDeVencimiento = fecha
        descuento.organizacion = descuentoParaAplicar.organizacion
        descuento.save()
        descuentos << descuento
      }
    }

    def pagos = []
    grupoPagoCommand.payables.each { payable ->
      def payments = generarPagosParaPayable(payable, grupoPagoCommand, descuentos)
      payments.each { payment ->
        payable.addToPagos(payment)
        pagos << payment
      }
      payable.save()
    }
    pagos

  }

  private def obtenerFechas(def meses, Date fechaDeVencimiento) {
    def fechas = []

    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)
    fechas.add(fechaDeVencimiento)

    def year = cal.get(Calendar.YEAR)
    def month = cal.get(Calendar.MONTH)
    def day = cal.get(Calendar.DAY_OF_MONTH)

    meses*.toInteger().each { mes ->
      if (mes < month) {
        cal.set(year+1, mes, day)
        fechas.add(cal.getTime())
      } else if (mes > month) {
        cal.set(year, mes, day)
        fechas.add(cal.getTime())
      }
    }

    fechas
  }

  private def generarPagosParaPayable(Payable payable, GrupoPagoCommand grupoPagoCommand, List descuentos) {
    def recargo = Recargo.get(grupoPagoCommand.recargoId)
    generatePaymentBook(grupoPagoCommand, recargo, descuentos)
  }

  private def generatePaymentBook(GrupoPagoCommand grupoPagoCommand, recargo, descuentos) {
    def meses = grupoPagoCommand.meses
    def pagos = []
    def fechasDeVencimiento = obtenerFechas(meses, grupoPagoCommand.fechaDeVencimiento)

    fechasDeVencimiento.each { fechaDeVencimiento ->
      Pago pago = new Pago()
      pago.conceptoDePago = grupoPagoCommand.conceptoDePago
      pago.cantidadDePago = grupoPagoCommand.cantidadDePago

      if (esPagoDobleEsteMes(grupoPagoCommand.pagoDoble, fechaDeVencimiento))
        pago.cantidadDePago *= 2

      pago.fechaDeVencimiento = fechaDeVencimiento

      descuentos.each { descuento ->
        pago.addToDescuentos(descuento)
      }
      pago.recargo = recargo
      pago.save()

      pagos.add(pago)
    }

    pagos
  }

  private Boolean esPagoDobleEsteMes(def pagoDoble, def fechaDeVencimiento){
    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)
    def month = cal.get(Calendar.MONTH)

    pagoDoble.contains(month)
  }

}

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