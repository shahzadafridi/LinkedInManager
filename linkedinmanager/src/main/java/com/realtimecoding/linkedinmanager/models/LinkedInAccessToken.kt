package com.realtimecoding.linkedinmanager.models

data class LinkedInAccessToken(
        var access_token: String? = null,
        var expires_in: Int = 0
)
