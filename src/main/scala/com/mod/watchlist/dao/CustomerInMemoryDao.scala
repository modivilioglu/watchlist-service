package com.mod.watchlist.dao

import com.mod.watchlist.service.Customer

object CustomerInMemoryDao extends CustomerDao {
  var customerRepository: List[Customer] = Nil

  def withInitialData: CustomerDao = {
    customerRepository = List(Customer("CS1"), Customer("CS2"), Customer("123"), Customer("abc")) //Initial Data, as if already present in DB
    this
  }

  override def findCustomer(id: String): Option[Customer] = customerRepository.find(_.id == id)
}
