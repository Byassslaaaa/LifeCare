package com.example.lifecare.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.lifecare.utils.Constants

/**
 * AuthRepository - Single Source of Truth untuk semua operasi autentikasi
 * Menggunakan Firebase Authentication sebagai backend
 */
class AuthRepository(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val googleSignInHelper = GoogleSignInHelper(context)

    companion object {
        private const val TAG = "AuthRepository"
        private val USERS_COLLECTION = Constants.FIREBASE_USERS_COLLECTION
    }

    /**
     * Get current logged in user
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    /**
     * Register dengan email & password
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        fullName: String,
        age: String,
        gender: String
    ): AuthResult {
        return try {
            Log.d(TAG, "Starting email registration for: $email")

            // Create user di Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                Log.d(TAG, "Firebase user created successfully: ${firebaseUser.uid}")

                // Simpan data tambahan user ke Firestore
                val userData = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to email,
                    "fullName" to fullName,
                    "age" to age,
                    "gender" to gender,
                    "authProvider" to "email",
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(userData)
                    .await()

                Log.d(TAG, "User data saved to Firestore")

                AuthResult.Success(firebaseUser, "✅ Registrasi berhasil! Selamat datang di LifeCare")
            } else {
                AuthResult.Error("Gagal membuat akun. Silakan coba lagi")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            val errorMessage = when {
                e.message?.contains("email address is already in use") == true ->
                    "Email sudah terdaftar. Silakan gunakan email lain atau login"
                e.message?.contains("network") == true ->
                    "Tidak ada koneksi internet. Periksa koneksi Anda"
                e.message?.contains("password") == true ->
                    "Password terlalu lemah. Gunakan minimal 6 karakter"
                else -> "Registrasi gagal: ${e.message}"
            }
            AuthResult.Error(errorMessage)
        }
    }

    /**
     * Login dengan email & password
     */
    suspend fun loginWithEmail(email: String, password: String): AuthResult {
        return try {
            Log.d(TAG, "Starting email login for: $email")

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                Log.d(TAG, "Login successful for user: ${firebaseUser.uid}")

                // Update last login time
                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .update("lastLoginAt", System.currentTimeMillis())
                    .await()

                AuthResult.Success(firebaseUser, "✅ Login berhasil! Selamat datang kembali")
            } else {
                AuthResult.Error("Login gagal. Silakan coba lagi")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val errorMessage = when {
                e.message?.contains("no user record") == true ||
                e.message?.contains("invalid-credential") == true ->
                    "Email atau password salah. Periksa kembali dan coba lagi"
                e.message?.contains("user-disabled") == true ->
                    "Akun ini telah dinonaktifkan. Hubungi support"
                e.message?.contains("network") == true ->
                    "Tidak ada koneksi internet. Periksa koneksi Anda"
                else -> "Login gagal: ${e.message}"
            }
            AuthResult.Error(errorMessage)
        }
    }

    /**
     * Sign in dengan Google
     */
    suspend fun signInWithGoogle(): AuthResult {
        return try {
            Log.d(TAG, "Starting Google Sign-In")

            // Get Google credential
            val googleResult = googleSignInHelper.signIn()

            googleResult.fold(
                onSuccess = { googleSignInResult ->
                    // Authenticate dengan Firebase menggunakan Google ID Token
                    val credential = GoogleAuthProvider.getCredential(googleSignInResult.idToken, null)
                    val authResult = auth.signInWithCredential(credential).await()
                    val firebaseUser = authResult.user

                    if (firebaseUser != null) {
                        Log.d(TAG, "Google Sign-In successful: ${firebaseUser.uid}")

                        // Check if this is first time login (new user)
                        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

                        if (isNewUser) {
                            // Simpan data user baru ke Firestore
                            val userData = hashMapOf(
                                "uid" to firebaseUser.uid,
                                "email" to (googleSignInResult.email ?: ""),
                                "fullName" to (googleSignInResult.displayName ?: "User Google"),
                                "age" to "",
                                "gender" to "",
                                "authProvider" to "google",
                                "photoUrl" to (googleSignInResult.profilePictureUri ?: ""),
                                "createdAt" to System.currentTimeMillis(),
                                "updatedAt" to System.currentTimeMillis()
                            )

                            firestore.collection(USERS_COLLECTION)
                                .document(firebaseUser.uid)
                                .set(userData)
                                .await()

                            Log.d(TAG, "New Google user data saved to Firestore")
                            AuthResult.Success(firebaseUser, "✅ Registrasi dengan Google berhasil! Selamat datang di LifeCare")
                        } else {
                            // Update last login time untuk existing user
                            firestore.collection(USERS_COLLECTION)
                                .document(firebaseUser.uid)
                                .update("lastLoginAt", System.currentTimeMillis())
                                .await()

                            Log.d(TAG, "Existing Google user logged in")
                            AuthResult.Success(firebaseUser, "✅ Login dengan Google berhasil! Selamat datang kembali")
                        }
                    } else {
                        AuthResult.Error("Google Sign-In gagal. Tidak dapat mengautentikasi user")
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Google Sign-In failed", error)
                    AuthResult.Error(error.message ?: "Google Sign-In gagal")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In exception", e)
            AuthResult.Error("Google Sign-In gagal: ${e.message}")
        }
    }

    /**
     * Reauthenticate dengan password (untuk forgot PIN flow)
     */
    suspend fun reauthenticateWithPassword(password: String): AuthResult {
        return try {
            val user = currentUser
            if (user == null) {
                return AuthResult.Error("User tidak ditemukan. Silakan login kembali")
            }

            val email = user.email
            if (email == null) {
                return AuthResult.Error("Email tidak ditemukan. Metode login mungkin menggunakan Google")
            }

            Log.d(TAG, "Reauthenticating user: $email")

            // Create credential dengan email dan password
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)

            // Reauthenticate user
            user.reauthenticate(credential).await()

            Log.d(TAG, "Reauthentication successful")
            AuthResult.Success(user, "Password terverifikasi")

        } catch (e: Exception) {
            Log.e(TAG, "Reauthentication failed", e)
            val errorMessage = when {
                e.message?.contains("invalid-credential") == true ||
                e.message?.contains("wrong-password") == true ->
                    "Password salah. Silakan coba lagi"
                e.message?.contains("network") == true ->
                    "Tidak ada koneksi internet. Periksa koneksi Anda"
                else -> "Verifikasi gagal: ${e.message}"
            }
            AuthResult.Error(errorMessage)
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        Log.d(TAG, "Logging out user: ${currentUser?.email}")
        auth.signOut()
    }

    /**
     * Get user data from Firestore
     */
    suspend fun getUserData(uid: String): Map<String, Any>? {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            document.data
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user data", e)
            null
        }
    }

    /**
     * Update user data in Firestore
     */
    suspend fun updateUserData(uid: String, data: Map<String, Any>): Boolean {
        return try {
            val updateData = data.toMutableMap()
            updateData["updatedAt"] = System.currentTimeMillis()

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(updateData)
                .await()

            Log.d(TAG, "User data updated successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user data", e)
            false
        }
    }
}

/**
 * AuthResult - Sealed class untuk handle hasil autentikasi
 */
sealed class AuthResult {
    data class Success(val user: FirebaseUser, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
