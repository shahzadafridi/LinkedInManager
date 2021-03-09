package com.realtimecoding.linkedinmanager.helper

import com.realtimecoding.linkedinmanager.util.CommonInfo
import com.realtimecoding.linkedinmanager.events.LinkedInAccessTokenResponse
import com.realtimecoding.linkedinmanager.events.LinkedInAccessTokenValidationResponse
import com.realtimecoding.linkedinmanager.events.LinkedInEmailAddressResponse
import com.realtimecoding.linkedinmanager.events.LinkedInProfileDataResponse

class LinkedInControl {
    fun getAccessToken(url: String?, linkedInAccessTokenResponse: LinkedInAccessTokenResponse?) {
        LinkedInNetworkTask(url!!, CommonInfo.MODE_ACCESS_TOKEN_REQUEST, linkedInAccessTokenResponse).doInBackground()
    }

    fun getProfileData(url: String?, linkedInProfileDataResponse: LinkedInProfileDataResponse?) {
        LinkedInNetworkTask(url!!, CommonInfo.MODE_PROFILE_DATA_REQUEST, linkedInProfileDataResponse).doInBackground()
    }

    fun getEmailAddress(url: String?, linkedInEmailAddressResponse: LinkedInEmailAddressResponse?) {
        LinkedInNetworkTask(url!!, CommonInfo.MODE_EMAIL_ADDRESS_REQUEST, linkedInEmailAddressResponse).doInBackground()
    }

    fun checkAccessTokenValidity(url: String?, urlParameters: String?, linkedInAccessTokenValidationResponse: LinkedInAccessTokenValidationResponse?) {
        LinkedInNetworkTask(url!!, urlParameters, CommonInfo.MODE_ACCESS_TOKEN_VALIDATION, linkedInAccessTokenValidationResponse).doInBackground()
    }
}