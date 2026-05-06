package com.floatingpet.feature.floating

import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

class MangaBubbleView(context: Context) : FrameLayout(context) {

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private var bubbleBitmap: Bitmap? = null
    private val textView = TextView(context).apply {
        setTextColor(Color.BLACK)
        textSize = 14f
        maxLines = 3
        gravity = Gravity.CENTER
    }

    init {
        addView(textView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        })
        setWillNotDraw(false)
        clipChildren = false
    }

    fun setText(text: String) {
        textView.text = text
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // simple rounded rect bubble
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 20f, 20f, bubblePaint)
        canvas.drawRoundRect(rect, 20f, 20f, borderPaint)
        // small tail triangle would be nice but omitted for brevity
    }
}