package com.payable

enum PaymentType {
  WIRE_TRANSFER("Wire Transfer")
  REFERENCED_DEPOSIT("Referenced Deposit")
  CHECK("Check")
  CASH("Cash")
  TERMINAL("Terminal")
  PAYPAL("PayPal")
  
  final String value
  PaymentType(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() } 
}
