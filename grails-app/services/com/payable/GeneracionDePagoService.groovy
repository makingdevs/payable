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
      fechasDescuentos = obtenerFechasDeAplicacion(meses, descuento.fechaDeVencimiento)
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

    List<Pago> pagos = []
    dependientes.each { dependiente ->
      def pago = generarPagoParaDependienteConCommand(dependiente, camadaPagoCommand, descuentos)
      pago.each { p ->
        dependiente.addToPagos(p)
        pagos << p
      }
      dependiente.save()
    }
    pagos

  }

  private def obtenerFechasDeAplicacion(def meses, Date fechaDeVencimiento) {
    def fechasAplicacion = []

    Calendar cal = Calendar.getInstance()
    cal.setTime(fechaDeVencimiento)

    def year = cal.get(Calendar.YEAR)
    def month = cal.get(Calendar.MONTH)
    def day = cal.get(Calendar.DAY_OF_MONTH)

    meses*.toInteger().each { mes ->
      if (mes < month) {
        cal.set(year+1, mes, day)
        fechasAplicacion.add(cal.getTime())
      } else if (mes > month) {
        cal.set(year, mes, day)
        fechasAplicacion.add(cal.getTime())
      }
    }

    fechasAplicacion
  }

}
