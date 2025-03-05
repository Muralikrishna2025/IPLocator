package uk.ac.tees.mad.iplocator.model.dataclass

sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<out T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}