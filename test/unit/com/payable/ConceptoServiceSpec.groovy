package com.payable

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(ConceptoService)
@Mock([Organizacion,Concepto])
class ConceptoServiceSpec extends Specification {

    def "Obtener un listado de los conceptos ligados a una Organizacion"() {
      given: "Se crearan 2 conceptos asociados a una Organizacion deacuerdo al Usuario"
        def organizacion = new Organizacion()
        organizacion.nombre = "Escuela primaria de Springfild"
        organizacion.save()

        def concepto1 = new Concepto()
        def concepto2 = new Concepto()
        concepto1.descripcion = "Colegiatura"
        concepto1.organizacion = organizacion
        concepto1.save(flush:true)

        concepto2.descripcion = "Primera colegiatura"
        concepto2.organizacion = organizacion
        concepto2.save(flush:true)

        def query = "giat"

      when: "Se realiza la llamada al metodo buscarConceptosDeUnaInstitucion"
        def conceptoInstitucion = service.buscarConceptosDeUnaInstitucion([organizacion], query)

      then: "la cantidad de conceptos debe de ser igual a 2"
        assert conceptoInstitucion.size() == 2
  }

  def "Al guardar un concepto de pago generado que no existe se debe de crear"(){
    given: "Se verificara que el concepto que se recibe no exista en la base de datos"
      def organizacion = new Organizacion()
      organizacion.nombre = "Escuela primaria de Springfild"
      organizacion.save()
      String descripcionConcepto = "concepto no existente"

    when: "se Realiza la llamada al servicio que verifica la existencia del concepto"
      def conceptoGuardado = service.buscarOSalvarConceptoDePago(organizacion, descripcionConcepto)

    then:
      assert conceptoGuardado.id == 1
      assert conceptoGuardado.descripcion =="concepto no existente"
  }

  def "No deberia guardar concepto de pago duplicado por institucion"(){
    given: "Se verificara que el concepto que se recibe no exista en la base de datos"
      String descripcionConcepto = "concepto no existente"

      def organizacion = new Organizacion()
      organizacion.nombre = "Escuela primaria de Springfild"
      organizacion.save()

      Concepto concepto = new Concepto()
      concepto.descripcion = descripcionConcepto
      concepto.organizacion = organizacion
      concepto.save()

    when: "se Realiza la llamada al servicio que verifica la existencia del concepto"
      def conceptoGuardado = service.buscarOSalvarConceptoDePago(organizacion, descripcionConcepto)

    then:
      assert conceptoGuardado.id == concepto.id
      assert conceptoGuardado.descripcion == concepto.descripcion
  }

}