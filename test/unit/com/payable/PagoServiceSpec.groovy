package com.payable

import grails.test.mixin.*
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(PagoService)
@Mock([Organizacion,Pago,Concepto,Recargo,Descuento,EsquemaDePago,Payable])
class PagoServiceSpec extends Specification {

  @Unroll("Crear un pago con el concepto: '#conceptoDePago', vencimiento: '#fechaDeVencimiento' y la cantidad: '\$ #cantidadDePago'")
  def "Crear un pago con el concepto: '#conceptoDePago', vencimiento: '#fechaDeVencimiento' y la cantidad: '#cantidadDePago'"(){
    given:
      new EsquemaDePago(
        cantidadDePago:cantidadDePago,
        concepto:new Concepto(descripcion:conceptoDePago).save(validate:false),
        descuentos:generadorDeDescuentos(descuentos),
        recargo:new Recargo(cantidad:recargoAplicable).save(validate:false)
      ).save(validate:false)

      def esquemaDePagoServiceMock = mockFor(EsquemaDePagoService)
      esquemaDePagoServiceMock.demand.obtenerCantidadDeDescuentoAplicable(1..3) { Long esquemaDePagoId -> descuentoAplicable }
      service.esquemaDePagoService = esquemaDePagoServiceMock.createMock()

    when:
      def pago = service.crearPago(fechaDeVencimiento, 1)

    then:
      assert esquemaDePagoServiceMock.verify() == null
      pago.id > 0
      pago.transactionId
      pago.cantidadDePago == cantidadDePago
      pago.conceptoDePago == conceptoDePago
      pago.fechaDeVencimiento.date  == fechaDeVencimiento.date
      pago.fechaDeVencimiento.month  == fechaDeVencimiento.month
      pago.fechaDeVencimiento.year  == fechaDeVencimiento.year
      pago.tipoDePago == TipoDePago.TRANSFERENCIA_BANCARIA
      pago.estatusDePago == EstatusDePago.CREADO
      pago.recargosAcumulados == 0
      pago.descuentoAplicable == descuentoAplicable
      pago.descuentos.size() == descuentos
      pago?.recargo?.cantidad ?: 0 == recargoAplicable
    where:
      fechaDeVencimiento || cantidadDePago | conceptoDePago | descuentoAplicable | descuentos | recargoAplicable
      new Date() + 30    || 1234.45        | "Inscripción"  | 0                  | 0          | 0
      new Date() + 40    || 1345.98        | "Colegiatura"  | 0                  | 0          | 0
      new Date() + 30    || 1500.00        | "Inscripción"  | 300                | 1          | 0
      new Date() + 30    || 1750.50        | "Excursión"    | 600                | 2          | 0
      new Date() + 90    || 9999.99        | "Televisión"   | 1300               | 3          | 0
      new Date() + 30    || 1234.45        | "Inscripción"  | 0                  | 0          | 100
  }

  private def generadorDeDescuentos = { cantidad ->
    def descuentos = []
    cantidad.times { descuentos << new Descuento() }
    descuentos
  }

  def "Obteniendo estado de cuenta de un objeto sin relación directa con pagos"() {
    given :
      Pago pago = new Pago(
        fechaDePago : new Date(),
        fechaDeVencimiento : new Date() + 7,
        cantidadDePago : 100,
        conceptoDePago : "concepto")
      Dependiente dependiente = new Dependiente(pagos:[pago])
      Usuario usuario = new Usuario(dependientes:[dependiente])

    when : 
      def results = service.estadoDeCuentaUsuario(usuario)

    then :
      results
      !results.pagosVencidos
      !results.pagosEnTiempo
      results.pagosPorRealizar
      !results.pagoMensual
  }

  def "Obteniendo estado de cuenta de un objeto con relación directa de pagos"() {
    given :
      Pago pago = new Pago(
        fechaDePago : new Date(),
        fechaDeVencimiento : new Date() + 7,
        cantidadDePago : 100,
        conceptoDePago : "concepto")
      User user = new User(pagos:[pago])

    when : 
      def results = service.estadoDeCuentaUsuario(user)

    then :
      results
      !results.pagosVencidos
      !results.pagosEnTiempo
      results.pagosPorRealizar
      !results.pagoMensual
  }

  private class Dependiente extends Payable {  }

  private class Usuario {
    List<Dependiente> dependientes = []
  }

  private class User extends Payable { }

}