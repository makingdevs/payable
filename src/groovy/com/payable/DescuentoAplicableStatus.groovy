package com.payable

enum DescuentoAplicableStatus {
  VIGENTE("Vigente"),
  EXPIRADO("Vencido")

  final String value
  DescuentoAplicableStatus(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() }
}