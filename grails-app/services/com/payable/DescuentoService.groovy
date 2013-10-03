package com.payable

class DescuentoService {

  def buscarDescuentosDeUnaOrganizacion(Organizacion organizacion, def query) {
    Descuento.withCriteria {
      like ('nombreDeDescuento', "%${query}%")
      eq ('organizacion', organizacion)
    }
  }

  def buscarPagosConDescuentosVencidos(){
    def hoy = new Date()
    def pagos = Pago.withCriteria {
      eq 'estatusDePago', EstatusDePago.CREADO
      
    }
    println pagos*.descuentos.flatten()*.diasPreviosParaCancelarDescuento.findAll { it != null }
    pagos
  }

}