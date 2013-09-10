package com.payable

class ConceptoService {

  static transactional = true

  def buscarConceptosDeUnaInstitucion(Organizacion organizacion, def query) {
    Concepto.withCriteria {
      like('descripcion', "%${query}%" )
      eq ('organizacion', organizacion)
    }
  }

  def buscarOSalvarConceptoDePago(Organizacion organizacion, String descripcionDeConcepto) {
    Concepto concepto = Concepto.findByDescripcion(descripcionDeConcepto) ?: new Concepto()

    if(!concepto.id) {
      concepto.descripcion = descripcionDeConcepto
      concepto.organizacion = organizacion
      concepto.save()
    }

    concepto
  }

}