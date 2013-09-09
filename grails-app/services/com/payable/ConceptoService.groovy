package com.payable

class ConceptoService {

  static transactional = true

  def buscarConceptosDeUnaInstitucion(List<Organizacion> organizaciones, def query) {
    Concepto.withCriteria {
      like('descripcion', "%${query}%" )
      'in'('organizacion', organizaciones)
    }
  }

}