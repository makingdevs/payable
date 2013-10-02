package com.payable

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore

@TestFor(DescuentoAplicableService)
@Mock([Descuento,EsquemaDePago])
class DescuentoAplicableServiceSpec extends Specification {

  @Unroll("Generar un descuento con fecha de vencimiento #_fechaDeExpiracion y porcentaje del #_porcentaje % ")
  def "Generar un descuento aplicado con una fecha de vencimiento y un descuento"() {
    given:
      new Descuento(
        nombreDeDescuento:_nombreDeDescuento,
        porcentaje:_porcentaje,
        cantidad:0,
        diasPreviosParaCancelarDescuento:_diasPrevios).save(validate:false)
      def fechaDeExpiracion = _fechaDeExpiracion
    when:
      def descuentoAplicable = service.generarParaPagoConVencimiento(fechaDeExpiracion, 1L)
    then:
      descuentoAplicable.status == DescuentoAplicableStatus.VIGENTE
      descuentoAplicable.descuento.nombreDeDescuento == _nombreDeDescuento
      descuentoAplicable.descuento.porcentaje == _porcentaje
      descuentoAplicable.fechaDeExpiracion.month == fechaDeExpiracion.month
      descuentoAplicable.fechaDeExpiracion.year == fechaDeExpiracion.year
      descuentoAplicable.fechaDeExpiracion.day == fechaDeExpiracion.day
    where:
      _nombreDeDescuento  | _porcentaje     | _diasPrevios    | _fechaDeExpiracion
      "Descuento X"       | aleatorioDe(10) | aleatorioDe(30) | new Date() - aleatorioDe(21)
  }

  def "Generar descuentos aplicados de un esquema de pago"() {
    given:
      def descuento1 = new Descuento().save(validate:false)
      def descuento2 = new Descuento().save(validate:false)
      def esquemaDePago = new EsquemaDePago().save(validate:false)
      esquemaDePago.addToDescuentos(descuento1).addToDescuentos(descuento2)
      esquemaDePago.save(validate:false)
      def fechaDeReferencia = (new Date() + 30)
    when:
      def descuentosAplicables = service.generarParaPagoConEsquemaDePagoConFechaReferencia(1L, fechaDeReferencia)
    then:
      descuentosAplicables.size() ==  2
      descuentosAplicables.every { da -> da.status == DescuentoAplicableStatus.VIGENTE }
  }

	def "Agregar un descuento aplicado a un pago"() {

	}
  
  private def aleatorioDe(int n){
    new Random().nextInt(n)
  }
  
}
