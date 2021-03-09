package com.realtimecoding.linkedinmanager.events

import com.realtimecoding.linkedinmanager.models.LinkedInAccessToken
import com.realtimecoding.linkedinmanager.models.LinkedInEmailAddress
import com.realtimecoding.linkedinmanager.models.LinkedInUserProfile

interface LinkedInManagerResponse {
    fun onGetAccessTokenFailed()
    fun onGetAccessTokenSuccess(linkedInAccessToken: LinkedInAccessToken?)
    fun onGetCodeFailed()
    fun onGetCodeSuccess(code: String?)
    fun onGetProfileDataFailed()
    fun onGetProfileDataSuccess(linkedInUserProfile: LinkedInUserProfile?)
    fun onGetEmailAddressFailed()
    fun onGetEmailAddressSuccess(linkedInEmailAddress: LinkedInEmailAddress?)
}