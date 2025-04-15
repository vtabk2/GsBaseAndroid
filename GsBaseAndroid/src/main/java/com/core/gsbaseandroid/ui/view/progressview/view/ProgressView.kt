@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.core.gsbaseandroid.ui.view.progressview.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.StringRes
import com.core.gsbaseandroid.R
import com.core.gsbaseandroid.ui.view.progressview.callback.OnProgressChangeListener
import com.core.gsbaseandroid.ui.view.progressview.callback.OnProgressClickListener
import com.core.gsbaseandroid.ui.view.progressview.utils.NO_COLOR
import com.core.gsbaseandroid.ui.view.progressview.utils.applyTextForm
import com.core.gsbaseandroid.ui.view.progressview.utils.doStartAndFinish
import com.core.gsbaseandroid.ui.view.progressview.utils.dp2Px
import com.core.gsbaseandroid.ui.view.progressview.utils.px2Sp
import com.core.gsbaseandroid.ui.view.progressview.utils.sp2Px
import com.core.gsbaseandroid.ui.view.progressview.utils.updateLayoutParams

@DslMarker
internal annotation class ProgressViewDSL

/** creates an instance of [ProgressView] by [ProgressView.Builder] using kotlin dsl. */
@JvmSynthetic
@ProgressViewDSL
inline fun progressView(
    context: Context,
    crossinline block: ProgressView.Builder.() -> Unit
): ProgressView =
    ProgressView.Builder(context).apply(block).build()

/** ProgressView is a progress bar with a flexible text and animations. */
class ProgressView : FrameLayout {

    /** presents progress value on the [ProgressView]. */
    val labelView: TextView = TextView(context)

    /** presents background color and highlighting colors of the [ProgressView]. */
    val highlightView: HighlightView = HighlightView(context)

    /** duration of the progress animation. */
    var duration: Long = 1000L

    /** returns the [ProgressView]'s animation is ongoing or not.*/
    var isAnimating: Boolean = false

    /** starts progress animation automatically when [ProgressView] is initialized. */
    var autoAnimate: Boolean = true

    /** minimum value of the progress. */
    var min: Float = 0f

    /** maximum value of the progress. */
    var max: Float = 100f
        set(value) {
            field = value
            updateProgressView()
        }

    /** a field for holding previous progressed value. */
    private var previousProgress: Float = 0f

    /** starts progress animation from the [previousProgress] to a new progress value.  */
    var progressFromPrevious: Boolean = false
        set(value) {
            field = value
            previousProgress = 0f
        }

    /** presents the progress value of the [ProgressView]. */
    var progress: Float = 0f
        set(value) {
            if (progressFromPrevious) {
                previousProgress = field
            }
            field = when {
                value >= max -> max
                value <= min -> min
                else -> value
            }
            updateProgressView()
            onProgressChangeListener?.onChange(field)
        }

    /**
     * a provided customized progress animation.
     * [ProgressViewAnimation.NORMAL], [ProgressViewAnimation.BOUNCE], [ProgressViewAnimation.DECELERATE],
     * [ProgressViewAnimation.ACCELERATEDECELERATE]
     * the default animation is [ProgressViewAnimation.NORMAL].
     */
    var progressAnimation: ProgressViewAnimation = ProgressViewAnimation.NORMAL

    /** a customized animation interpolator. */
    var interpolator: Interpolator? = null

    /**
     * an orientation of the [ProgressView].
     * [ProgressViewOrientation.HORIZONTAL], [ProgressViewOrientation.VERTICAL]
     * the default orientation is [ProgressViewOrientation.HORIZONTAL].
     * */
    var orientation: ProgressViewOrientation = ProgressViewOrientation.HORIZONTAL
        set(value) {
            field = value
            highlightView.orientation = value
            updateProgressView()
        }

    /** background color of the [ProgressView]'s container. */
    @ColorInt
    var colorBackground: Int = Color.WHITE
        set(value) {
            field = value
            updateBackground()
        }

    /** corner radius of the [ProgressView]'s container. */
    @Px
    var radius: Float = dp2Px(5).toFloat()
        set(value) {
            field = value
            highlightView.radius = value
            updateBackground()
        }

    var radiusArray: FloatArray? = null
        set(value) {
            field = value
            highlightView.radiusArray = value
            updateBackground()
        }

    /** a border color of the [ProgressView]'s container. */
    @ColorInt
    var borderColor: Int = colorBackground
        set(value) {
            field = value
            updateBackground()
        }

    /** a border size of the [ProgressView]'s container. */
    @Px
    var borderWidth: Int = 0
        set(value) {
            field = value
            updateBackground()
        }

    /** text of the [labelView] for presenting progress. */
    var labelText: CharSequence? = ""
        set(value) {
            field = value
            updateProgressView()
        }

    /** text size of the [labelView]. */
    @Px
    var labelSize: Float = 12f
        set(value) {
            field = value
            updateProgressView()
        }

    /**
     * text color of the [labelView] when the label is located inside of the progressed container.
     * when your [labelText]'s length is shorter than the progressed container,
     * the [labelView] will be located inside of the progressed container.
     */
    @ColorInt
    var labelColorInner: Int = Color.WHITE
        set(value) {
            field = value
            updateProgressView()
        }

    /**
     * text color of the [labelView] when the label is located outside of the progressed container.
     * when your [labelText]'s length is longer than the progressed container,
     * the [labelView] will be located outside of the progressed container.
     */
    @ColorInt
    var labelColorOuter: Int = Color.BLACK
        set(value) {
            field = value
            updateProgressView()
        }

    /** typeface of the [labelView]. */
    var labelTypeface: Int = Typeface.NORMAL
        set(value) {
            field = value
            updateProgressView()
        }

    /** typeface object of the [labelView]. */
    var labelTypefaceObject: Typeface? = null
        set(value) {
            field = value
            updateProgressView()
        }

    /** determines the constraints of the label positioning. */
    var labelConstraints: ProgressLabelConstraints = ProgressLabelConstraints.ALIGN_PROGRESS
        set(value) {
            field = value
            updateProgressView()
        }

    /** the gravity of the label. */
    var labelGravity: Int? = Gravity.CENTER
        set(value) {
            field = value
            updateProgressView()
        }

    /**
     * spacing for [labelView] between progressed container.
     * space will be applied if the labelView is located inside or outside.
     */
    @Px
    var labelSpace: Float = dp2Px(8).toFloat()
        set(value) {
            field = value
            updateProgressView()
        }

    var labelTextAllCaps: Boolean = false
        set(value) {
            field = value
            updateProgressView()
        }

    var labelTextCenter: Boolean = false
        set(value) {
            field = value
            updateProgressView()
        }

    /** interface for listening to the progress is changed. */
    private var onProgressChangeListener: OnProgressChangeListener? = null

    /** interface for listening to the progress bar is clicked. */
    private var onProgressClickListener: OnProgressClickListener? = null

    /** path for smoothing the container's corner. */
    private val path = Path()

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        attributeSet: AttributeSet
    ) : this(context, attributeSet, 0)

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyle: Int
    ) : super(
        context,
        attributeSet,
        defStyle
    ) {
        getAttrs(attributeSet, defStyle)
    }

    private fun getAttrs(
        attributeSet: AttributeSet,
        defStyleAttr: Int
    ) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ProgressView, defStyleAttr, 0)
        try {
            setTypeArray(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun setTypeArray(a: TypedArray) {
        this.labelText = a.getString(R.styleable.ProgressView_progressView_labelText)
        this.labelSize =
            px2Sp(a.getDimension(R.styleable.ProgressView_progressView_labelSize, labelSize))
        this.labelSpace =
            a.getDimension(R.styleable.ProgressView_progressView_labelSpace, labelSpace)
        this.labelColorInner =
            a.getColor(R.styleable.ProgressView_progressView_labelColorInner, labelColorInner)
        this.labelColorOuter =
            a.getColor(R.styleable.ProgressView_progressView_labelColorOuter, labelColorOuter)
        this.labelTypeface =
            when (a.getInt(R.styleable.ProgressView_progressView_labelTypeface, Typeface.NORMAL)) {
                1 -> Typeface.BOLD
                2 -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
        this.labelTextAllCaps =
            a.getBoolean(R.styleable.ProgressView_progressView_labelTextAllCaps, labelTextAllCaps)
        this.labelTextCenter =
            a.getBoolean(R.styleable.ProgressView_progressView_labelTextCenter, labelTextCenter)
        this.labelConstraints =
            when (
                a.getInt(
                    R.styleable.ProgressView_progressView_labelConstraints,
                    ProgressLabelConstraints.ALIGN_PROGRESS.ordinal
                )
            ) {
                1 -> ProgressLabelConstraints.ALIGN_CONTAINER
                else -> ProgressLabelConstraints.ALIGN_PROGRESS
            }
        when (
            a.getInt(
                R.styleable.ProgressView_progressView_orientation,
                ProgressViewOrientation.HORIZONTAL.value
            )
        ) {
            0 -> this.orientation = ProgressViewOrientation.HORIZONTAL
            1 -> this.orientation = ProgressViewOrientation.VERTICAL
        }
        when (
            a.getInt(
                R.styleable.ProgressView_progressView_animation,
                progressAnimation.value
            )
        ) {
            ProgressViewAnimation.NORMAL.value -> this.progressAnimation =
                ProgressViewAnimation.NORMAL

            ProgressViewAnimation.BOUNCE.value -> this.progressAnimation =
                ProgressViewAnimation.BOUNCE

            ProgressViewAnimation.DECELERATE.value -> this.progressAnimation =
                ProgressViewAnimation.DECELERATE

            ProgressViewAnimation.ACCELERATEDECELERATE.value -> this.progressAnimation =
                ProgressViewAnimation.ACCELERATEDECELERATE
        }
        this.min = a.getFloat(R.styleable.ProgressView_progressView_min, min)
        this.max = a.getFloat(R.styleable.ProgressView_progressView_max, max)
        this.progress = a.getFloat(R.styleable.ProgressView_progressView_progress, progress)
        this.radius = a.getDimension(R.styleable.ProgressView_progressView_radius, radius)
        this.duration =
            a.getInteger(R.styleable.ProgressView_progressView_duration, duration.toInt())
                .toLong()
        this.colorBackground =
            a.getColor(R.styleable.ProgressView_progressView_colorBackground, colorBackground)
        this.borderColor =
            a.getColor(R.styleable.ProgressView_progressView_borderColor, borderColor)
        this.borderWidth =
            a.getDimensionPixelSize(R.styleable.ProgressView_progressView_borderWidth, borderWidth)
        this.autoAnimate =
            a.getBoolean(R.styleable.ProgressView_progressView_autoAnimate, autoAnimate)
        this.progressFromPrevious =
            a.getBoolean(
                R.styleable.ProgressView_progressView_progressFromPrevious,
                progressFromPrevious
            )

        with(highlightView) {
            alpha = a.getFloat(R.styleable.ProgressView_progressView_highlightAlpha, highlightAlpha)
            color = a.getColor(R.styleable.ProgressView_progressView_colorProgress, color)
            colorGradientStart =
                a.getColor(R.styleable.ProgressView_progressView_colorGradientStart, NO_COLOR)
            colorGradientCenter =
                a.getColor(R.styleable.ProgressView_progressView_colorGradientCenter, NO_COLOR)
            colorGradientEnd =
                a.getColor(R.styleable.ProgressView_progressView_colorGradientEnd, NO_COLOR)
            radius = this@ProgressView.radius
            radiusArray = this@ProgressView.radiusArray
            padding =
                a.getDimension(R.styleable.ProgressView_progressView_padding, borderWidth.toFloat())
                    .toInt()
            highlightColor =
                a.getColor(R.styleable.ProgressView_progressView_highlightColor, highlightColor)
            highlightThickness = a.getDimension(
                R.styleable.ProgressView_progressView_highlightWidth,
                highlightThickness.toFloat()
            )
                .toInt()
            if (!a.getBoolean(R.styleable.ProgressView_progressView_highlighting, !highlighting)) {
                highlightThickness = 0
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updateProgressView()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed && orientation == ProgressViewOrientation.VERTICAL) {
            rotation = 180f
            labelView.rotation = 180f
        }
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.path.apply {
            reset()
            val radiusArray =
                radiusArray ?: floatArrayOf(
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius
                )
            addRoundRect(
                RectF(0f, 0f, w.toFloat(), h.toFloat()),
                radiusArray,
                Path.Direction.CCW
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(this.path)
        super.dispatchDraw(canvas)
    }

    private fun updateProgressView() {
        post {
            updateHighlightView()
            updateLabel()
            autoAnimate()
        }
    }

    private fun updateBackground() {
        background = GradientDrawable().apply {
            if (radiusArray != null) {
                cornerRadii = radiusArray
            } else {
                cornerRadius = radius
            }
            setColor(colorBackground)
            setStroke(borderWidth, borderColor)
        }
    }

    private fun updateHighlightView() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        if (max <= progress) {
            if (isVertical()) {
                params.height = getViewSize(this)
            } else {
                params.width = getViewSize(this)
            }
        } else {
            if (isVertical()) {
                params.height = getProgressSize().toInt()
            } else {
                params.width = getProgressSize().toInt()
            }
        }
        this.highlightView.layoutParams = params
        this.highlightView.updateHighlightView()
        removeView(highlightView)
        addView(highlightView)
    }

    private fun updateLabel() {
        if (labelText == null) {
            return
        }
        if (labelGravity != null) {
            this.labelView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.labelView.gravity = requireNotNull(labelGravity)
        } else if (!isVertical()) {
            this.labelView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.labelView.gravity = Gravity.CENTER_VERTICAL
        } else {
            this.labelView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            this.labelView.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        applyTextForm(
            textForm(context) {
                text = labelText
                textSize = labelSize
                textTypeface = labelTypeface
                textTypefaceObject = labelTypefaceObject
                textAllCaps = labelTextAllCaps
            }
        )
        removeView(labelView)
        addView(labelView)

        post {
            if (labelView.width == 0) return@post
            if (labelTextCenter) {
                setLabelViewPosition((measuredWidth - this.labelView.width - this.labelSpace) / 2f) {
                    this.labelView.setTextColor(labelColorOuter)
                }
            } else {
                when {
                    this.labelView.width + labelSpace < getProgressSize() -> {
                        setLabelViewPosition(getProgressSize() - this.labelView.width - this.labelSpace) {
                            this.labelView.setTextColor(labelColorInner)
                        }
                    }

                    else -> {
                        setLabelViewPosition(getProgressSize() + this.labelSpace) {
                            this.labelView.setTextColor(labelColorOuter)
                        }
                    }
                }
            }
        }
    }

    private inline fun setLabelViewPosition(position: Float, crossinline action: () -> Unit = {}) {
//        if (position < 0) return
        if (this.labelConstraints == ProgressLabelConstraints.ALIGN_PROGRESS) {
            action()
            if (isVertical()) {
                labelView.y = position
            } else {
                labelView.x = position
            }
        }
    }

    private fun getProgressSize(progressValue: Float = progress): Float {
        return (getViewSize(this) / max) * progressValue
    }

    private fun getPreviousMergedProgressSize(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) progressRange: Float
    ): Float {
        return if (getProgressSize(previousProgress) +
            getProgressSize() * progressRange <= getProgressSize()
        ) {
            getProgressSize(previousProgress) + getProgressSize() * progressRange
        } else {
            getProgressSize()
        }
    }

    private fun getLabelPosition(progressValue: Float = progress): Float {
        return when {
            labelView.width + labelSpace < getProgressSize(progressValue) -> getProgressSize(
                progressValue
            ) - labelView.width - labelSpace

            else -> getProgressSize(progressValue) + labelSpace
        }
    }

    private fun getPreviousMergedLabelPosition(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) progressRange: Float
    ): Float {
        return if (getLabelPosition(previousProgress) +
            getLabelPosition() * progressRange <= getLabelPosition()
        ) {
            getLabelPosition(previousProgress) + getLabelPosition() * progressRange
        } else {
            getLabelPosition()
        }
    }

    private fun autoAnimate() {
        if (this.autoAnimate) {
            progressAnimate()
        }
    }

    private fun getViewSize(view: View): Int {
        return if (isVertical()) view.height
        else view.width
    }

    /** animates [ProgressView]'s progress bar. */
    fun progressAnimate() {
        ValueAnimator.ofFloat(0f, 1f)
            .apply {
                interpolator = if (this@ProgressView.interpolator != null) {
                    this@ProgressView.interpolator
                } else {
                    progressAnimation.getInterpolator()
                }
                duration = this@ProgressView.duration
                addUpdateListener {
                    val value = it.animatedValue as Float
                    setLabelViewPosition(getPreviousMergedLabelPosition(value))
                    highlightView.updateLayoutParams {
                        if (isVertical()) {
                            height = getPreviousMergedProgressSize(value).toInt()
                        } else {
                            width = getPreviousMergedProgressSize(value).toInt()
                        }
                    }
                }
                doStartAndFinish(
                    start = { isAnimating = true },
                    finish = { isAnimating = false }
                )
            }
            .also { it.start() }
    }

    fun isVertical(): Boolean {
        return orientation == ProgressViewOrientation.VERTICAL
    }

    fun isProgressedMax(): Boolean {
        return progress == max
    }

    /** sets a progress change listener. */
    fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener
    }

    /** sets a progress change listener. */
    @JvmSynthetic
    fun setOnProgressChangeListener(block: (Float) -> Unit) {
        this.onProgressChangeListener = OnProgressChangeListener { progress -> block(progress) }
    }

    /** sets a progress click listener. */
    fun setOnProgressClickListener(onProgressClickListener: OnProgressClickListener) {
        this.onProgressClickListener = onProgressClickListener
        this.highlightView.onProgressClickListener = this.onProgressClickListener
    }

    /** sets a progress click listener. */
    @JvmSynthetic
    fun setOnProgressClickListener(block: (Boolean) -> Unit) {
        this.onProgressClickListener =
            OnProgressClickListener { highlighting -> block(highlighting) }
        this.highlightView.onProgressClickListener = this.onProgressClickListener
    }

    /** applies [TextForm] attributes to a TextView. */
    fun applyTextForm(textForm: TextForm) {
        this.labelView.applyTextForm(textForm)
    }

    /** Builder class for creating [ProgressView]. */
    @ProgressViewDSL
    class Builder(context: Context) {
        private val progressView = ProgressView(context)

        fun setSize(@Px width: Int, @Px height: Int): Builder = apply {
            this.progressView.layoutParams =
                LayoutParams(progressView.dp2Px(width), progressView.dp2Px(height))
        }

        fun setHeight(@Px value: Int): Builder =
            apply { this.progressView.layoutParams.height = value }

        fun setDuration(value: Long): Builder = apply { this.progressView.duration = value }
        fun setAutoAnimate(value: Boolean): Builder =
            apply { this.progressView.autoAnimate = value }

        fun setMin(value: Float): Builder = apply { this.progressView.min = value }
        fun setMax(value: Float): Builder = apply { this.progressView.max = value }
        fun setProgress(value: Float): Builder = apply { this.progressView.progress = value }
        fun setOrientation(value: ProgressViewOrientation): Builder = apply {
            this.progressView.orientation = value
        }

        fun setColorBackground(@ColorInt value: Int): Builder = apply {
            this.progressView.colorBackground = value
        }

        fun setRadius(@Px value: Float): Builder = apply { this.progressView.radius = value }
        fun setRadii(value: FloatArray): Builder =
            apply { this.progressView.radiusArray = value }

        fun setLabelText(value: CharSequence): Builder =
            apply { this.progressView.labelText = value }

        fun setLabelTextResource(@StringRes value: Int): Builder = apply {
            setLabelText(progressView.context.getString(value))
        }

        fun setLabelSize(value: Float): Builder = apply {
            this.progressView.labelSize = this.progressView.sp2Px(value)
        }

        fun setLabelSpace(@Px value: Float): Builder =
            apply { this.progressView.labelSpace = value }

        fun setLabelColorInner(@ColorInt value: Int): Builder = apply {
            this.progressView.labelColorInner = value
        }

        fun setLabelColorOuter(@ColorInt value: Int): Builder = apply {
            this.progressView.labelColorOuter = value
        }

        fun setLabelTypeface(value: Int): Builder = apply {
            this.progressView.labelTypeface = value
        }

        fun setLabelTypeface(value: Typeface): Builder = apply {
            this.progressView.labelTypefaceObject = value
        }

        fun setLabelTextAllCaps(value: Boolean): Builder = apply {
            this.progressView.labelTextAllCaps = value
        }

        fun setLabelGravity(value: Int): Builder = apply {
            this.progressView.labelGravity = value
        }

        fun setLabelConstraints(value: ProgressLabelConstraints): Builder = apply {
            this.progressView.labelConstraints = value
        }

        fun setProgressbarAlpha(@FloatRange(from = 0.0, to = 1.0) value: Float): Builder =
            apply {
                this.progressView.highlightView.alpha = value
            }

        fun setProgressbarColor(@ColorInt value: Int): Builder = apply {
            this.progressView.highlightView.color = value
        }

        fun setProgressbarColorGradientStart(@ColorInt value: Int): Builder = apply {
            this.progressView.highlightView.colorGradientStart = value
        }

        fun setProgressbarColorGradientCenter(@ColorInt value: Int): Builder = apply {
            this.progressView.highlightView.colorGradientCenter = value
        }

        fun setProgressbarColorGradientEnd(@ColorInt value: Int): Builder = apply {
            this.progressView.highlightView.colorGradientEnd = value
        }

        fun setProgressbarRadius(@Px value: Float): Builder = apply {
            this.progressView.highlightView.radius = value
        }

        fun setProgressbarRadii(value: FloatArray): Builder = apply {
            this.progressView.highlightView.radiusArray = value
        }

        fun setHighlightColor(@ColorInt value: Int): Builder = apply {
            this.progressView.highlightView.highlightColor = value
        }

        fun setHighlighting(value: Boolean): Builder = apply {
            this.progressView.highlightView.highlighting = value
        }

        fun setHighlightThickness(@Px value: Int): Builder = apply {
            this.progressView.highlightView.highlightThickness = value
        }

        fun setOnProgressChangeListener(value: OnProgressChangeListener): Builder = apply {
            this.progressView.onProgressChangeListener = value
        }

        fun setProgressViewAnimation(value: ProgressViewAnimation): Builder = apply {
            this.progressView.progressAnimation = value
        }

        fun setInterpolator(value: Interpolator?): Builder = apply {
            this.progressView.interpolator = value
        }

        fun setOnProgressClickListener(value: OnProgressClickListener): Builder = apply {
            this.progressView.onProgressClickListener = value
        }

        fun setTextForm(value: TextForm): Builder = apply {
            this.progressView.labelView.applyTextForm(value)
        }

        @JvmSynthetic
        fun setOnProgressChangeListener(block: (Float) -> Unit): Builder = apply {
            this.progressView.onProgressChangeListener =
                OnProgressChangeListener { progress -> block(progress) }
        }

        @JvmSynthetic
        fun setOnProgressClickListener(block: (Boolean) -> Unit): Builder = apply {
            this.progressView.onProgressClickListener =
                OnProgressClickListener { highlighting -> block(highlighting) }
        }

        fun build(): ProgressView = progressView
    }
}
