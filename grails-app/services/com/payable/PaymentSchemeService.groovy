package com.payable

class PaymentSchemeService {

  def savePaymentScheme(PaymentGroupCommand pgc){
    Concept concept = Concept.findByDescriptionAndOrganization(pgc.paymentConcept,pgc.organization)
    def paymentScheme = PaymentScheme.findByConcept(concept) ?: new PaymentScheme(
      paymentAmount:pgc.paymentAmount,
      concept:concept,
      surcharge:Surcharge.get(pgc.surchargeId)
    )
    
    if(!paymentScheme.id){
      pgc.discountIds.each{ id ->
        def discount = Discount.findById(id)
        paymentScheme.addToDiscounts(discount)
      }
      paymentScheme.save(flush:true)
    }
    
    paymentScheme
  }

}
