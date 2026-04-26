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

        

        lottie.speed = 1.0f

        lottie.alpha = 0f
        lottie.animate()
            .alpha(1f)
            .setDuration(800)
            .start()

        lottie.addLottieOnCompositionLoadedListener { composition ->
            Log.d("Lottie", "Splash animation loaded successfully")
            Log.d("Lottie", "Animation duration: ${composition.duration}ms")
        }

        lottie.setFailureListener {
            Log.e("LottieError", "Splash animation failed to load", it)

            navigateToLogin()
        }

        lottie.addAnimatorUpdateListener { animator ->
            if (animator.animatedValue as Float >= 0.95f) {

                lottie.removeAllAnimatorListeners()
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLogin()
                }, 500)
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