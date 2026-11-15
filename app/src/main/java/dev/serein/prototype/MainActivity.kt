package dev.serein.prototype

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import dev.serein.prototype.ui.PhonePrototypeApp

class MainActivity : AppCompatActivity() {
    private var unlockAfterAuthentication: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhonePrototypeApp(
                onRequestBiometric = { onAuthenticated ->
                    unlockAfterAuthentication = onAuthenticated
                    requestBiometric()
                },
            )
        }
    }

    private fun requestBiometric() {
        val capability = BiometricManager.from(this).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK,
        )
        if (capability != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(
                this,
                "Biometric unlock is unavailable. Use your device's real lock screen.",
                Toast.LENGTH_LONG,
            ).show()
            unlockAfterAuthentication = null
            return
        }

        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    unlockAfterAuthentication?.invoke()
                    unlockAfterAuthentication = null
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    unlockAfterAuthentication = null
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        Toast.makeText(this@MainActivity, errString, Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Serein preview")
                .setSubtitle("Confirm your identity with your device")
                .setNegativeButtonText("Cancel")
                .build(),
        )
    }
}
