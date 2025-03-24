package uk.ac.tees.mad.iplocator.model.dataclass

sealed class IpDetailsUiState {
    object Loading : IpDetailsUiState()
    data class Success(val ipLocationDetails: IpLocation) : IpDetailsUiState()
    data class Error(val errorState: ErrorState, val message: String) : IpDetailsUiState()
}
