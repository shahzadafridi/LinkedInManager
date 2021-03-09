package com.realtimecoding.linkedinmanager.events

interface LinkedInUserLoginValidationResponse {
    fun activeLogin()
    fun tokenExpired()
    fun notLogged()
}