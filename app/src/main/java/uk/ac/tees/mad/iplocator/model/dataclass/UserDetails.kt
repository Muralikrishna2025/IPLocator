package uk.ac.tees.mad.iplocator.model.dataclass

data class UserDetails(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val isEmailVerified: Boolean,
    val phoneNumber: String?
)