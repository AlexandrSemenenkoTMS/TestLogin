package dev.fest.testlogin

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import dev.fest.testlogin.GoogleConst.GOOGLE_SIGN_IN_REQUEST_CODE

class AccountHelper(activity: MainActivity) {
    private val activityAccountHelper = activity
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activityAccountHelper.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        activityAccountHelper.uiUpdate(task.result?.user)
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception =
                                task.exception as FirebaseAuthUserCollisionException
                            Log.d("MyLog", "error 1 ${exception.errorCode}")
                            if (exception.errorCode == GoogleConst.ERROR_EMAIL_ALREADY_IN_USE) {
                                linkEmailToGoogle(email, password)
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            Log.d("MyLog", "error 2 ${exception.errorCode}")
                            if (exception.errorCode == GoogleConst.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    activityAccountHelper,
                                    GoogleConst.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activityAccountHelper.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        activityAccountHelper.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "error ${task.exception}")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == GoogleConst.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    activityAccountHelper,
                                    GoogleConst.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else if (exception.errorCode == GoogleConst.ERROR_WRONG_PASSWORD) {
                                Toast.makeText(
                                    activityAccountHelper,
                                    GoogleConst.ERROR_WRONG_PASSWORD,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else if (task.exception is FirebaseAuthInvalidUserException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidUserException
                            Log.d("MyLog", "error ${exception.errorCode}")
                            if (exception.errorCode == GoogleConst.ERROR_USER_NOT_FOUND) {
                                Toast.makeText(
                                    activityAccountHelper,
                                    GoogleConst.ERROR_USER_NOT_FOUND,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
        }

    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    activityAccountHelper,
                    "Вы зарегистрировались. Для подтверждения зайдите в почту.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    activityAccountHelper,
                    "Забыли пароль? ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activityAccountHelper.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activityAccountHelper, gso)
    }

    fun signInClientWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activityAccountHelper.startActivityForResult(
            intent,
            GOOGLE_SIGN_IN_REQUEST_CODE
        )
    }

    fun signOutClientWithGoogle() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        activityAccountHelper.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activityAccountHelper, "Sign in done", Toast.LENGTH_LONG)
                        .show()
                    activityAccountHelper.uiUpdate(task.result?.user)
                } else {
                    Toast.makeText(activityAccountHelper, "Sign in false", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (activityAccountHelper.mAuth.currentUser != null) {
            activityAccountHelper.mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activityAccountHelper,
                            activityAccountHelper.resources.getString(R.string.link_done),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                activityAccountHelper,
                R.string.entre_to_google_account,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}

object GoogleConst {
    const val GOOGLE_SIGN_IN_REQUEST_CODE = 1234
    const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
    const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
    const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
    const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
}