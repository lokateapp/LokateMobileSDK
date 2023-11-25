package com.lokate.kmmsdk.domain.model.authentication

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(val appToken: String)
