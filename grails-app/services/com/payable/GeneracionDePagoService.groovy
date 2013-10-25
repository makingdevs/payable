package com.payable

class GeneracionDePagoService {

  def conceptoService

  def generaPagoParaGrupo( GrupoPagoCommand grupoPagoCommand ) {
    def concepto = conceptoService.buscarOSalvarConceptoDePago(grupoPagoCommand.organizacion, grupoPagoCommand.conceptoDePago)

    def pagos = []
    grupoPagoCommand.payables.each { payable ->
      def payments = generarPagosParaPayable(payable, grupoPagoCommand)
      payments.each { payment ->
        payable.addToPagos(payment)
        pagos << payment
      }
      payable.save()
    }
    pagos

  }

  private def obtenerFechas(def meses, Date fechaDeVencimiento, Integer diasVencimientoPago) {
    def fechas = []

    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)
    def year = cal.get(Calendar.YEAR)
    def month = cal.get(Calendar.MONTH)
    def day = cal.get(Calendar.DAY_OF_MONTH)

    if (diasVencimientoPago)
      day = diasVencimientoPago

    cal.set(year, month, day)
    fechas.add(cal.getTime())

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

  private def generarPagosParaPayable(Payable payable, GrupoPagoCommand grupoPagoCommand) {
    def recargo = Recargo.findById(grupoPagoCommand.recargoId)
    generatePaymentBook(grupoPagoCommand, recargo)
  }

  private def generatePaymentBook(GrupoPagoCommand grupoPagoCommand, recargo) {
    def meses = grupoPagoCommand.meses
    def pagos = []
    def fechasDeVencimiento = obtenerFechas(meses, grupoPagoCommand.fechaDeVencimiento, grupoPagoCommand.diasVencimientoPago)

    fechasDeVencimiento.each { fechaDeVencimiento ->
      Pago pago = new Pago()
      pago.conceptoDePago = grupoPagoCommand.conceptoDePago
      pago.cantidadDePago = grupoPagoCommand.cantidadDePago

      if (esPagoDobleEsteMes(grupoPagoCommand.pagoDoble, fechaDeVencimiento))
        pago.cantidadDePago *= 2

      pago.fechaDeVencimiento = fechaDeVencimiento

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