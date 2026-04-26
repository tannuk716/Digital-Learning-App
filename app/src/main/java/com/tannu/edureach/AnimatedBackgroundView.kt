package com.tannu.edureach

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.Random

class AnimatedBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val random = Random()
    private var initialized = false
    private var touchX = -1000f
    private var touchY = -1000f

    private val particlePaint = Paint().apply {
        isAntiAlias = true
    }

    init {
        val typedValue = android.util.TypedValue()

        val hasColor = context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        if (hasColor) {
            particlePaint.color = typedValue.data
            particlePaint.alpha = 100
        } else {
            particlePaint.color = Color.parseColor("#88FFFFFF")
        }
    }
    
    private var parallaxX = 0f
    private var parallaxY = 0f

    fun updateParallaxOffset(roll: Float, pitch: Float) {

        parallaxX = roll * 20f
        parallaxY = pitch * 20f
    }

    data class Particle(var x: Float, var y: Float, var radius: Float, var speed: Float, var wobblePhase: Float)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        if (!initialized) {

            for (i in 0..60) {
                particles.add(
                    Particle(
                        x = random.nextFloat() * w,
                        y = random.nextFloat() * h,
                        radius = 8f + random.nextFloat() * 20f,
                        speed = 1f + random.nextFloat() * 4f,
                        wobblePhase = random.nextFloat() * (Math.PI.toFloat() * 2)
                    )
                )
            }
            initialized = true
        }
    }
    
    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        when (event.action) {
            android.view.MotionEvent.ACTION_DOWN, android.view.MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                touchY = event.y
            }
            android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                touchX = -1000f
                touchY = -1000f
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.save()

        canvas.translate(parallaxX, parallaxY)

        val time = System.currentTimeMillis() / 1000f

        for (p in particles) {
            canvas.drawCircle(p.x, p.y, p.radius, particlePaint)
            
            p.y -= p.speed

            p.x += Math.sin((time * p.speed + p.wobblePhase).toDouble()).toFloat() * 2f
            

            if (touchX > 0 && touchY > 0) {
                val dx = p.x - touchX
                val dy = p.y - touchY
                val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                if (dist < 200f) {
                    val force = (200f - dist) / 200f
                    p.x += (dx / dist) * force * 15f
                    p.y += (dy / dist) * force * 15f
                }
            }

            if (p.y < -p.radius - 100f) {
                p.y = height.toFloat() + p.radius + 100f
                p.x = random.nextFloat() * width
            } else if (p.x < -p.radius - 100f) {
                p.x = width.toFloat() + p.radius + 100f
            } else if (p.x > width + p.radius + 100f) {
                p.x = -p.radius - 100f
            }
        }
        
        canvas.restore()

        postInvalidateOnAnimation() 
    }
}