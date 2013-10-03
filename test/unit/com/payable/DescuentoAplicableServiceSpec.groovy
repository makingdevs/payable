package com.payable

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore

@TestFor(DescuentoAplicableService)
@Mock([Descuento,EsquemaDePago,Pago,DescuentoAplicable])
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
      descuentoAplicable.descuentoAplicableStatus == DescuentoAplicableStatus.VIGENTE
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
      def descuentos = crearDescuentos(_diasParaCancelar)
      def esquemaDePago = new EsquemaDePago().save(validate:false)
      descuentos.each { d -> esquemaDePago.addToDescuentos(d)}
      esquemaDePago.save(validate:false)
      def fechaDeReferencia = Date.parse("dd/MM/yyyy",_fechaDeReferencia)
    when:
      def descuentosAplicables = service.generarParaPagoConEsquemaDePagoConFechaReferencia(1L, fechaDeReferencia)
    then:
      descuentosAplicables.size() ==  _fechasEsperadas.size()
      descuentosAplicables.every { da -> da.descuentoAplicableStatus == DescuentoAplicableStatus.VIGENTE }
      descuentosAplicables*.fechaDeExpiracion*.format("dd/MM/yyyy").sort() == _fechasEsperadas.sort()
      descuentosAplicables*.descuento*.diasPreviosParaCancelarDescuento.sort() == _diasParaCancelar.sort()
    where:
      _fechaDeReferencia | _diasParaCancelar || _fechasEsperadas
      "30/10/2013"       | [7]               || ["23/10/2013"]
      "30/10/2013"       | [7,14]            || ["23/10/2013","16/10/2013"]
      "25/10/2013"       | [7,14,21]         || ["04/10/2013","11/10/2013","18/10/2013"]
  }

	def "Agregar un descuento aplicado a un pago"() {
    given:
      def pago = new Pago(cantidadDePago:_cantidadDePago,descuentoAplicable:_descuentoAplicableActual).save(validate:false)
      def descuento = new Descuento(porcentaje:_porcentaje).save(validate:false)
      def descuentoAplicable = new DescuentoAplicable(descuento:descuento).save(validate:false,descuento:descuento)
    when:
      def noDescuentosAplicablesIniciales = pago?.descuentosAplicables?.size() ?: 0
      def pagoEsperado = service.agregarDescuentoAplicableAUnPago(descuentoAplicable,1L)
    then:
      pagoEsperado.descuentosAplicables.size() == noDescuentosAplicablesIniciales + 1
      pagoEsperado.cantidadDePago == _cantidadDePago
      pagoEsperado.descuentoAplicable == nuevoDescuentoAplicable
    where:
      _cantidadDePago | _descuentoAplicableActual | _porcentaje || nuevoDescuentoAplicable
      100             | 0                         | 10          || 10
      750             | 100                       | 15          || 212.5
      3250            | 325                       | 10          || 650
      3250            | 650                       | 10          || 975
	}

  private def crearDescuentos(def diasParaCancelar){
    diasParaCancelar.collect { d -> new Descuento(diasPreviosParaCancelarDescuento:d).save(validate:false) }
  }
  
  private def aleatorioDe(int n){
    new Random().nextInt(n)
  }
  
}
