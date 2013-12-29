package com.payable

class RecargoService {

  def buscarOSalvarRecargo(Organizacion organizacion, def params){
    Recargo recargo = Recargo.findAllByOrganizacion(organizacion)

    if (!recargo.id) {
      recargo = new Recargo(params)
      recargo.organizacion  = organizacion
      recargo.save()
    }
  }
  def calcularRecargoAcumulado(Recargo recargo, def cantidadDePago) {
  	if (recargo.porcentaje)
  		cantidadDePago / 100 * recargo.porcentaje
  	else
  		recargo.cantidad

  }
}