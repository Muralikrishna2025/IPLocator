package uk.ac.tees.mad.iplocator.model.dataclass

sealed class ErrorState {
    /**
     * Indicates a network-related error (e.g., no internet connection).
     */
    object NetworkError : ErrorState()

    /**
     * Indicates a timeout error (e.g., the server took too long to respond).
     */
    object TimeoutError : ErrorState()

    /**
     * Indicates an error with the data itself (e.g., parsing error).
     */
    object DataError : ErrorState()

    /**
     * Indicates an unknown or unexpected error.
     */
    object UnknownError : ErrorState()
}