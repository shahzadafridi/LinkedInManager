package com.realtimecoding.linkedinmanager.models

data class LinkedInLoggedTokenBean(
         var linkedInAccessToken: String? = null,
         val requestMode: Int = 0,
         val loggedIn: Boolean = false
)
