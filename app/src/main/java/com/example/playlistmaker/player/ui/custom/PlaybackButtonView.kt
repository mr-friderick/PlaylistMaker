package com.example.playlistmaker.player.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val imagePlay: Drawable?
    private val imagePause: Drawable?
    private var imageRect = Rect(0, 0, 0, 0)
    var isPlaying = true
        set(value) {
            if (field == value) {
                return
            } else {
                field = value
                invalidate()
            }
        }
    var clickEventListener: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imagePlay = getDrawable(R.styleable.PlaybackButtonView_srcPlay)
                imagePause = getDrawable(R.styleable.PlaybackButtonView_srcPause)
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = Rect(0, 0, measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = if (isPlaying) imagePlay else imagePause
        drawable?.let {
            it.bounds = imageRect
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                clickEventListener?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}