package com.example.playlistmaker.player.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val imageBitmapPlay: Bitmap?
    private val imageBitmapPause: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    var defaultState = true
        set(value) {
            field = value
            invalidate()
        }
    var clickEvent: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imageBitmapPlay = getDrawable(R.styleable.PlaybackButtonView_srcPlay)?.toBitmap()
                imageBitmapPause = getDrawable(R.styleable.PlaybackButtonView_srcPause)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (defaultState) {
            imageBitmapPlay?.let {
                canvas.drawBitmap(imageBitmapPlay, null, imageRect, null)
            }
        } else {
            imageBitmapPause?.let {
                canvas.drawBitmap(imageBitmapPause, null, imageRect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                clickEvent?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}