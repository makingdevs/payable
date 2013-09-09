package com.payable

enum TipoDeTelefono {
  CASA("Casa"),
  CELULAR("Celular"),
  TRABAJO("Trabajo"),
  RECADOS("Recados")

  final String value
  TipoDeTelefono(String value){ this.value = value }

  String toString(){ value }
  String getKey(){ name() }
}