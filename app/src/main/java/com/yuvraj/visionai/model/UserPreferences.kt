package com.yuvraj.visionai.model

data class UserPreferences(
    val name: String? = "",
    val phoneNumber: String? = "",
    val age: Int? = 0,
    val gender: String? = "",
    val email: String? = ""
) {
    constructor() : this("", "", 0, "", "")
}