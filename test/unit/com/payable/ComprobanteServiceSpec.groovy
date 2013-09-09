package com.payable

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import org.grails.s3.S3Asset
import org.grails.s3.S3AssetService

@TestFor(ComprobanteService)
@Mock(Pago)
class ComprobanteServiceSpec extends Specification {

  def "Conciliar un pago con un comprobante correcto"() {
    given : "Tenemos un pago sin conciliar"
      def pagoExistente = new Pago(conceptoDePago : "",fechaDeVencimiento : new Date() + 10,cantidadDePago : 1000,transactionId : "1234567890",comprobanteDePago:new S3Asset())
      pagoExistente.save(validate:false)
    when : "Le decimos que lo apruebe el pago conciliado"
      def pagoAprobado = service.aprobarPago("1234567890", new Date() - 2, _tipoDePago)
    then : "Que el pago esté aprobado"
      pagoAprobado.estatusDePago == EstatusDePago.PAGADO
      pagoAprobado.fechaDePago
      pagoAprobado.tipoDePago == _tipoDePago
    where : 
      _tipoDePago << [TipoDePago.TRANSFERENCIA_BANCARIA,TipoDePago.FICHA_REFERENCIADA,TipoDePago.CHEQUE_FICHA,TipoDePago.EFECTIVO,TipoDePago.TERMINAL]
  }

  def "Conciliar un pago con un comprobante incorrecto indicando que el pago no procede"() {
    given : "Tenemos un pago sin conciliar"
      def pagoExistente = new Pago(conceptoDePago : "",fechaDeVencimiento : new Date() + 10,cantidadDePago : 1000,transactionId : "1234567890",comprobanteDePago:new S3Asset())
      pagoExistente.save(validate:false)
    and: "Simulamos el borrado del comprobante"
      def s3AssetServiceMock = mockFor(S3AssetService)
      s3AssetServiceMock.demand.delete(1..1) { S3Asset -> null }
      service.s3AssetService = s3AssetServiceMock.createMock()
    when : "Le decimos que no aprueba el pago"
      def pagoRechazado = service.rechazarPago("1234567890")
      s3AssetServiceMock.verify()
    then : "Vemos que el pago esta rechazado y se ha borrado el comprobante"
      pagoRechazado.estatusDePago == EstatusDePago.RECHAZADO
      !pagoRechazado.fechaDePago
      !pagoRechazado.comprobanteDePago
  }
}
