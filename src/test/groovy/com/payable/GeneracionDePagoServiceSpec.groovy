package com.payable

import grails.test.mixin.*
import spock.lang.Specification
import spock.lang.Ignore

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
          diasVencimientoPago : 4,
          fechaDeVencimiento : fechaDeVencimiento,
          organizacion : new Organizacion().save(validate:false),
          payables : [new Payable()]
        )

    and :
      def conceptoServiceMock = createConceptoServiceMock()
      service.conceptoService = conceptoServiceMock.createMock()

    when :
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then :
      assert pagos.size() == size
      assert pagos.first().id > 0
      assert !pagos.first().descuentosAplicables
      assert pagos.first().conceptoDePago == conceptoDePago
      assert pagos.first().cantidadDePago == cantidadDePago

    where :
      conceptoDePago    | cantidadDePago | fechaDeVencimiento || size
      "conceptoDePago"  | 100.00         | new Date() + 7     || 1
  }

  @Ignore
  def "Generar un pago con un descuento para un grupo"(){
    setup: "creando organizacion"
      def organizacion = new Organizacion()
      organizacion.save(validate:false)

      GrupoPagoCommand grupoPagoCommand = new GrupoPagoCommand(
          conceptoDePago : conceptoDePago,
          cantidadDePago : cantidadDePago,
          descuentoIds : descuentoIds,
          pagoDoble : [],
          meses : [],
          fechaDeVencimiento : fechaDeVencimiento,
          organizacion : organizacion,
          payables : [new Payable()]
        )

    and :
      Descuento descuento = new Descuento()
      descuento.nombreDeDescuento = "descuento 1"
      descuento.cantidad = 10
      descuento.organizacion = organizacion
      descuento.save(validate:false)

    and :
      def conceptoServiceMock = createConceptoServiceMock()
      service.conceptoService = conceptoServiceMock.createMock()

    when:
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then:
      assert conceptoServiceMock.verify() == null
      assert pagos.size() == 1
      assert pagos.first().id > 0
      assert pagos.first().descuentosAplicables
      assert pagos.first().descuentosAplicables.first().id > 0
      assert pagos.first().descuentosAplicables.first().cantidad == 10

    where :
      conceptoDePago | cantidadDePago | fechaDeVencimiento | descuentoIds
      "concepto"     | 100.00         | new Date() + 7     | [1L]
  }

  def "Generar un pago con un recargo para un grupo"(){
    setup: "creando organizacion"
      def organizacion = new Organizacion()
      organizacion.save(validate:false)

      GrupoPagoCommand grupoPagoCommand = new GrupoPagoCommand(
          conceptoDePago : conceptoDePago,
          cantidadDePago : cantidadDePago,
          descuentoIds : [],
          pagoDoble : [],
          recargoId : recargoId,
          meses : [],
          fechaDeVencimiento : fechaDeVencimiento,
          organizacion : organizacion,
          payables : [new Payable()]
        )

    and :
      Recargo recargo = new Recargo(
        cantidad : cantidadDeRecargo,
        organizacion : organizacion
      )
      recargo.save()

    and :
      def conceptoServiceMock = createConceptoServiceMock()
      service.conceptoService = conceptoServiceMock.createMock()

    when:
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then:
      assert conceptoServiceMock.verify() == null
      assert pagos.size() == 1
      assert pagos.first().id > 0
      assert !pagos.first().descuentosAplicables
      assert pagos.first().recargo
      assert pagos.first().recargo.cantidad == cantidadDeRecargo
      

    where :
      conceptoDePago | cantidadDePago | fechaDeVencimiento | recargoId | cantidadDeRecargo
      "concepto"     | 100.00         | new Date() + 7     | 1L        | 50.00
  }

  def "Generar un talonario de pagos para una camada"() {
    setup: "creando organizacion"
      def organizacion = new Organizacion()
      organizacion.save(validate:false)

      GrupoPagoCommand grupoPagoCommand = new GrupoPagoCommand(
          conceptoDePago : conceptoDePago,
          cantidadDePago : cantidadDePago,
          descuentoIds : [],
          pagoDoble : [],
          meses : meses,
          fechaDeVencimiento : fechaDeVencimiento,
          organizacion : organizacion,
          payables : [new Payable()]
        )

    and :
      def conceptoServiceMock = createConceptoServiceMock()
      service.conceptoService = conceptoServiceMock.createMock()

    when : 
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then :
      assert conceptoServiceMock.verify() == null
      assert pagos.size() == 4
      assert pagos.first().id > 0
      assert !pagos.first().descuentosAplicables
      assert !pagos.first().recargo
      assert pagos.first().conceptoDePago == conceptoDePago
      assert pagos.first().cantidadDePago == cantidadDePago 

    where : 
      conceptoDePago   | cantidadDePago | fechaDeVencimiento | meses
      "conceptoDePago" | 100.00         | new Date() + 7     | [1,3,5,11]
  }

  @Ignore
  def "Generar un talonario de pagos con pagos doble para una camada"() {
    setup: "creando organizacion"
      def organizacion = new Organizacion()
      organizacion.save(validate:false)

      GrupoPagoCommand grupoPagoCommand = new GrupoPagoCommand(
          conceptoDePago : conceptoDePago,
          cantidadDePago : cantidadDePago,
          descuentoIds : [],
          pagoDoble : pagoDoble,
          meses : meses,
          fechaDeVencimiento : fechaDeVencimiento,
          organizacion : organizacion,
          payables : [new Payable()]
        )

    and :
      def conceptoServiceMock = createConceptoServiceMock()
      service.conceptoService = conceptoServiceMock.createMock()

    when : 
      def pagos = service.generaPagoParaGrupo(grupoPagoCommand)

    then :
      assert pagos.size() == 6
      assert pagos.first().id == 1
      pagos.each { pago ->
        if( pagoDoble.contains( pago.fechaDeVencimiento.getMonth() ) )
          assert pago.cantidadDePago == 200.00
        else
          assert pago.cantidadDePago == 100.00
      }

    where : 
      conceptoDePago   | cantidadDePago | fechaDeVencimiento | meses       | pagoDoble
      "conceptoDePago" | 100.00         | new Date() + 7     | [1,3,5,7,9] | [1,5,9]
  }


  private createConceptoServiceMock() {
    def conceptoServiceMock = mockFor(ConceptoService)
    conceptoServiceMock.demand.buscarOSalvarConceptoDePago { organizacion, conceptoDePago ->
      new Concepto(organizacion:organizacion, conceptoDePago:conceptoDePago).save(validate:false)
    }
    conceptoServiceMock
  }

}
