package com.payable

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.Shared

@TestFor(DescuentoAplicableService)
@Mock([Descuento,EsquemaDePago,Pago,DescuentoAplicable])
class DescuentoAplicableServiceSpec extends Specification {

  @Shared creaFecha = { n -> (new Date() + n).format("dd/MM/yyyy") }

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

  @Unroll("Con la fecha de referencia #_fechaDeReferencia y los días antes de expirar #_diasParaCancelar, se aplican #_descuentosAplicados con expiración #_fechasEsperadas")
  def "Generar descuentos aplicados de un esquema de pago"() {
    given:
      def descuentos = crearDescuentos(_diasParaCancelar)
      def esquemaDePago = new EsquemaDePago().save(validate:false)
      descuentos.each { d -> esquemaDePago.addToDescuentos(d)}
      esquemaDePago.save(validate:false)
      def fechaDeReferencia = Date.parse("dd/MM/yyyy",_fechaDeReferencia)
    when:
      def descuentosAplicables = service.generarParaPagoConEsquemaDePagoConFechaReferencia(1L, fechaDeReferencia,[])
    then:
      descuentosAplicables.size() ==  _descuentosAplicados
      descuentosAplicables.every { da -> da.descuentoAplicableStatus == DescuentoAplicableStatus.VIGENTE }
      descuentosAplicables*.fechaDeExpiracion*.format("dd/MM/yyyy").sort() == _fechasEsperadas.sort()
      descuentosAplicables*.descuento*.diasPreviosParaCancelarDescuento.sort() == _diasAplicados.sort()
    where:
      _fechaDeReferencia | _diasParaCancelar || _diasAplicados | _fechasEsperadas                           | _descuentosAplicados
      creaFecha(30)      | [7]               || [7]            | [creaFecha(23)]                            | 1
      creaFecha(30)      | [7,14]            || [7,14]         | [creaFecha(23),creaFecha(16)]              | 2
      creaFecha(30)      | [7,14,21]         || [7,14,21]      | [creaFecha(23),creaFecha(16),creaFecha(9)] | 3
      creaFecha(1)       | [7]               || []             | []                                         | 0
      creaFecha(10)      | [7,14]            || [7]            | [creaFecha(3)]                             | 1
      creaFecha(15)      | [7,14,21]         || [7,14]         | [creaFecha(8),creaFecha(1)]                | 2
  }

	def "Agregar un descuento aplicado a un pago"() {
    given:
      def pago = new Pago(cantidadDePago:_cantidadDePago,descuentoAplicable:_descuentoAplicableActual).save(validate:false)
      def descuento = new Descuento(porcentaje:_porcentaje, cantidad:_cantidad).save(validate:false)
      def descuentoAplicable = new DescuentoAplicable(descuento:descuento).save(validate:false,descuento:descuento)
    when:
      def noDescuentosAplicablesIniciales = pago?.descuentosAplicables?.size() ?: 0
      def pagoEsperado = service.agregarDescuentoAplicableAUnPago(descuentoAplicable,1L)
    then:
      pagoEsperado.descuentosAplicables.size() == noDescuentosAplicablesIniciales + 1
      pagoEsperado.cantidadDePago == _cantidadDePago
      pagoEsperado.descuentoAplicable == nuevoDescuentoAplicable
    where:
      _cantidadDePago | _descuentoAplicableActual | _porcentaje | _cantidad || nuevoDescuentoAplicable
      100             | 0                         | 10          |   ""      || 10
      750             | 100                       | 15          |   ""      || 212.5
      3250            | 325                       | 10          |   ""      || 650
      3250            | 650                       | 10          |   ""      || 975
      3250            | 650                       | ""          |   500     || 1150
	}

  def "Invalidar un descuento aplicado a un pago en el caso de tener un solo descuento"(){
    given: "Teniendo un descuento a un pago"
      def descuento = new Descuento(porcentaje:_porcentaje, cantidad:_cantidad).save(validate:false)
      def descuentoAplicable = new DescuentoAplicable(descuento:descuento).save(validate:false)
      def pago = new Pago(cantidadDePago:_cantidadDePago,descuentoAplicable:_descuentoAplicableActual)
      .addToDescuentosAplicables(descuentoAplicable)
      .save(validate:false)
    when: "Se llama el servicio para quitar ese descuento a descuento Aplicable"
      service.invalidarDescuentoAplicableAUnPago(descuentoAplicable,1L)
    then:
      pago.descuentosAplicables.size() == 1
      pago.cantidadDePago == _cantidadDePago
      pago.descuentoAplicable == nuevoDescuentoAplicable
    where:
    _cantidadDePago | _descuentoAplicableActual | _porcentaje | _cantidad || nuevoDescuentoAplicable
    100             | 10                        | 10          |   ""      || 0
    750             | 112.5                     | 15          |   null    || 0
    100             | 10                        | null        |   10      || 0
  }

  def "Invalidar un descuento aplicado a un pago en el caso de tener mas de un descuento"(){
    given: "Teniendo dos descuentos a un pago"
      def descuento1 = new Descuento(porcentaje:_porcentaje1, cantidad:_cantidad1).save(validate:false)
      def descuento2 = new Descuento(porcentaje:_porcentaje2, cantidad:_cantidad2).save(validate:false)
      def descuentoAplicable1 = new DescuentoAplicable(descuento:descuento1).save(validate:false)
      def descuentoAplicable2 = new DescuentoAplicable(descuento:descuento2).save(validate:false)
      def pago = new Pago(cantidadDePago:_cantidadDePago,descuentoAplicable:_descuentoAplicableActual)
      .addToDescuentosAplicables(descuentoAplicable1)
      .addToDescuentosAplicables(descuentoAplicable2)
      .save(validate:false)
    when: "Se llama al servicio para invalidar el descuento2 a el pago"
      service.invalidarDescuentoAplicableAUnPago(descuentoAplicable2,1L)
    then:
      pago.descuentosAplicables.size() == 2
      pago.cantidadDePago == _cantidadDePago
      pago.descuentoAplicable == nuevoDescuentoAplicable
    where:
    _cantidadDePago | _descuentoAplicableActual | _porcentaje1 | _cantidad1 | _porcentaje2 | _cantidad2 || nuevoDescuentoAplicable
    100             | 25                        |  10          |   null     |  15          |   ""       || 10
    100             | 25                        |  10          |   ""       |  null        |   15       || 10
    750             | 125                       |  10          |   ""       |  ""          |   50       || 75
    750             | 150                       |  10          |   ""       |  10          |   ""       || 75
  }


  private def crearDescuentos(def diasParaCancelar){
    diasParaCancelar.collect { d -> new Descuento(diasPreviosParaCancelarDescuento:d).save(validate:false) }
  }
  
  private def aleatorioDe(int n){
    new Random().nextInt(n)
  }
  
}
