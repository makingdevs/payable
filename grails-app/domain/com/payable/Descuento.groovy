package com.payable

class Descuento {

  String nombreDeDescuento
  BigDecimal porcentaje
  BigDecimal cantidad
  Integer diasPreviosParaCancelarDescuento = 0

  Date dateCreated
  Date lastUpdated

  Organizacion organizacion

  static constraints = {
    nombreDeDescuento blank:false, size:1..150
    porcentaje nullable: true, min:0.0
    cantidad nullable: true, min:0.0
    diasPreviosParaCancelarDescuento min:1
  }

  String toString(){
    "${nombreDeDescuento} por ${porcentaje ?: 0} % - \$ ${cantidad ?: 0}"
  }

}
