package com.realtimecoding.linkedinmanager.models

data class LinkedInTokenValidationSuccessBean(
        var active: Boolean,
        var client_id: String,
        var authorized_at: Int,
        var created_at: Int,
        var status: String,
        var expires_at: Int,
        var scope: String
)
