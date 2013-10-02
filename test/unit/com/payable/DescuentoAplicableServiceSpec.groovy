package com.payable

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Ignore

@TestFor(DescuentoAplicableService)
@Mock([Descuento])
class DescuentoAplicableServiceSpec extends Specification {

  def "Generar un descuento aplicado con una fecha de vencimiento y un descuento"() {
    given:
      new Descuento(nombreDeDescuento:"Descuento 1",porcentaje:10,cantidad:0,diasPreviosParaCancelarDescuento:7).save(validate:false)
      new Descuento(nombreDeDescuento:"Descuento 2",porcentaje:5,cantidad:0,diasPreviosParaCancelarDescuento:14).save(validate:false)
      new Descuento(nombreDeDescuento:"Descuento 3",porcentaje:0,cantidad:100,diasPreviosParaCancelarDescuento:21).save(validate:false)
      def fechaDeVencimiento = new Date() + 7
    when:
      def descuentoAplicable = service.generarParaPagoConVencimiento(fechaDeVencimiento, 1L)
    then:
      descuentoAplicable.status == DescuentoAplicableStatus.VIGENTE
      descuentoAplicable.descuento.nombreDeDescuento == "Descuento 1"
      descuentoAplicable.descuento.porcentaje == 10
      descuentoAplicable.fechaDeVencimiento.month == fechaDeVencimiento.month
      descuentoAplicable.fechaDeVencimiento.year == fechaDeVencimiento.year
      descuentoAplicable.fechaDeVencimiento.day == fechaDeVencimiento.day
  }

  def "Generar descuentos aplicados de un esquema de pago"() {

  }

	def "Agregar un descuento aplicado a un pago"() {

	}
  
  
}
