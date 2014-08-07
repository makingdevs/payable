package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification
import org.grails.s3.S3Asset
import org.grails.s3.S3AssetService

@TestFor(ProofOfPaymentService)
@Mock([Payment,ApplicableDiscount])
class ProofOfPaymentServiceSpec extends Specification {

  def "Conciliate a payment with a correct proof of payment"(){
    given: "a payment without conciliation"
      def payment = new Payment(paymentConcept:"",
                                dueDate: new Date()+10,
                                paymentAmount: 1000,
                                transactionId:"1234567890",
                                proofOfPayment:new S3Asset())

      def applicableDiscount = new ApplicableDiscount(applicableDiscountStatus: ApplicableDiscountStatus.VALID)
      payment.addToApplicableDiscounts(applicableDiscount)
      payment.save(validate:false)

    when: 
      def approvedPayment = service.approvePayment("1234567890",new Date() - 2, _paymentType)  

    then: 
      approvedPayment.paymentStatus == PaymentStatus.PAID
      approvedPayment.dueDate
      approvedPayment.paymentType == _paymentType
      approvedPayment.applicableDiscounts.first().applicableDiscountStatus == ApplicableDiscountStatus.APPLIED 

    where:
      _paymentType << [PaymentType.WIRE_TRANSFER, PaymentType.REFERENCED_DEPOSIT, PaymentType.CHECK, PaymentType.CASH, PaymentType.TERMINAL]
  }

  def "Conciliate a payment with an incorrect proof of payment indicating if the payment is not valid"(){
    given: "A payment without Conciliation"
      def payment = new Payment(paymentConcept:"",
                                dueDate: new Date()+10,
                                paymentAmount:1000,
                                transactionId:"1234567890",
                                proofOfPayment:new S3Asset()).save(validate:false)

    when:
      def s3AssetServiceMock = mockFor(S3AssetService) 
      s3AssetServiceMock.demand.delete(1..1){ S3Asset -> null }
      service.s3AssetService = s3AssetServiceMock.createMock()
 
    and:
      def rejectedPayment = service.rejectPayment("1234567890") 
      s3AssetServiceMock.verify()

    then:
      rejectedPayment.paymentStatus == PaymentStatus.REJECTED
      !rejectedPayment.paymentDate
      !rejectedPayment.proofOfPayment
  }
}
