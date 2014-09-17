package com.payable

import java.text.SimpleDateFormat

class DiscountService {
  
  def searchDiscountsOfAnOrganization(Organization organization,String stringToSearch){
    Discount.withCriteria{
      like('discountName',"%${stringToSearch}%")
      eq('organization',organization)
    }
  }

  def saveDiscountForPaymentSchemeOfOrganizationWithReferenceDate(organization,params){
    Discount discount = Discount.findByOrganizationAndDiscountName(organization,params.discountName)
     
    if(!discount){

      if(!params.previousDaysForCancelingDiscount){
        def format = new SimpleDateFormat("dd/MM/yyyy")
        
        try{
          def previousDays = getPreviousDaysForCancelingDiscount(format.parse(params.referenceDate),format.parse(params.expirationDate)) 
          params.previousDaysForCancelingDiscount = previousDays 
        }
        catch(Exception exception){
          params.previousDaysForCancelingDiscount = 0
        }
       
      }
      params.organization = organization
      discount = new Discount(params).save()
    }

    discount
  }


  private Integer getPreviousDaysForCancelingDiscount(Date referenceDate, Date expirationDate){
    (referenceDate ?: 0) - (expirationDate ?: 0) 
  }
}
