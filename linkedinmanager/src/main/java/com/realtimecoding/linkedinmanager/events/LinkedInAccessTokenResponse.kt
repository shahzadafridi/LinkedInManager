package com.realtimecoding.linkedinmanager.events

import org.json.JSONObject

interface LinkedInAccessTokenResponse {
    fun onAuthenticationSuccess(jsonObject: JSONObject?)
    fun onAuthenticationFailed()
}