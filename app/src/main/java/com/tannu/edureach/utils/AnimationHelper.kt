package com.tannu.edureach.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

object AnimationHelper {

    fun startSwayAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", -3f, 3f)
        animator.duration = 2500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    fun startFloatAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "translationY", -15f, 15f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    fun startPulseAnimation(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.05f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.05f)
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ValueAnimator.REVERSE
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ValueAnimator.REVERSE
        scaleX.duration = 1500
        scaleY.duration = 1500
        scaleX.start()
        scaleY.start()
    }
}