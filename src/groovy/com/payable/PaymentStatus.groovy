package com.payable

enum PaymentStatus{
  CREATED("Created")
  EXPIRED("Expired")
  PROCESS("Payment in Process")
  PAID("Paid") 
  REJECTED("Rejected")
  CANCELED("Canceled")

  final String value
  PaymentStatus(String value){this.value = value}

  String toString(){ value }
  String getKey(){ name() }
}
