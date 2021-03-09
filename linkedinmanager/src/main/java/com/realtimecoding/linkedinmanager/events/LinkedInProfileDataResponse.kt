package com.realtimecoding.linkedinmanager.events

import org.json.JSONObject

interface LinkedInProfileDataResponse {
    fun onRequestSuccess(jsonObject: JSONObject?)
    fun onRequestFailed()
}