package com.core.gsbaseandroid.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.core.gsbaseandroid.ui.control.OnSizeChangedListener
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

open class ZoomConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var layoutMatrix = Matrix()
    private var tmpMatrix = Matrix()
    private val savedMatrix: Matrix = Matrix()
    private val invertMatrix: Matrix = Matrix()

    private var mode = State.NONE

    // remember some things for zooming
    private val start: PointF = PointF()
    private val mid: PointF = PointF()
    private val midRotate: PointF = PointF()
    private var oldDist = 1f
    private var d = 0f
    private var newRot = 0f
    private var lastEvent: FloatArray? = null
    private var currentScale = 1f

    private val tmpFloatRect = RectF()
    private val tmpPointArray = FloatArray(2)
    private val tmpMatrixArray = FloatArray(9)
    private var currentMotionEvent: MotionEvent? = null

    var minimumScale = 0.5f
    var maximumScale = 10.0f

    var isLimitDrag = true
    var enableTouch = false

    var onSizeChangedListener: OnSizeChangedListener? = null

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = DecelerateInterpolator()
    }
    private val duration = 300

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (width == 0 || height == 0 || oldWidth == 0 || oldHeight == 0) return
        if (abs(width - oldWidth) <= 2 && abs(height - oldHeight) <= 2) {
            return
        }
        onSizeChangedListener?.onSizeChanged(width, height, oldWidth, oldHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.concat(layoutMatrix)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun invalidateChildInParent(location: IntArray?, dirty: Rect?): ViewParent? {
        applyMatrix(dirty, layoutMatrix)
        return super.invalidateChildInParent(location, dirty)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        currentMotionEvent = event
        // We should remap given event back to original coordinates
        // so that children can correctly respond to it
        layoutMatrix.invert(invertMatrix)
        val invertedEvent: MotionEvent? = applyMatrix(event, invertMatrix)
        return try {
            super.dispatchTouchEvent(invertedEvent)
        } finally {
            invertedEvent?.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (currentMotionEvent == null) return false
        if (!enableTouch) return false
        // handle touch events here
        return currentMotionEvent?.let { ev ->
            when (ev.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(layoutMatrix)
                    start.set(ev.x, ev.y)
                    mode = State.DRAG
                    lastEvent = null
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(ev)
                    if (oldDist > 10f) {
                        savedMatrix.set(layoutMatrix)
                        midPoint(mid, ev)
                        mode = State.ZOOM
                    }
                    lastEvent = FloatArray(4)
                    lastEvent?.set(0, ev.getX(0))
                    lastEvent?.set(1, ev.getX(1))
                    lastEvent?.set(2, ev.getY(0))
                    lastEvent?.set(3, ev.getY(1))
                    d = rotation(ev)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = State.NONE
                    lastEvent = null
                }

                MotionEvent.ACTION_MOVE -> if (mode == State.DRAG) {
                    layoutMatrix.set(savedMatrix)
                    var dx: Float = ev.x - start.x
                    var dy: Float = ev.y - start.y
                    if (isLimitDrag) {
                        val mid = PointF()
                        midDiagonalPoint(mid)
                        if (mid.x + dx < 0) {
                            dx = 0 - mid.x
                        } else if (mid.x + dx > width) {
                            dx = width - mid.x
                        }
                        if (mid.y + dy < 0) {
                            dy = 0 - mid.y
                        } else if (mid.y + dy > height) {
                            dy = height - mid.y
                        }
                    }
                    layoutMatrix.postTranslate(dx, dy)
                } else if (mode == State.ZOOM) {
                    val newDist: Float = spacing(ev)
                    if (newDist > 10f) {
                        layoutMatrix.set(savedMatrix)
                        val scale: Float = newDist / oldDist
                        layoutMatrix.postScale(scale, scale, mid.x, mid.y)

                        val vals = FloatArray(9)
                        layoutMatrix.getValues(vals)

                        currentScale = getScale(layoutMatrix)

                        if (currentScale <= minimumScale) {
                            layoutMatrix.postScale(
                                minimumScale / currentScale, minimumScale / currentScale, mid.x, mid.y
                            )
                        } else if (currentScale >= maximumScale) {
                            layoutMatrix.postScale(
                                maximumScale / currentScale, maximumScale / currentScale, mid.x, mid.y
                            )
                        }
                    }
                    if (lastEvent != null && ev.pointerCount == 2) {
                        newRot = rotation(ev)
                        val r: Float = newRot - d
                        val values = FloatArray(9)
                        layoutMatrix.getValues(values)
                        midDiagonalPoint(midRotate)
                        layoutMatrix.postRotate(r, midRotate.x, midRotate.y)
                    }
                }
            }

            invalidate()
            return true
        } ?: false
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        val s = x * x + y * y
        return sqrt(s.toDouble()).toFloat()
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private fun rotation(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(deltaY, deltaX)
        return Math.toDegrees(radians).toFloat()
    }

    private fun midDiagonalPoint(paramPointF: PointF) {
        val childView = getChildAt(0) ?: return
        val arrayOfFloat = FloatArray(9)
        this.layoutMatrix.getValues(arrayOfFloat)
        val f1 = 0.0f * arrayOfFloat[Matrix.MSCALE_X] + 0.0f * arrayOfFloat[Matrix.MSKEW_X] + arrayOfFloat[Matrix.MTRANS_X]
        val f2 = 0.0f * arrayOfFloat[Matrix.MSKEW_Y] + 0.0f * arrayOfFloat[Matrix.MSCALE_Y] + arrayOfFloat[Matrix.MTRANS_Y]
        val f3 = arrayOfFloat[Matrix.MSCALE_X] * childView.width + arrayOfFloat[Matrix.MSKEW_X] * childView.height + arrayOfFloat[Matrix.MTRANS_X]
        val f4 = arrayOfFloat[Matrix.MSKEW_Y] * childView.width + arrayOfFloat[Matrix.MSCALE_Y] * childView.height + arrayOfFloat[Matrix.MTRANS_Y]
        val f5 = f1 + f3
        val f6 = f2 + f4
        paramPointF.set(f5 / 2.0f, f6 / 2.0f)
    }

    private fun getScale(matrix: Matrix): Float {
        val v = FloatArray(9)
        matrix.getValues(v)
        // calculate real scale
        val scaleX = v[Matrix.MSCALE_X]
        val skewY = v[Matrix.MSKEW_Y]
        return sqrt(scaleX * scaleX + skewY * skewY.toDouble()).toFloat()
    }

    private fun applyMatrix(rect: Rect?, matrix: Matrix) {
        if (rect == null) return
        tmpFloatRect.set(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
        matrix.mapRect(tmpFloatRect)
        rect.set(tmpFloatRect.left.roundToInt(), tmpFloatRect.top.roundToInt(), tmpFloatRect.right.roundToInt(), tmpFloatRect.bottom.roundToInt())
    }

    private fun applyMatrix(event: MotionEvent?, matrix: Matrix): MotionEvent? {
        if (event == null) return null
        tmpPointArray[0] = event.x
        tmpPointArray[1] = event.y
        matrix.mapPoints(tmpPointArray)
        val copy = MotionEvent.obtain(event)
        copy.setLocation(tmpPointArray[0], tmpPointArray[1])
        return copy
    }

    fun reset() {
        layoutMatrix.reset()
        invalidate()
    }

    fun scaleToAndTranslateToWithAnimation(endScale: Float, x: Float, y: Float, quick: Boolean = false) {
        animator.removeAllUpdateListeners()
        animator.end()
        val startScale = getScale(layoutMatrix)
        val startTranslate = getTranslate(layoutMatrix)
        val endTranslate = PointF(x, y)
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            val scaleNew: Float = (startScale + (endScale - startScale) * value) / getScale(layoutMatrix)
            layoutMatrix.postScale(scaleNew, scaleNew)
            val currentTranslate = getTranslate(layoutMatrix)
            val dx: Float = (startTranslate.x + (endTranslate.x - startTranslate.x) * value) - currentTranslate.x
            val dy: Float = (startTranslate.y + (endTranslate.y - startTranslate.y) * value) - currentTranslate.y
            layoutMatrix.postTranslate(dx, dy)
            invalidate()
        }
        if (quick) {
            animator.duration = 0
        } else {
            animator.duration = duration.toLong()
        }
        animator.start()
    }

    private fun getTranslate(matrix: Matrix): PointF {
        matrix.getValues(tmpMatrixArray)
        val tx = tmpMatrixArray[Matrix.MTRANS_X]
        val ty = tmpMatrixArray[Matrix.MTRANS_Y]
        return PointF(tx, ty)
    }

    fun getCurrentRectInScale(endScale: Float = -1f): RectF {
        val currentRect = RectF()
        if (endScale == -1f) {
            layoutMatrix.getValues(tmpMatrixArray)
        } else {
            tmpMatrix.set(layoutMatrix)
            val scaleBy = endScale / getScale(tmpMatrix)
            tmpMatrix.postScale(scaleBy, scaleBy)
            tmpMatrix.getValues(tmpMatrixArray)
        }
        val left = 0.0f * tmpMatrixArray[Matrix.MSCALE_X] + 0.0f * tmpMatrixArray[Matrix.MSKEW_X] + tmpMatrixArray[Matrix.MTRANS_X]
        val top = 0.0f * tmpMatrixArray[Matrix.MSKEW_Y] + 0.0f * tmpMatrixArray[Matrix.MSCALE_Y] + tmpMatrixArray[Matrix.MTRANS_Y]
        val right = tmpMatrixArray[Matrix.MSCALE_X] * width + tmpMatrixArray[Matrix.MSKEW_X] * height + tmpMatrixArray[Matrix.MTRANS_X]
        val bottom = tmpMatrixArray[Matrix.MSKEW_Y] * width + tmpMatrixArray[Matrix.MSCALE_Y] * height + tmpMatrixArray[Matrix.MTRANS_Y]
        currentRect.left = left
        currentRect.top = top
        currentRect.right = right
        currentRect.bottom = bottom
        return currentRect
    }

    enum class State {
        NONE, DRAG, ZOOM
    }
}