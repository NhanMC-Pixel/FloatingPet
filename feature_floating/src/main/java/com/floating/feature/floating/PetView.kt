package com.floatingpet.feature.floating

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.floatingpet.domain.PetState

class PetView(context: Context) : FrameLayout(context) {

    private val circleView = CircleView(context)
    private val labelView = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        textSize = 14f
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var dragListener: ((dx: Int, dy: Int) -> Unit)? = null
    private var currentState: PetState = PetState.IDLE

    init {
        addView(circleView, LayoutParams(120, 120).apply {
            gravity = Gravity.CENTER
        })
        addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        })
        setWillNotDraw(false)
        isFocusableInTouchMode = false
    }

    fun setState(state: PetState) {
        if (currentState == state) return
        currentState = state
        // Update appearance based on state
        circleView.setColor(getColorForState(state))
        labelView.text = state.name
    }

    fun setDragListener(listener: (dx: Int, dy: Int) -> Unit) {
        dragListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.rawX
                lastTouchY = event.rawY
                // Tell service we are dragging
                setState(PetState.DRAG)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - lastTouchX).toInt()
                val dy = (event.rawY - lastTouchY).toInt()
                if (dx != 0 || dy != 0) {
                    dragListener?.invoke(dx, dy)
                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setState(PetState.IDLE)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getColorForState(state: PetState): Int = when (state) {
        PetState.IDLE -> Color.LTGRAY
        PetState.WALK -> Color.GREEN
        PetState.SLEEP -> Color.DKGRAY
        PetState.DRAG -> Color.YELLOW
        PetState.DANCE -> Color.MAGENTA
        PetState.HAPPY -> Color.CYAN
        PetState.SAD -> Color.BLUE
    }

    /** Simple circular view that draws a filled circle */
    private inner class CircleView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private var circleColor = Color.LTGRAY
        private val rect = RectF()

        fun setColor(color: Int) {
            circleColor = color
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            paint.color = circleColor
            val padding = 4f
            rect.set(padding, padding, width - padding, height - padding)
            canvas.drawRoundRect(rect, 20f, 20f, paint)
        }
    }
}