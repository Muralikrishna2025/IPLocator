package uk.ac.tees.mad.iplocator.model.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult


class AuthRepository(private val auth: FirebaseAuth){
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

    fun isLoggedIn(): Boolean{
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already authenticated
            return true
        } else {
            // User is not authenticated
            return false
        }
    }

    fun LogOut(){
        auth.signOut()
    }

}