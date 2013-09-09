package com.payable

class Direccion {
  String calle
  String colonia
  String codigoPostal
  String delegacionMunicipio
  String numeroInterior
  String numeroExterior
  String estado
  String ciudad

  Date dateCreated
  Date lastUpdated

  static constraints = {
    calle size:1..100,blank:false
    delegacionMunicipio size:1..100,blank:false
    colonia size:1..100,blank:false
    codigoPostal size:6..6,blank:false
    ciudad nullable:true
    numeroExterior size:1..100,blank:false
  }
}