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
  
  void exprirarPagosYCalcularRecargo() {    
    def pagos = Pago.withCriteria{
      lt('fechaDeVencimiento', new Date())
      inList('estatusDePago', [EstatusDePago.CREADO,EstatusDePago.RECHAZADO])
    }

    pagos.each{ pago ->
      if(pago.recargo)
        pago.recargosAcumulados = calcularRecargoAcumulado(pago.recargo, pago.cantidadDePago)
      pago.estatusDePago = EstatusDePago.VENCIDO
      pago.save()
    }    
  }

  def calcularRecargoAcumulado(Recargo recargo, def cantidadDePago) {
  	if (recargo.porcentaje)
  		cantidadDePago / 100 * recargo.porcentaje
  	else
  		recargo.cantidad

  }
}