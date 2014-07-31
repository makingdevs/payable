package com.payable

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Ignore

@TestFor(GenerationOfPaymentService)
@Mock([PaymentWithImplements,Payment,Discount,Concept,Surcharge,Organization,PaymentLink])
class GenerationOfPaymentServiceSpec extends Specification {

  def "Generation of payment for a litter"(){
    setup:
      
      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentConcept: _paymentConcept,
        paymentAmount: _paymentAmount,
        discountIds: [],
        doublePayment: [],
        daysPaymentDue: 4,
        dueDate: _dueDate,
        organization: new Organization().save(validate:false),
        instances: [new PaymentWithImplements().save()]
      )
    and:
      def conceptServiceMock = mockFor(ConceptService)
      conceptServiceMock.demand.savePaymentConcept{ organization, paymentConcept ->
        new Concept(organization:organization,paymentConcept:paymentConcept).save(validate:false)
      }
      service.conceptService = conceptServiceMock.createMock()

    when:
      def payments = service.generatePaymentsForGroup(paymentGroupCommand)

    then:
      assert payments.size() == _size
      assert payments.first().id > 0
      assert !payments.first().applicableDiscounts
      assert payments.first().paymentConcept == _paymentConcept
      assert payments.first().paymentAmount == _paymentAmount
    where:
     _paymentConcept | _paymentAmount | _dueDate      || _size  
     "PaymentConcept"|  100.00        | new Date()+7  || 1
  }

  @Ignore
  def "Generate a Payment with a discount for a group"(){
    setup: "An organization and a group"
      def organization = new Organization()
      organization.save(validate:false)  

      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentConcept: _paymentConcept,
        paymentAmount: _paymentAmount,
        discountIds: _discountIds,
        doublePayment: [],
        months: [],
        dueDate: _dueDate,
        organization: organization,
        instances: [new PaymentWithImplements().save()]     
      )
    
    and:
        Discount discount = new Discount()
        discount.discountName = "Discount 1" 
        discount.amount = 10
        discount.organization = organization
        discount.save(validate:false)
    and:
      def conceptServiceMock = mockFor(ConceptService)
      conceptServiceMock.demand.savePaymentConcept{ _organization, paymentConcept ->
        new Concept(organization:_organization,paymentConcept:paymentConcept).save(validate:false)
      }
      service.conceptService = conceptServiceMock.createMock()

    when:
      def payments = service.generatePaymentsForGroup(paymentGroupCommand)
      conceptServiceMock.verify()         
    then:
      assert payments.size() == 1
      assert payments.first().id > 0
      assert payments.first().applicableDiscounts
      assert payments.first().applicableDiscounts.first().id > 0
      assert payments.first().applicableDiscounts.first().cantidad ==  10

    where:
      _paymentConcept | _paymentAmount  |  _dueDate         |  _discountIds
      "concepto"      | 100.00          |  new Date() + 7   |  [1L]
  }
  
  def "Generate a payment with a surcharge for a group"(){
    given: "An organization and the group info"
      def organization = new Organization()
      organization.save(validate:false)

      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentConcept: _paymentConcept,
        paymentAmount: _paymentAmount,
        discountIds: [],
        doublePayment: [],
        surchargeId: _surchargeId, 
        months: [],
        dueDate: _dueDate,
        organization: organization,
        instances: [new PaymentWithImplements().save()]     
      )

    and:
      Surcharge surcharge = new Surcharge(
        amount: _amount,
        organization: organization
      ).save()

    and:
      def conceptServiceMock = mockFor(ConceptService)
      conceptServiceMock.demand.savePaymentConcept{ _organization, paymentConcept ->
        new Concept(organization:_organization,paymentConcept:paymentConcept).save(validate:false)
      }
      service.conceptService = conceptServiceMock.createMock()
    
    when:
      def payments = service.generatePaymentsForGroup(paymentGroupCommand)
      conceptServiceMock.verify()
    
    then:
      assert payments.size() == 1
      assert payments.first().id > 0
      assert !payments.first().applicableDiscounts
      assert payments.first().surcharge
      assert payments.first().surcharge.amount == _amount
  
    where:
      _paymentConcept | _paymentAmount | _dueDate       | _surchargeId |  _amount
      "concepto"      | 100.00         |  new Date()+7  | 1L           |  50.00
  }
  
  def "Generate a payment book for a litter"(){
    given: "An organization"
      def organization = new Organization()
      organization.save(validate:false)

      PaymentGroupCommand paymentGroupCommand = new PaymentGroupCommand(
        paymentConcept: _paymentConcept,
        paymentAmount: _paymentAmount,
        discountIds: [],
        doublePayment: [],
        months: _months,
        dueDate: _dueDate,
        organization: organization,
        instances: [new PaymentWithImplements().save()]
      )
    
    and:
      def conceptServiceMock = mockFor(ConceptService)
      conceptServiceMock.demand.savePaymentConcept{ _organization, paymentConcept ->
        new Concept(organization:_organization,paymentConcept:paymentConcept).save(validate:false)
      }
      service.conceptService = conceptServiceMock.createMock()
    
    when:
      def payments = service.generatePaymentsForGroup(paymentGroupCommand)
      conceptServiceMock.verify()
    
    then:
      assert payments.size() == 4
      assert payments.first().id > 0
      assert !payments.first().applicableDiscounts 
      assert !payments.first().surcharge 
      assert payments.first().paymentConcept == _paymentConcept
      assert payments.first().paymentAmount == _paymentAmount

    where:
      _paymentConcept   |   _paymentAmount    |  _dueDate        |   _months
      "paymentConcept"  |   100.00            |  new Date()+7    |   [1,3,5,11]
  }
}
