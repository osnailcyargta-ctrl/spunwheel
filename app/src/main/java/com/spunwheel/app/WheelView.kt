package com.spunwheel.app

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.view.View
import kotlin.math.*

class WheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var items: List<WheelItem> = emptyList()
        set(value) { field = value; invalidate() }

    private var rotation = 0f
    private var isSpinning = false
    private var resultIndex = -1
    var onSpinEnd: ((WheelItem) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#1A1A1E")
        strokeWidth = 3f
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0A0A0C")
    }
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (items.isEmpty()) return

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) - 16f
        val count = items.size

        canvas.save()
        canvas.rotate(rotation, cx, cy)

        val sweepAngle = 360f / count
        val rectF = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        items.forEachIndexed { i, item ->
            val startAngle = i * sweepAngle

            paint.color = item.color
            paint.style = Paint.Style.FILL
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)

            canvas.drawArc(rectF, startAngle, sweepAngle, true, borderPaint)

            textPaint.color = item.textColor
            val textRadius = radius * 0.65f

            val maxWidth = 2 * radius * sin(Math.toRadians(sweepAngle / 2.0)).toFloat() * 0.85f
            textPaint.textSize = 42f
            while (textPaint.measureText(item.label) > maxWidth && textPaint.textSize > 18f) {
                textPaint.textSize -= 2f
            }

            canvas.save()
            canvas.rotate((startAngle + sweepAngle / 2f), cx, cy)
            val textX = cx + textRadius
            val textY = cy + textPaint.textSize / 3f
            canvas.drawText(item.label, textX, textY, textPaint)
            canvas.restore()
        }

        canvas.restore()

        canvas.drawCircle(cx, cy, 28f, centerPaint)
        paint.color = Color.parseColor("#2A2A30")
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, 24f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, 10f, paint)

        val px = cx + radius + 14f
        val path = Path().apply {
            moveTo(px + 20f, cy)
            lineTo(px - 4f, cy - 16f)
            lineTo(px - 4f, cy + 16f)
            close()
        }
        canvas.drawPath(path, pointerPaint)
    }

    fun spin() {
        if (isSpinning || items.isEmpty()) return
        isSpinning = true

        val extraSpins = (5..10).random() * 360f
        val stopOffset = (0 until 360).random().toFloat()
        val totalRotation = rotation + extraSpins + stopOffset

        val count = items.size
        val sweepAngle = 360f / count
        val normalizedFinal = ((totalRotation % 360) + 360) % 360
        val pointerAngle = (360f - normalizedFinal + 360f) % 360f
        resultIndex = ((pointerAngle / sweepAngle).toInt()) % count

        val animator = ValueAnimator.ofFloat(rotation, totalRotation).apply {
            duration = (3000..5000).random().toLong()
            interpolator = DecelerateInterpolator(3f)
            addUpdateListener {
                rotation = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rotation = totalRotation % 360f
                    isSpinning = false
                    if (resultIndex in items.indices) {
                        onSpinEnd?.invoke(items[resultIndex])
                    }
                }
            })
        }
        animator.start()
    }

    fun reset() {
        rotation = 0f
        isSpinning = false
        resultIndex = -1
        invalidate()
    }
}
