package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.doOnRepeat
import kotlin.properties.Delegates
import android.animation.Animator
import androidx.core.content.ContextCompat

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    //Box specifications
    val rectPaint = Paint().apply {
        color = ContextCompat.getColor(context,R.color.colorPrimary)
    }

    //Arc specifications
    val arcPaint = Paint().apply {
        color = ContextCompat.getColor(context,R.color.colorPrimaryDark)
    }

    //text specifications.
    val textSpec = Paint().apply {
        color = ContextCompat.getColor(context,R.color.colorPrimaryDark)
        textSize = 60F
        textAlign = Paint.Align.CENTER
    }


    // This would contain the loading background dimensions.
    private val loadingRect = Rect()
    // This is the attribute that you would use [onDraw] to draw the view
    // according to the current progress of the animation.
    private var progress = 0

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                animator()
            }
            ButtonState.Completed -> {

            }
        }
    }

    fun completedButtonState(){
        buttonState = ButtonState.Completed
    }

    private fun animator() {

        // The range is between 0 and 360, since the button arc is 360 degrees,
        // so this is a good value to easily work with.
        valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000).apply {
            // Add an update listener to update [progress] value.
            addUpdateListener {
                // Update the current progress to use it [onDraw].
                progress = it.animatedValue as Int
                //Log.i("LoadingButton", "${it.animatedValue as Int}")
                // Redraw the layout to use the new updated value of [progress].
                invalidate()
            }

            // Repeat the animation infinitely.
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            //On first repeat, change the buttonState to Completed. This will draw the standard rect.
            doOnRepeat {
                buttonState = ButtonState.Completed
                isClickable = true
                Log.i("LoadingButton", "Button status changed to $buttonState")
            }

            // Start the animation.
            start()
        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.UnClicked
        Log.i("LoadingButton", "Button status is $buttonState")
    }

    override fun performClick(): Boolean {
        super.performClick()

        isClickable = false
        buttonState = ButtonState.Loading
        Log.i("LoadingButton", "Button status changed to $buttonState")

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centreText = (textSpec.ascent() + textSpec.descent()) / 2  //Used to centre text vertically

        when (buttonState) {

            ButtonState.Loading -> {
                // Update the loading background width to be from 0 to the current progress of the button full width.
                loadingRect.set(0, 0, width * progress / 360, height)
                // Draw the loading background with the newly defined dimensions.
                canvas.drawRect(loadingRect, rectPaint)

                //Draw the arc
                canvas.drawArc(
                    (widthSize - 150f),
                    (heightSize / 2) - 30f,
                    (widthSize - 80f),
                    (heightSize / 2) + 30f,
                    0f, progress.toFloat(),
                    true,
                    arcPaint
                )

                canvas.drawText(
                    "Downloading", //Text to display
                    (width / 2).toFloat(), //Starting point of x axis. Setting at half of the width to centre.
                    ((height / 2).toFloat() - (centreText)), //Starting point of y axis. Setting at half of the height to centre.
                    textSpec //Text attributes
                )
            }

                ButtonState.Completed -> { //Draw completed rectangle and text

                    canvas.drawRect(
                        0f, // left side of the rectangle to be drawn
                        0f, // top side
                        width.toFloat(), // right side
                        height.toFloat(), // bottom side
                        rectPaint  //rect Attributes
                    )

                    canvas.drawText("Download Complete", //Text to display
                        (width / 2).toFloat(), //Starting point of x axis. Setting at half of the width to centre.
                        ((height / 2).toFloat() - (centreText)), //Starting point of y axis. Setting at half of the height to centre.
                        textSpec //Text attributes
                    )


        } else -> { //Draw default rectangle and text

                canvas.drawRect(
                    0f, // left side of the rectangle to be drawn
                    0f, // top side
                    width.toFloat(), // right side
                    height.toFloat(), // bottom side
                    rectPaint  //rect Attributes
                )

                canvas.drawText("Download", //Text to display
                    (width / 2).toFloat(), //Starting point of x axis. Setting at half of the width to centre.
                    ((height / 2).toFloat() - (centreText)), //Starting point of y axis. Setting at half of the height to centre.
                    textSpec //Text attributes
                )
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}