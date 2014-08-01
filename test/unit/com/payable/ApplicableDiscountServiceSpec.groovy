package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.Shared

@TestFor(ApplicableDiscountService)
@Mock([Discount,PaymentScheme,Payment,ApplicableDiscount])
class ApplicableDiscountServiceSpec extends Specification {
  
  @Shared createDate = {  n -> (new Date() - n).format("dd/MM/yyyy")  }

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
      def paymentScheme = new PaymentScheme()
      discounts.each{ discount -> paymentScheme.addToDiscounts(discount) }
      paymentScheme.save(validate:false)
      def referenceDate = Date.parse("dd/MM/yyyy",_referenceDate)

    when:
      def applicableDiscounts = service.generateForPaymentWithPaymentSchemeWithReferenceDate(paymentScheme.id,referenceDate)
    then:
      applicableDiscounts.size() == _appliedDiscounts
      applicableDiscounts.every{ applicableDiscount -> applicableDiscount.applicableDiscountStatus == ApplicableDiscountStatus.VALID}
      applicableDiscounts*.expirationDate*.format("dd/MM/yyyy").sort() == _expectedDates.sort()
      applicableDiscounts*.discount*.previousDaysForCancelingDiscount.sort() == _appliedDays.sort()

    where:
      _referenceDate  |  _previousDays  ||  _appliedDays  || _expectedDates                                 || _appliedDiscounts
      createDate(5)   | [7]             ||  [7]           || [createDate(12)]                               ||  1
      createDate(5)   | [5,14]          ||  [5,14]        || [createDate(10),createDate(19)]                ||  2
      createDate(0)   | [0,14,21]       ||  [14,21]       || [createDate(14),createDate(21)]                ||  2 
      createDate(0)   | [0]             ||  []            || []                                             ||  0
      createDate(-5)  | [4,14]          ||  [14]          || [createDate(9)]                                ||  1
      createDate(3)   | [7,14,0]        ||  [7,14,0]      || [createDate(10),createDate(17),createDate(3)]  ||  3
  }

  def "Add an applicable discount to a payment"(){
    given:
      def payment = new Payment(paymentAmount:_paymentAmount,
                                accumulatedDiscount:_accumulatedDiscount).save(validate:false)
      def discount = new Discount(percentage:_percentage,
                                  amount:_amount).save(validate:false) 
      def applicableDiscount = new ApplicableDiscount(discount:discount).save(validate:false,discount:discount)
    when:
      def numberOfApplicableDiscounts = payment?.applicableDiscounts?.size() ?: 0
      def expectedPayment = service.addApplicableDiscountToAPayment(applicableDiscount,payment.id)
        
    then:
      expectedPayment.applicableDiscounts.size() == numberOfApplicableDiscounts + 1
      expectedPayment.paymentAmount == _paymentAmount
      expectedPayment.accumulatedDiscount == _newAccumulatedDiscount

    where: 
      _paymentAmount  | _accumulatedDiscount  | _percentage | _amount   ||  _newAccumulatedDiscount
      100             | 0                     | 10          | ""        ||  10
      750             | 100                   | 15          | ""        ||  212.5
      3250            | 325                   | 10          | ""        ||  650
      3250            | 650                   | 10          | ""        ||  975
      3250            | 650                   | ""          | 500       ||  1150
  } 

  def "Invalidate an applicable discount to a payment if there is only a discount"(){
    given: "A discount of a payment"
      def discount = new Discount(percentage:_percentage,amount:_amount).save(validate:false)
      def applicableDiscount = new  ApplicableDiscount(discount:discount).save(validate:false)
      def payment = new Payment(paymentAmount:_paymentAmount,accumulatedDiscount:_accumulatedDiscount)
      .addToApplicableDiscounts(applicableDiscount)
      .save(validate:false)

    when: "the service is called to remove the discount to an applicable discount"
      service.invalidateApplicableDiscountToAPayment(applicableDiscount,payment.id)

    then:
      payment.applicableDiscounts.size() == 1
      payment.paymentAmount == _paymentAmount
      payment.accumulatedDiscount == _newAccumulatedDiscount

    where:
      _paymentAmount    | _accumulatedDiscount    | _percentage | _amount   ||  _newAccumulatedDiscount
      100               | 10                      | 10          | ""        ||  0
      750               | 112.5                   | 15          | null      ||  0
      100               | 10                      | null        | 10        ||  0 
  }

  def "Invalidate an applicable discount to a payment if there is more than one discount"(){
    given: "the payment discounts" 
      def discounts = [new Discount(percentage:_percentage1,amount:_amount1).save(validate:false),
                       new Discount(percentage:_percentage2,amount:_amount2).save(validate:false)]

      def applicableDiscounts = [new ApplicableDiscount(discount:discounts[0]).save(validate:false),
                                 new ApplicableDiscount(discount:discounts[1]).save(validate:false)]

      def payment = new Payment(paymentAmount:_paymentAmount,accumulatedDiscount:_accumulatedDiscount)      
      applicableDiscounts.each{ applicableDiscount ->
        payment.addToApplicableDiscounts(applicableDiscount)
      }
      payment.save(validate:false)
   
    when: "the service to invalidate the discount to the payment is called"
      service.invalidateApplicableDiscountToAPayment(applicableDiscounts[1],payment.id)
    
   then:
      payment.applicableDiscounts.size() == 2
      payment.paymentAmount == _paymentAmount
      payment.accumulatedDiscount == _newAccumulatedDiscount
   
    where:
      _paymentAmount |  _accumulatedDiscount  | _percentage1  | _amount1 | _percentage2  | _amount2   || _newAccumulatedDiscount
      100            |  25                    | 10            | null     |  15           |  ""        ||  10 
      100            |  25                    | 10            | ""       |  null         |  15        ||  10
      750            |  125                   | 10            | ""       |  ""           |  50        ||  75
      750            |  150                   | 10            | ""       |  10           |  ""        ||  75
  }

  @Unroll("Given a payment with a payment amount of #_paymentAmount and two discounts of #_percentage1 % - \$ #_amount1 and #_percentage2 % - \$ #_amount2 with the accumulated discount of #_accumulatedDiscount, when its discounts have expired, it new accumulated discount will be of #_newAccumulatedDiscount") 
  def "Invalidate the applicable discounts for a payment and recalculate it"(){
    given:"A payment with its accumulated discounts whose expiration date is greater than today"

      def applicableDiscounts = [new ApplicableDiscount(discount:new Discount(percentage:_percentage1,amount:_amount1).save(validate:false),
                                                        expirationDate:new Date()+2,
                                                        applicableDiscountStatus: ApplicableDiscountStatus.VALID).save(validate:false),
                                 new ApplicableDiscount(discount:new Discount(percentage:_percentage2,amount:_amount2).save(validate:false),
                                                        expirationDate:new Date()+6,
                                                        applicableDiscountStatus: ApplicableDiscountStatus.VALID).save(validate:false)]

      def payment = new Payment(paymentAmount:_paymentAmount,accumulatedDiscount:_accumulatedDiscount).save(validate:false)
      applicableDiscounts.each{ applicableDiscount ->
        payment.addToApplicableDiscounts(applicableDiscount) 
      }
      payment.save(validate:false)
    
    when: "the service to invalidate all the applicated discounts is called"
      service.expireDiscountsAndRecalculatePayment() 

    then:
      payment.applicableDiscounts.size() == 2
      payment.paymentAmount == _paymentAmount
      payment.accumulatedDiscount == _newAccumulatedDiscount
    
    where:
      _paymentAmount  | _accumulatedDiscount  | _percentage1  | _amount1  | _percentage2  | _amount2  ||  _newAccumulatedDiscount
      100             | 25                    | 10            | null      | 15            | ""        ||  0   
      100             | 25                    | null          | 10        | null          | 15        ||  0
      750             | 125                   | 10            | ""        | ""            | 50        ||  0
      750             | 150                   | null          | 75        | 10            | ""        ||  0
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
