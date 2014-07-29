package com.payable

class Concept {

  String description
  
  Organization organization
  
  Date dateCreated
  Date lastUpdated
  
  static constraints = {
    description blank:false, size:1..150
  }

  String toString(){
    description 
  }

}
