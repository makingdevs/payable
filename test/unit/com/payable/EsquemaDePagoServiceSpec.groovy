package com.payable

import static org.junit.Assert.*
import grails.test.mixin.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(EsquemaDePagoService)
@Mock([Descuento,EsquemaDePago])
class EsquemaDePagoServiceSpec extends Specification {

  @Unroll("Dados los descuentos: #descuentos la cantidad aplicable de descuento es #descuentoCalculado para una cantidad de #cantidadDePago")
  def "Dado un esquema de pago con descuentos en cantidades calcular la cantidad aplicable"() {
    given:
      crearEsquemaDePagoConDescuentos(cantidadDePago, descuentos)
    when:
      def cantidadDeDescuentoAplicable = service.obtenerCantidadDeDescuentoAplicable(1L)
    then:
      cantidadDeDescuentoAplicable == descuentoCalculado
      cantidadDePago - descuentoCalculado == cantidadAPagar
    where:
      cantidadDePago     << [
        1000, 
        1000, 
        8000,
        10000,
        10000
      ]
      descuentos         << [
        [
          [porcentaje:0,cantidad:200,diasPreviosParaCancelarDescuento:0]
        ],
        [
          [porcentaje:0,cantidad:200,diasPreviosParaCancelarDescuento:0],
          [porcentaje:0,cantidad:300,diasPreviosParaCancelarDescuento:0]
        ],
        [
          [porcentaje:0,cantidad:200,diasPreviosParaCancelarDescuento:0],
          [porcentaje:0,cantidad:100,diasPreviosParaCancelarDescuento:0],
          [porcentaje:0,cantidad:300,diasPreviosParaCancelarDescuento:0]
        ],[
          [porcentaje:10,cantidad:0,diasPreviosParaCancelarDescuento:0]
        ],[
          [porcentaje:10,cantidad:0,diasPreviosParaCancelarDescuento:0],
          [porcentaje:10,cantidad:0,diasPreviosParaCancelarDescuento:0]
        ]

      ]
      descuentoCalculado << [
        200, 
        500, 
        600,
        1000,
        2000
      ]
      cantidadAPagar     << [
        800, 
        500, 
        7400,
        9000,
        8000
      ]
  }

  private def crearEsquemaDePagoConDescuentos(cantidadDePago, descuentos){
    EsquemaDePago esquemaDePago = new EsquemaDePago(cantidadDePago:cantidadDePago)
    descuentos.eachWithIndex { d, i ->
      Descuento descuento = new Descuento(d)
      descuento.nombreDeDescuento = "Descuento $i"
      descuento.save()
      esquemaDePago.addToDescuentos(descuento)
    }
    esquemaDePago.save(validate:false)
    esquemaDePago
  }
}
