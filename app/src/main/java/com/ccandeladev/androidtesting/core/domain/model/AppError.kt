package com.ccandeladev.androidtesting.core.domain.model

// To errors control
sealed class AppError: Exception() {

    data object NetworkError: AppError()
    data object NotFoundError: AppError()
    data object DAtaBaseError: AppError()
    data class ValidationError(override val message: String): AppError()
    data class UnknownError(override val message: String?): AppError()
}