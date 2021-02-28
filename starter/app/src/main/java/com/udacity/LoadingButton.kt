package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import kotlin.properties.Delegates
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var customBackgroundColor = 0
    private var customLoadingCircleColor = 0
    private var customDefaultText = ""
    private var customLoadingText = ""
    private var customTextColor = 0

    private var displayText = ""

    private var valueAnimator = ValueAnimator()
    private val rect = Rect()
    private var progressArc = RectF()
    private var progressIndicator = 0

    init {
        isClickable = true
        this.context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            customBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, Color.CYAN)
            customDefaultText = getString(R.styleable.LoadingButton_text) ?: "-"
            customLoadingText = getString(R.styleable.LoadingButton_downloadingText) ?: "Loading"
            customLoadingCircleColor = getColor(R.styleable.LoadingButton_circleColor, Color.RED)
            customTextColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            displayText = customDefaultText
        }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buildLoadingAnimation()
            }
            ButtonState.Completed -> {
                buildCompletedAnimation()
            }
            ButtonState.Clicked -> {
                buildClickedAnimation()
            }
        }
    }

    fun load(){
        buttonState = ButtonState.Loading
    }

    private fun buildLoadingAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, 1000).apply {
            addUpdateListener {
                progressIndicator = animatedValue as Int
                invalidate()
            }
            duration = 20000
            doOnStart {
                displayText = customLoadingText
                this@LoadingButton.isEnabled = false
            }

            doOnEnd {
                progressIndicator = 0
                this@LoadingButton.isEnabled = true
                displayText = customDefaultText
            }
            start()
        }
    }

    private fun buildCompletedAnimation() {
        progressIndicator = 0
        this@LoadingButton.isEnabled = true
        displayText = customDefaultText
    }

    private fun buildClickedAnimation() {
        with(this){
            isEnabled = false
            buttonState = ButtonState.Loading
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create(BUTTON_TEXT_FONT, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = this.customBackgroundColor
        canvas!!.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if(buttonState == ButtonState.Loading){
            paint.color = customBackgroundColor
            val progressRect = progressIndicator / 1000f * widthSize
            canvas.drawRect(0f, 0f, progressRect, heightSize.toFloat(), paint)

            val angle = progressIndicator / 1000f * 360f
            paint.color = customLoadingCircleColor
            canvas.drawArc(progressArc, 0f, angle, true, paint)
        }

        paint.color = customTextColor
        paint.getTextBounds(displayText, 0, displayText.length, rect)
        val centerPosition = measuredHeight.toFloat() / 2 - rect.centerY()
        canvas.drawText(displayText, widthSize / 2f, centerPosition, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        progressArc = RectF(widthSize - 100f, heightSize / 2 - 25f, widthSize.toFloat() - 50f, heightSize / 2 + 25f)
    }

    fun download() {
        val fraction = valueAnimator.animatedFraction
        valueAnimator.cancel()
        valueAnimator.setCurrentFraction(fraction + 0.1f)
        valueAnimator.duration = 1000
        valueAnimator.start()
    }

    companion object {
        private const val BUTTON_TEXT_FONT = "Roboto"
    }
}