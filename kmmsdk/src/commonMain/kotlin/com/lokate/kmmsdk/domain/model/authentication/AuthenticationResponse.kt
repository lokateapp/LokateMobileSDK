package com.lokate.kmmsdk.domain.model.authentication

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponse(
    val valid: Boolean
)