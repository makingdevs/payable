package com.payable

class ConceptoService {

  static transactional = true

  def buscarConceptosDeUnaInstitucion(List<Organizacion> organizaciones, def query) {
    Concepto.withCriteria {
      like('descripcion', "%${query}%" )
      'in'('organizacion', organizaciones)
    }
  }

  def verificarConceptoPagoExistente(String descripcionDeConcepto) {
    Concepto.findByDescripcion(descripcionDeConcepto)
  }

  def guardarConceptoDePagoGenerado(Organizacion organizacion, String descripcionDeConcepto) {
    Concepto concepto = new Concepto()
    concepto.descripcion = descripcionDeConcepto
    concepto.organizacion = organizacion
    concepto.save()
    concepto
  }

}