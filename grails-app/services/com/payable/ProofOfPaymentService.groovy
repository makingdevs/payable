package com.payable

class ProofOfPaymentService {
  def grailsApplication

  def addProofOfPayment(Long paymentId, file){
    Payment payment = Payment.get(paymentId)
    payment.proofOfPayment = amazonService.uploadFile(file)
    payment.paymentStatus = PaymentStatus.PROCESS
    payment.save()
    payment
  }

  def approvePayment(def transactionId,def paymentDate,def paymentType, String reference = ""){
    def payment = Payment.findByTransactionId(transactionId)
    payment.paymentType = paymentType
    payment.paymentDate = paymentDate
    payment.reference = reference;
    if(payment.paymentType == PaymentType.CASH || payment.paymentStatus == PaymentStatus.PROCESS)
      payment.paymentStatus = PaymentStatus.PAID
    else
      payment.paymentStatus = PaymentStatus.PROCESS

    def applicableDiscounts = payment.applicableDiscounts.findAll {
      it.applicableDiscountStatus == ApplicableDiscountStatus.VALID
    }

    applicableDiscounts.each{ applicableDiscount ->
      applicableDiscount.applicableDiscountStatus = ApplicableDiscountStatus.APPLIED
    }

    applicableDiscounts*.save()
    payment.save()
    payment
  }

  def rejectPayment(transactionId){
    def payment = Payment.findByTransactionId(transactionId)
    if(payment.proofOfPayment)
      amazonService.deleteFile(payment.proofOfPayment.tokenize("/").last())

    payment.proofOfPayment = null
    payment.paymentStatus = PaymentStatus.REJECTED
    payment.save()
    payment
  }

  def bytesOfProofOfPayment(paymentId){
    def payment = Payment.get(paymentId)
    def url = new URL(payment.proofOfPayment)
    url.getBytes()
  }


}
