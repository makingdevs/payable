package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore

@TestFor(ApplicableDiscountService)
@Mock([Discount,PaymentScheme,Payment,ApplicableDiscount])
class ApplicableDiscountServiceSpec extends Specification {

  @Unroll("Generate a discount with an expiration date #_expirationDate and percentage of #_percentage %")
  def "Generate an applicated discount with a due date and a discount"(){
    given: "A discount and an expiration date"
      new Discount(
        discountName:_discountName, 
        percentage:_percentage,
        amount:0,
        previousDaysForCancelingDiscount:_previousDays).save(validate:false)

      def expirationDate = _expirationDate
    when:
      def applicableDiscount = service.generateForPaymentWithExpirationDate(expirationDate,1L)

    then:
      applicableDiscount.applicableDiscountStatus == ApplicableDiscountStatus.VALID
      applicableDiscount.discount.discountName == _discountName 
      applicableDiscount.discount.percentage == _percentage
      applicableDiscount.expirationDate.month == expirationDate.month
      applicableDiscount.expirationDate.year == expirationDate.year
      applicableDiscount.expirationDate.day == expirationDate.day

    where:
      _discountName   |   _percentage   |   _previousDays   |   _expirationDate
      "X Discount"    |  randomOf(10)   |   randomOf(30)    | new Date() - randomOf(21)
  } 


  private def randomOf(int n){
    new Random().nextInt(n)
  }
}
