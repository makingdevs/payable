package com.payable

class DescuentoService {

  def buscarDescuentosDeUnaOrganizacion(Organizacion organizacion, def query) {

    Descuento.withCriteria {
      like ('nombreDeDescuento', "%${query}%")
      eq ('organizacion', organizacion)
    }
  }

}