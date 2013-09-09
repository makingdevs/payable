package com.payable

class Recargo {

  BigDecimal porcentaje
  BigDecimal cantidad

  Date dateCreated
  Date lastUpdated

  Organizacion organizacion

  static constraints = {
    porcentaje nullable: true, min:0.0
    cantidad nullable: true, min:0.0
  }

  String toString(){
    "${porcentaje} % - \$ ${cantidad}"
  }

}