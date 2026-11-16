package com.example.rai_oreo.util

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/** Pembungkus BiometricPrompt untuk login sidik jari. */
object Biometric {

    private fun authenticators(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        }

    fun tersedia(context: Context): Boolean =
        BiometricManager.from(context).canAuthenticate(authenticators()) ==
            BiometricManager.BIOMETRIC_SUCCESS

    fun minta(
        activity: FragmentActivity,
        onSukses: () -> Unit,
        onGagal: (String) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSukses()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onGagal(errString.toString())
            }
        }
        val prompt = BiometricPrompt(activity, executor, callback)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Masuk ke SIMPER-NRA")
            .setSubtitle("Tempelkan sidik jari pada sensor perangkat")
            .setNegativeButtonText("Gunakan password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()
        prompt.authenticate(info)
    }
}
