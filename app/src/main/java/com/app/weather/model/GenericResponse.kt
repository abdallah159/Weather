package com.app.weather.model

data class GenericResponse<T>(
    val message: String = "",
    val data: T?,
    val error: Int?=0,
    val success: Boolean = false
)