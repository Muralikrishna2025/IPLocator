package uk.ac.tees.mad.iplocator.model.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails


class AuthRepository(private val auth: FirebaseAuth) {
    fun signUp(email: String, pass: String): Flow<AuthResult<Boolean>> = flow {
        try {
            emit(AuthResult.Loading)
            auth.createUserWithEmailAndPassword(email, pass).await()
            emit(AuthResult.Success(true))
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

    fun logIn(email: String, pass: String): Flow<AuthResult<Boolean>> = flow {
        try {
            emit(AuthResult.Loading)
            auth.signInWithEmailAndPassword(email, pass).await()
            emit(AuthResult.Success(true))
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

    fun isLoggedIn(): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already authenticated
            return true
        } else {
            // User is not authenticated
            return false
        }
    }

    fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    fun LogOut() {
        auth.signOut()
    }

    fun getCurrentUserDetails(): Flow<AuthResult<UserDetails>> = flow {
        emit(AuthResult.Loading)
        try {
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                val userDetails = UserDetails(
                    userId = currentUser.uid,
                    email = currentUser.email,
                    displayName = currentUser.displayName,
                    isEmailVerified = currentUser.isEmailVerified,
                    phoneNumber = currentUser.phoneNumber
                )
                emit(AuthResult.Success(userDetails))
            } else {
                emit(AuthResult.Error(Exception("No user logged in")))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

}