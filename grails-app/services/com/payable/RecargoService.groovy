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

}