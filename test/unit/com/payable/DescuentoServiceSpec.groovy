package com.payable

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(DescuentoService)
@Mock([Organizacion, Descuento])
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
}