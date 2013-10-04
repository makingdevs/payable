package com.payable

enum DescuentoAplicableStatus {
  VIGENTE("Vigente"),
  EXPIRADO("Expirado")

  final String value
  DescuentoAplicableStatus(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() }
}