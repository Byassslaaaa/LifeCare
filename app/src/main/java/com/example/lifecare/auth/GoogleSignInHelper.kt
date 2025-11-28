package com.example.lifecare.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
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

    // Web Client ID dari Firebase Console (Web SDK configuration)
    private val webClientId = "923227050918-uar849jimarkq0jedsl9be4s96a8lla9.apps.googleusercontent.com"

    private val credentialManager = CredentialManager.create(context)

    /**
     * Memulai proses Google Sign-In
     */
    suspend fun signIn(): Result<GoogleSignInResult> {
        return try {
            Log.d(TAG, "Starting Google Sign-In process...")

            // Generate nonce untuk security
            val nonce = generateNonce()
            val hashedNonce = hashNonce(nonce)
            Log.d(TAG, "Nonce generated successfully")

            // Setup Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Allow semua akun Google
                .setServerClientId(webClientId)
                .setNonce(hashedNonce)
                .build()
            Log.d(TAG, "Google ID option configured with client ID: $webClientId")

            // Create credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            Log.d(TAG, "Credential request created")

            // Get credential
            Log.d(TAG, "Requesting credentials from CredentialManager...")
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            Log.d(TAG, "Credentials received successfully")

            // Handle response
            handleSignInResult(result)
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credentials available", e)
            Result.failure(Exception("""
                Google Sign-In memerlukan akun Google di perangkat.

                Untuk Emulator:
                1. Buka Settings → Accounts → Add account
                2. Pilih Google
                3. Login dengan akun Google Anda
                4. Restart aplikasi dan coba lagi

                Error: ${e.message}
            """.trimIndent()))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException occurred", e)
            Result.failure(Exception("Google Sign-In gagal: ${e.message}\n\nDetail: ${e.javaClass.simpleName}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sign-in", e)
            Result.failure(Exception("Error tidak terduga: ${e.message}\n\nDetail: ${e.javaClass.simpleName}"))
        }
    }

    companion object {
        private const val TAG = "GoogleSignInHelper"
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
