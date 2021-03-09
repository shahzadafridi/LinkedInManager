package com.realtimecoding.linkedinmanager.util

object URLManager {
    const val URL_FOR_AUTHORIZATION_CODE = "https://www.linkedin.com/oauth/v2/authorization"
    const val URL_FOR_ACCESS_TOKEN = "https://www.linkedin.com/oauth/v2/accessToken"
    const val URL_FOR_PROFILE_DATA = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))&oauth2_access_token="
    const val URL_FOR_EMAIL_ADDRESS = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))&oauth2_access_token="
    const val URL_FOR_ACCESS_TOKEN_VALIDATION = "https://www.linkedin.com/oauth/v2/introspectToken"
}