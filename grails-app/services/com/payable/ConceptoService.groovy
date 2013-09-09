package com.payable

class ConceptoService {

  static transactional = true

  def buscarConceptosDeUnaInstitucion(List<Organizacion> organizaciones, def query) {
    Concepto.withCriteria {
      like('descripcion', "%${query}%" )
      'in'('organizacion', organizaciones)
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