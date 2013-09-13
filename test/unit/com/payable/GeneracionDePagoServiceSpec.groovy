package com.payable

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(GeneracionDePagoService)
@Mock([Pago, Payable, Descuento, Concepto, Recargo, Organizacion])
class GeneracionDePagoServiceSpec extends Specification {

  def "Generacion de pago para una camada"() {
    setup:
      GrupoPagoCommand grupoPagoCommand = new GrupoPagoCommand(
          conceptoDePago : conceptoDePago,
          cantidadDePago : cantidadDePago,
          descuentoIds : [],
          pagoDoble : [],
          fechaDeVencimiento : new Date() + 7,
          organizacion : new Organizacion().save(validate:false),
          payables : [new Payable()]
        )

    and :
      def conceptoServiceMock = mockFor(ConceptoService)
      conceptoServiceMock.demand.buscarOSalvarConceptoDePago { organizacion, conceptoDePago ->
        new Concepto(organizacion:organizacion, conceptoDePago:conceptoDePago).save(validate:false)
      }
      service.conceptoService = conceptoServiceMock.createMock()

    when :
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then :
      assert pagos.size() == size
      assert pagos.first().id > 0
      assert !pagos.first().descuentos
      assert pagos.first().conceptoDePago == conceptoDePago
      assert pagos.first().cantidadDePago == cantidadDePago

    where :
      conceptoDePago    | cantidadDePago | fechaDeVencimiento || size
      "conceptoDePago"  | 100.00         | new Date() + 7     || 1
  }

}
