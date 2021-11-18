package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates
import android.graphics.Typeface




class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    // This would contain the loading background dimensions.
    private val loadingRect = Rect()
    // This is the attribute that you would use [onDraw] to draw the view
    // according to the current progress of the animation.
    private var progress = 0

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

//        when (new) {
//            ButtonState.Loading -> {
////                // Start the animation.
////
////                // The range is between 0 and 360, since the button arc is 360 degrees,
////                // so this is a good value to easily work with.
////                valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000).apply {
////                    // Add an update listener to update [progress] value.
////                    addUpdateListener {
////                        // Update the current progress to use it [onDraw].
////                        progress = it.animatedValue as Int
////                        // Redraw the layout to use the new updated value of [progress].
////                        invalidate()
////                    }
////                    // Repeat the animation infinitely.
////                    repeatCount = ValueAnimator.INFINITE
////                    repeatMode = ValueAnimator.RESTART
////                    // Start the animation.
////                    start()
//                }
//
//            }
//            ButtonState.Completed -> {
//                // Cancel the animation.
//            }
//        }


    }

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true


        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        val footingpaint = Paint ()
//        footingpaint.setARGB (100, 100, 128, 10)
////
////        canvas?.drawRect(((width/2)-10).toFloat(), 0f, ((width/2)+ 10) .toFloat (), 40f, footingpaint)
////
////
//
//        // Update the loading background width to be from 0 to the current progress of the button full width.
//        loadingRect.set(0, 0, width * progress / 360, height)
//        // Draw the loading background with the newly defined dimensions.
//        canvas?.drawRect(loadingRect, footingpaint)

        val footingpaint = Paint ()
        footingpaint.setARGB (255, 128, 128, 128)

        //canvas.drawRect (70f, 40f, (width - 70) .toFloat (), 80f, footingpaint)

        // draw rectangle on canvas
        canvas.drawRect(
            0f, // left side of the rectangle to be drawn
            0f, // top side
            width.toFloat(), // right side
            height.toFloat(), // bottom side
            footingpaint
        )

        val textSpec = Paint ()
        textSpec.color = Color.WHITE
        textSpec.textSize = 70F
        textSpec.textAlign = Paint.Align.CENTER
        val centreText = (textSpec.ascent() + textSpec.descent()) / 2  //Used to centre text vertically

        canvas.drawText("Download", //Text to display
            (width / 2).toFloat(), //Starting point of x axis. Setting at half of the width to centre.
            ((height / 2).toFloat() - (centreText)), //Starting point of y axis. Setting at half of the height to centre.
            textSpec //Text attributes
        )



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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }



}