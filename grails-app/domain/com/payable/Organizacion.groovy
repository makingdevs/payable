package com.payable

import com.makingdevs.Direccion
import com.makingdevs.Telefono

class Organizacion {

  String nombre

  Date dateCreated
  Date lastUpdated

  static hasMany = [direcciones: Direccion, telefonos: Telefono]

  static constraints = {
    nombre size:1..100,blank:false
  }

}
