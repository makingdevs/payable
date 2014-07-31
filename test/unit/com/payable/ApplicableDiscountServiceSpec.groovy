package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.Shared

@TestFor(ApplicableDiscountService)
@Mock([Discount,PaymentScheme,Payment,ApplicableDiscount])
class ApplicableDiscountServiceSpec extends Specification {
  
  @Shared createDate = {  n -> (new Date() + n).format("dd/MM/yyyy")  }

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

  @Unroll("With the reference date #_referenceDate and the previous expiration days #_previousDays, are applied #_appliedDiscounts with expiration #_expectedDates")
  def "Generate applied discounts of a paymente scheme"(){
    given:
      def discounts = createDiscounts(_previousDays)
      def paymentScheme = new PaymentScheme().save(validate:false)
      discounts.each{ discount -> paymentScheme.addToDiscounts(discount) }
      paymentScheme.save(validate:false)
      def referenceDate = Date.parse("dd/MM/yyyy",_referenceDate)

    when:
      def applicableDiscounts = service.generateForPaymentWithPaymentSchemeWithReferenceDate(1L, referenceDate)

    then:
      applicableDiscounts.size() == _appliedDiscounts
      applicableDiscounts.every{ applicableDiscount -> applicableDiscount.applicableDiscountStatus == ApplicableDiscountStatus.VALID}
      applicableDiscounts*.expirationDate*.format("dd/MM/yyyy").sort() == _expectedDates.sort()
      applicableDiscounts*.discount*.previousDaysForCancelingDiscount.sort() == _appliedDays.sort()

    where:
      _referenceDate  |  _previousDays  ||  _appliedDays  || _expectedDates                                 || _appliedDiscounts
      createDate(30)  | [7]             ||  [7]           || [createDate(23)]                               ||  1
      createDate(30)  | [7,14]          ||  [7,14]        || [createDate(23),createDate(16)]                ||  2
      createDate(30)  | [7,14,21]       ||  [7,14,21]     || [createDate(23),createDate(16),createDate(9)]  ||  3
      createDate(1)   | [7]             ||  []            || []                                             ||  0
      createDate(10)  | [7,14]          ||  [7]           || [createDate(3)]                                ||  1
      createDate(15)  | [7,14,21]       ||  [7,14]        || [createDate(8),createDate(1)]                  ||  2
  }

  private def createDiscounts(def previousDays){
    previousDays.collect{ day ->
      new Discount(previousDaysForCancelingDiscount:day).save(validate:false)
    } 
  }

  private def randomOf(int n){
    new Random().nextInt(n)
  }

}
