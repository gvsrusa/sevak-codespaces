package com.agriconnect.domain

/**
 * Base class for a Use Case (Interactor in Clean Architecture).
 * This class represents an execution unit for different use cases (this means that any use case
 * in the application should implement this contract).
 *
 * By convention each [UseCase] implementation will execute its job in a background thread
 * and will post the result in the UI thread.
 */
interface UseCase<in P, R> {
    suspend operator fun invoke(params: P): R
}

/**
 * Use case that does not take parameters.
 */
interface NoParamsUseCase<R> {
    suspend operator fun invoke(): R
}