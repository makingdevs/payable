package com.payable

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(DescuentoService)
@Mock([Organizacion, Descuento,Pago])
class DescuentoServiceSpec extends Specification {

    def "Obtener todos los descuentos ligados a una organizacion"() {
      given: "Un descuento asociado a la organizacion"
        def organizacion = new Organizacion()
        organizacion.nombre = "Escuela primaria de Springfild"
        organizacion.save()

        def descuento = new Descuento()
        descuento.nombreDeDescuento = "Pago anticipado"
        descuento.porcentaje = 10
        descuento.fechaDeVencimiento = new Date()
        descuento.organizacion = organizacion
        descuento.save()
      when: "Se realiza la llamada al metodo buscarDescuentosDeUnaOrganizacion"
        def query = "ant"
        def descuentoInstitucion = service.buscarDescuentosDeUnaOrganizacion(organizacion, query)
      then: "La cantidad de descuentos debe ser igual a 1"
        assert descuentoInstitucion.size() == 1
  }

  def "Obtener pagos con descuento expirado"(){
    given:
      def fechaDePrueba = new Date() - 7
      def descuentoVencido = new Descuento(diasPreviosParaCancelarDescuento:7).save(validate:false)
      def p1 = new Pago(fechaDeVencimiento:fechaDePrueba).save(validate:false)
      def p2 = new Pago(fechaDeVencimiento:fechaDePrueba).save(validate:false)
      def p3 = new Pago(fechaDeVencimiento:new Date()).save(validate:false)
      p1.addToDescuentos(descuentoVencido).save(validate:false)
      p2.addToDescuentos(descuentoVencido).save(validate:false)
    when:
      def pagos = service.buscarPagosConDescuentosVencidos()
    then:
      pagos*.id == [1,2]
      pagos.every { pago -> pago.estatusDePago == EstatusDePago.CREADO }
      pagos.every { pago -> 
        pago.fechaDeVencimiento.date == fechaDePrueba.date &&
        pago.fechaDeVencimiento.month == fechaDePrueba.month &&
        pago.fechaDeVencimiento.year == fechaDePrueba.year 
      }
      Descuento.list().size() == 1
      Pago.list().size == 3
  }
}