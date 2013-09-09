package com.payable

class Telefono {

  String numeroTelefonico
  TipoDeTelefono tipoDeTelefono = TipoDeTelefono.CELULAR
  String extension

  String lada
  Boolean principal = false

  Date dateCreated
  Date lastUpdated

  static constraints = {
    lada nullable:true, blank: false
    numeroTelefonico size:1..10,blank:false
    extension size:1..6,nullable:true,blank:true
  }
}