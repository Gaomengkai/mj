package icu.merky.mj.core.result

sealed interface AppError {
    data object Unknown : AppError
    data class Validation(val message: String) : AppError
    data class Data(val message: String) : AppError
    data class Network(val message: String) : AppError
}
