package com.realtimecoding.linkedinmanager.events

interface LinkedInUserLoginDetailsResponse {
    fun loggedMode(mode: Int)
    fun tokenExpired()
    fun notLogged()
}