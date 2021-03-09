package com.realtimecoding.linkedinmanager.events

interface LinkedInAccessTokenValidationResponse {
    fun onValidationSuccess()
    fun onValidationFailed()
}