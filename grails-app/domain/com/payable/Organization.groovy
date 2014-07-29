package com.payable

class Organization {

  String name

  Date dateCreated
  Date lastUpdated  
  
  static constraints = {
    name size:1..100,blank:false
  }

}
