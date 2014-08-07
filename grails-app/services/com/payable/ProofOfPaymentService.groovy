package com.payable

class ProofOfPaymentService {
  
  def s3AssetService

  def approvePayment(def transactionId,def paymentDate,def paymentType){
    def payment = Payment.findByTransactionId(transactionId)
    payment.paymentType = paymentType
    payment.paymentDate = paymentDate
    payment.paymentStatus = PaymentStatus.PAID
    def applicableDiscounts = payment.applicableDiscounts.findAll { 
      applicableDiscountStatus == ApplicableDiscountStatus.VALID 
    }

    applicableDiscounts.each{       
      applicableDiscountStatus = ApplicableDiscountStatus.APPLIED 
    }

    applicableDiscounts*.save()
    payment.save()
    payment
  }
  
  def rejectPayment(transactionId){
    def payment = Payment.findByTransactionId(transactionId)
    s3AssetService.delete(payment.proofOfPayment)
    payment.proofOfPayment = null
    payment.paymentStatus = PaymentStatus.REJECTED
    payment.save()
    payment
  } 
  
  def bytesOfProofOfPayment(pagoId){
    def pago = Pago.get(pagoId)
    def url = new URL(pago.comprobanteDePago.url())
    url.getBytes()
  }


}
