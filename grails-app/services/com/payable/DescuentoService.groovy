package com.payable

class DescuentoService {

  def buscarDescuentosDeUnaOrganizacion(List<Organizacion> organizaciones, def query) {

    Descuento.withCriteria {
      like ('nombreDeDescuento', "%${query}%")
      'in' ('organizacion', organizaciones)
    }
  }

}