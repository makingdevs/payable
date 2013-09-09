package com.payable

class Organizacion {

  String nombre

  Date dateCreated
  Date lastUpdated

  static hasMany = [direcciones: Direccion, telefonos: Telefono]

  static constraints = {
    nombre size:1..100,blank:false
  }

}
