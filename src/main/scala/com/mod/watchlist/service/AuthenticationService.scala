package com.mod.watchlist.service

trait AuthenticationService {
  def authenticate(id: String): Option[Customer]
}
