package com.payable

class Concepto {

  String descripcion

  Organizacion organizacion

  Date dateCreated
  Date lastUpdated

  static constraints = {
    descripcion blank:false, size:1..150
  }

  String toString(){
    descripcion    
  }

}
