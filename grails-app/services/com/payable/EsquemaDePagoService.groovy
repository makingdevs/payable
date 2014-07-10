package com.payable

class EsquemaDePagoService {

  def buscarOSalvarEsquemaDePago(GrupoPagoCommand gpc) {
    Concepto concepto = Concepto.findByDescripcion(gpc.conceptoDePago)
    def esquemaDePago = EsquemaDePago.findByConcepto(concepto) ?: new EsquemaDePago()

    if (!esquemaDePago.id) {
      esquemaDePago.cantidadDePago = gpc.cantidadDePago
      esquemaDePago.concepto = concepto
      esquemaDePago.recargo = Recargo.get(gpc.recargoId)
      if (gpc.descuentoIds) {  
        gpc.descuentoIds.each{ descuentoid ->
          def descuento = Descuento.findById(descuentoid)
          esquemaDePago.addToDescuentos(descuento)
        }
      }
    esquemaDePago.save(flush:true)
    }

    esquemaDePago
  }

}
