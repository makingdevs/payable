package com.payable

enum ApplicableDiscountStatus{
  VALID("Valid"),
  APPLIED("Applied"),
  EXPIRED("Expired")
  
  final String value
  ApplicableDiscountStatus(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() }
}
