package com.mod.watchlist.service

import com.google.inject.Inject
import com.mod.watchlist.dao.CustomerDao

class AuthenticationServiceImpl @Inject()(dao: CustomerDao) extends AuthenticationService {
  override def authenticate(id: String): Option[Customer] = dao.findCustomer(id)
}
