package com.realtimecoding.linkedinmanager.events

import org.json.JSONObject

interface LinkedInEmailAddressResponse {
    fun onSuccessResponse(jsonObject: JSONObject?)
    fun onFailedResponse()
}