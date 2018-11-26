package com.mod.watchlist.dao

import com.mod.watchlist.service.Customer

trait CustomerDao {
  def findCustomer(id: String): Option[Customer]
}
