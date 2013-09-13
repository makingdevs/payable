package com.payable

class GeneracionDePagoService {

  def conceptoService

  def serviceMethod( Organizacion organizacion,
                     String conceptoDePago,
                     Set<Payable> payables,
                     List<Long> descuentoIds,
                     def meses = []) {
    conceptoService.buscarOSalvarConceptoDePago(organizacion, conceptoDePago)

    def listaDeDescuentosParaAplicar = Descuento.getAll(descuentoIds)
    def descuentos = []

    listaDeDescuentosParaAplicar.each { descuento ->
      fechasDescuentos = obtenerFechas(meses, descuento.fechaDeVencimiento)
      fechasDescuentos.each { fecha ->
        Descuento desc = new Descuento()
        desc.nombreDeDescuento = descuento.nombreDeDescuento
        desc.porcentaje = descuento?.porcentaje
        desc.cantidad = descuento?.cantidad
        desc.fechaDeVencimiento = fecha
        desc.institucion = descuento.institucion
        desc.save()
        descuentos << desc
      }
    }

    def pagos = []
    payables.each { payable ->
      def payments = generarPagoParaDependienteConCommand(payable, command, descuentos)
      payments.each { payment ->
        payable.addToPagos(payment)
        pagos << payment
      }
      payable.save()
    }
    pagos

  }

  private def obtenerFechas(def meses, Date fechaDeVencimiento, Closure closure = null) {
    def fechas = []

    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)

    closure?.call( fechas, fechaDeVencimiento )

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

  private def generarPagosParaPayable(Payable payable, def command, List descuentos) {
    def recargo = Recargo.get(command.recargoId)
    generatePaymentBook(command, recargo, descuentos)
  }

  private def generatePaymentBook(command, recargo, descuentos) {
    def meses = command.meses
    def pagos = []
    def fechasDeVencimiento = obtenerFechas(meses, command.fechaDeVencimiento, { fechas, vencimiento -> fechas << vencimiento } )

    fechasDeVencimiento.each { fechaDeVencimiento ->
      Pago pago = new Pago()
      pago.conceptoDePago = command.conceptoDePago
      pago.cantidadDePago = command.cantidadDePago

      if (esPagoDobleEsteMes(command, fechaDeVencimiento)) 
        pago.cantidadDePago *= 2

      pago.fechaDeVencimiento = fechaDeVencimiento

      descuentos.each { descuento ->
        pago.addToDescuentos(descuento)
      }
  
      if (recargo)
        pago.addToRecargos(recargo)
  
      pago.save()
      pagos.add(pago)
    }

    pagos
  }

  private Boolean esPagoDobleEsteMes(def pagoDoble, def fechaDeVencimiento){
    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)
    def month = cal.get(Calendar.MONTH)

    pagoDoble.contains(month.toString())
  }

}