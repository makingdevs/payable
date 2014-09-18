package com.payable

import org.grails.s3.S3Asset

class ProofOfPaymentService {
  
  def s3AssetService

  def addProofOfPayment(Long paymentId, file){
    S3Asset proofOfPayment = new S3Asset()
    proofOfPayment.options.addAsync = 'false'
    Payment payment = Payment.get(paymentId)
    def tmp = s3AssetService.getNewTmpLocalFile(file.contentType)
    file.transferTo(tmp)
    proofOfPayment.newFile(tmp)
    proofOfPayment.mimeType = file.contentType
    s3AssetService.put(proofOfPayment)
    payment.proofOfPayment = proofOfPayment
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
      s3AssetService.delete(payment.proofOfPayment)

    payment.proofOfPayment = null
    payment.paymentStatus = PaymentStatus.REJECTED
    payment.save()
    payment
  } 
  
  def bytesOfProofOfPayment(paymentId){
    def payment = Payment.get(paymentId)
    def url = new URL(payment.proofOfPayment.url())
    url.getBytes()
  }


}
