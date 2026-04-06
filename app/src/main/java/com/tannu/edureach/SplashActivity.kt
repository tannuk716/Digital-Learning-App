package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {

    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottie = findViewById<LottieAnimationView>(R.id.lottieView)

        // Animation is set via XML (app:lottie_rawRes="@raw/splash")
        // and plays automatically (app:lottie_autoPlay="true")
        
        // Optional: control speed
        lottie.speed = 1.0f

        // Fade-in effect
        lottie.alpha = 0f
        lottie.animate()
            .alpha(1f)
            .setDuration(800)
            .start()

        // ✅ Debug success
        lottie.addLottieOnCompositionLoadedListener { composition ->
            Log.d("Lottie", "Splash animation loaded successfully")
            Log.d("Lottie", "Animation duration: ${composition.duration}ms")
        }

        // ❌ Catch errors (VERY IMPORTANT)
        lottie.setFailureListener {
            Log.e("LottieError", "Splash animation failed to load", it)
            // Navigate immediately if animation fails
            navigateToLogin()
        }

        // Wait for animation to complete before navigating
        lottie.addAnimatorUpdateListener { animator ->
            if (animator.animatedValue as Float >= 0.95f) {
                // Animation is almost complete (95%), navigate to login
                lottie.removeAllAnimatorListeners()
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLogin()
                }, 500) // Small delay after animation completes
            }
        }
    }

    private fun navigateToLogin() {
        if (!hasNavigated) {
            hasNavigated = true
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
