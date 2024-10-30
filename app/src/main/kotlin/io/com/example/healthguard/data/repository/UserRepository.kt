

package io.com.example.healthguard.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.com.example.healthguard.data.auth.AuthLiveData

class UserRepository(private val auth: FirebaseAuth) {

    fun getFirebaseAuthState(): AuthLiveData = AuthLiveData(auth)

    fun getUser() = auth.currentUser as FirebaseUser

    fun login(email: String, password: String) = auth.signInWithEmailAndPassword(email, password)

    fun login(credential: AuthCredential) = auth.signInWithCredential(credential)

    fun logout() = auth.signOut()

    fun register(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password)

    fun register(credential: AuthCredential) = login(credential)
}
