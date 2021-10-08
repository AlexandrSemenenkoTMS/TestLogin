package dev.fest.testlogin

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import dev.fest.testlogin.databinding.SignDialogBinding

class DialogHelper(activity: MainActivity) {
    private val activityDialogHelper = activity
    val accountHelper = AccountHelper(activity)

    fun createSignDialog(index: Int) {

        val builder = AlertDialog.Builder(activityDialogHelper)
        val bindingDialogHelper = SignDialogBinding.inflate(activityDialogHelper.layoutInflater)
        builder.setView(bindingDialogHelper.root)
        setDialogState(index, bindingDialogHelper)

        val dialog = builder.create()

        bindingDialogHelper.buttonSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, bindingDialogHelper, dialog)
        }

        bindingDialogHelper.buttonForgetPassword.setOnClickListener {
            setOnClickResetPassword(bindingDialogHelper, dialog)
        }
        bindingDialogHelper.buttonSignInGoogle.setOnClickListener {
            accountHelper.signInClientWithGoogle()
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun setOnClickSignUpIn(
        index: Int,
        bindingDialogHelper: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        dialog?.dismiss()
        if (index == DialogConst.SIGN_UP_STATE) {
            accountHelper.signUpWithEmail(
                bindingDialogHelper.editTextSignEmail.text.toString(),
                bindingDialogHelper.editTextSignPassword.text.toString(),
            )
        } else {
            accountHelper.signInWithEmail(
                bindingDialogHelper.editTextSignEmail.text.toString(),
                bindingDialogHelper.editTextSignPassword.text.toString()
            )
        }
    }

    private fun setOnClickResetPassword(
        bindingDialogHelper: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        if (bindingDialogHelper.editTextSignEmail.text.isNotEmpty()) {
            activityDialogHelper.mAuth.sendPasswordResetEmail(bindingDialogHelper.editTextSignEmail.text.toString())
                .addOnCompleteListener { taks ->
                    if (taks.isSuccessful) {
                        Toast.makeText(
                            activityDialogHelper,
                            R.string.email_reset_password_was_sent,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            dialog?.dismiss()
        } else {
        bindingDialogHelper.textViewDialogMessage.visibility = View.VISIBLE
    }
}

private fun setDialogState(index: Int, bindingDialogHelper: SignDialogBinding) {
    if (index == DialogConst.SIGN_UP_STATE) {
        bindingDialogHelper.apply {
            textViewSignTitle.text = activityDialogHelper.resources.getString(R.string.sign_up)
            buttonSignUpIn.text =
                activityDialogHelper.resources.getString(R.string.sign_up_action)
        }
    } else {
        bindingDialogHelper.apply {
            textViewSignTitle.text = activityDialogHelper.resources.getString(R.string.sign_in)
            buttonSignUpIn.text =
                activityDialogHelper.resources.getString(R.string.sign_in_action)
            buttonForgetPassword.visibility = View.VISIBLE
        }
    }
}
}

object DialogConst {
    const val SIGN_IN_STATE = 0
    const val SIGN_UP_STATE = 1
}