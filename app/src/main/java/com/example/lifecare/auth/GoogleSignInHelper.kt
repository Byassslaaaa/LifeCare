package com.example.lifecare.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

data class GoogleSignInResult(
    val idToken: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUri: String?
)

class GoogleSignInHelper(private val context: Context) {

    // Web Client ID dari Google Cloud Console
    private val webClientId = "573764659302-iuu3m0pu89jtm2rcgs32rs6ga5i2g611.apps.googleusercontent.com"

    private val credentialManager = CredentialManager.create(context)

    /**
     * Memulai proses Google Sign-In
     */
    suspend fun signIn(): Result<GoogleSignInResult> {
        return try {
            // Generate nonce untuk security
            val nonce = generateNonce()
            val hashedNonce = hashNonce(nonce)

            // Setup Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Allow semua akun Google
                .setServerClientId(webClientId)
                .setNonce(hashedNonce)
                .build()

            // Create credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Get credential
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            // Handle response
            handleSignInResult(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In gagal: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): Result<GoogleSignInResult> {
        return try {
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val signInResult = GoogleSignInResult(
                    idToken = googleIdTokenCredential.idToken,
                    displayName = googleIdTokenCredential.displayName,
                    email = googleIdTokenCredential.id,
                    profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                )

                Result.success(signInResult)
            } else {
                Result.failure(Exception("Tipe credential tidak valid"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memproses credential: ${e.message}"))
        }
    }

    // Generate random nonce untuk security
    private fun generateNonce(): String {
        return UUID.randomUUID().toString()
    }

    // Hash nonce menggunakan SHA-256
    private fun hashNonce(nonce: String): String {
        val bytes = nonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
