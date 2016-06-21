package com.payable

enum TipoDePago {
  TRANSFERENCIA_BANCARIA("Transferencia Bancaria"),
  FICHA_REFERENCIADA("Ficha Referenciada"),
  CHEQUE_FICHA("Cheque Ficha"),
  EFECTIVO("Efectivo"),
  TERMINAL("Terminal"),
  PAYPAL("PayPal")

  final String value
  TipoDePago(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() }  
}