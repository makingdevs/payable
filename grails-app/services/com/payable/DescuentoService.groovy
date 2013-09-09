package com.payable

class DescuentoService {

  def buscarDescuentosDeUnaInstitucion(def organizaciones, def query) {

    Descuento.withCriteria {
      like ('nombreDeDescuento', "%${query}%")
      'in' ('organizacion', organizaciones)
    }
  }

}