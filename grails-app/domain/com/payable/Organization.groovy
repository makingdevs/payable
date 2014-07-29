package com.payable

import com.makingdevs.Direccion
import com.makingdevs.Telefono

class Organization {

  String name

  Date dateCreated
  Date lastUpdated  
  
  static hasMany = [addresses:Direccion, phones:Telefono]

  static constraints = {
    name size:1..100,blank:false
  }

}
