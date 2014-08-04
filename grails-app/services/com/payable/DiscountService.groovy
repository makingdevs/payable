package com.payable

class DiscountService {
  
  def searchDiscountsOfAnOrganization(Organization organization,String stringToSearch){
    Discount.withCriteria{
      like('discountName',"%${stringToSearch}%")
      eq('organization',organization)
    }
  }

}
