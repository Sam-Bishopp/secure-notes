package com.sambishopp.securenotes.activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sambishopp.securenotes.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()
{
    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback()
            {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
                {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication Error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication Success")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val lockImage = findViewById<ImageView>(R.id.lockSymbol)

        checkBiometricSupport()

        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Biometric Authentication")
            .setSubtitle("Authentication is required")
            .setDescription("Use your fingerprint to verify your identity")
            .setNegativeButton("Cancel", this.mainExecutor, { _, _ ->
                notifyUser("Authentication cancelled")
            }).build()

        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)

        lockImage.setOnClickListener {
            val biometricPromptClicked = BiometricPrompt.Builder(this)
                .setTitle("Biometric Authentication")
                .setSubtitle("Authentication is required")
                .setDescription("Use your fingerprint to verify your identity")
                .setNegativeButton("Cancel", this.mainExecutor, { _, _ ->
                    notifyUser("Authentication cancelled")
                }).build()

            biometricPromptClicked.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }

    private fun getCancellationSignal(): CancellationSignal
    {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean
    {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if(!keyguardManager.isKeyguardSecure)
        {
            notifyUser("Fingerprint authentication has not been enabled in the settings")
            return false
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED)
        {
            notifyUser("Fingerprint authentication is not permitted")
            return false
        }

        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
        {
            true
        }

        else true
    }

    private fun notifyUser(message: String)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

    }
}