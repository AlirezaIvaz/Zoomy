package ir.alirezaivaz.zoomy

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewParent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import ir.alirezaivaz.zoomy.MotionUtils.midPointOfEvent
import ir.alirezaivaz.zoomy.ViewUtils.getBitmapFromView
import ir.alirezaivaz.zoomy.ViewUtils.getViewAbsoluteCords

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */
internal class ZoomableTouchListener(
    private val mTargetContainer: TargetContainer,
    private val mTarget: View,
    private val mConfig: ZoomyConfig,
    interpolator: Interpolator?,
    private val mZoomListener: ZoomListener?,
    private val mTapListener: TapListener?,
    private val mLongPressListener: LongPressListener?,
    private val mDoubleTapListener: DoubleTapListener?
) : OnTouchListener, OnScaleGestureListener {
    private var mState = STATE_IDLE
    private var mZoomableView: ImageView? = null
    private var mShadow: View? = null
    private val mScaleGestureDetector: ScaleGestureDetector
    private val mGestureDetector: GestureDetector
    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            mTapListener?.onTap(mTarget)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            mLongPressListener?.onLongPress(mTarget)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            mDoubleTapListener?.onDoubleTap(mTarget)
            return true
        }
    }
    private var mScaleFactor = 1f
    private var mCurrentMovementMidPoint = PointF()
    private var mInitialPinchMidPoint = PointF()
    private var mTargetViewCords = Point()
    private var mAnimatingZoomEnding = false
    private val mEndZoomingInterpolator: Interpolator
    private val mEndingZoomAction = Runnable {
        removeFromDecorView(mShadow)
        removeFromDecorView(mZoomableView)
        mTarget.visibility = View.VISIBLE
        mZoomableView = null
        mCurrentMovementMidPoint = PointF()
        mInitialPinchMidPoint = PointF()
        mAnimatingZoomEnding = false
        mState = STATE_IDLE
        mZoomListener?.onViewEndedZooming(mTarget)
        if (mConfig.isImmersiveModeEnabled) showSystemUI()
    }

    init {
        mEndZoomingInterpolator = interpolator ?: AccelerateDecelerateInterpolator()
        mScaleGestureDetector = ScaleGestureDetector(mTarget.context, this)
        mGestureDetector = GestureDetector(mTarget.context, mGestureListener)
    }

    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        if (mAnimatingZoomEnding || ev.pointerCount > 2) return true
        mScaleGestureDetector.onTouchEvent(ev)
        mGestureDetector.onTouchEvent(ev)
        val action = ev.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> when (mState) {
                STATE_IDLE -> mState = STATE_POINTER_DOWN
                STATE_POINTER_DOWN -> {
                    mState = STATE_ZOOMING
                    midPointOfEvent(mInitialPinchMidPoint, ev)
                    startZoomingView(mTarget)
                }
            }

            MotionEvent.ACTION_MOVE -> if (mState == STATE_ZOOMING) {
                midPointOfEvent(mCurrentMovementMidPoint, ev)
                //because our initial pinch could be performed in any of the view edges,
                //we need to substract this difference and add system bars height
                //as an offset to avoid an initial transition jump
                mCurrentMovementMidPoint.x -= mInitialPinchMidPoint.x
                mCurrentMovementMidPoint.y -= mInitialPinchMidPoint.y
                //because previous function returns the midpoint for relative X,Y coords,
                //we need to add absolute view coords in order to ensure the correct position
                mCurrentMovementMidPoint.x += mTargetViewCords.x.toFloat()
                mCurrentMovementMidPoint.y += mTargetViewCords.y.toFloat()
                val x = mCurrentMovementMidPoint.x
                val y = mCurrentMovementMidPoint.y
                mZoomableView!!.x = x
                mZoomableView!!.y = y
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> when (mState) {
                STATE_ZOOMING -> endZoomingView()
                STATE_POINTER_DOWN -> mState = STATE_IDLE
            }
        }
        return true
    }

    private fun endZoomingView() {
        if (mConfig.isZoomAnimationEnabled) {
            mAnimatingZoomEnding = true
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener(object : AnimatorUpdateListener {
                var xStart = mZoomableView!!.x
                var yStart = mZoomableView!!.y
                var scaleStart = mZoomableView!!.scaleX
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    mZoomableView!!.x =
                        xStart + animation.animatedFraction * (mTargetViewCords.x - xStart)
                    mZoomableView!!.y =
                        yStart + animation.animatedFraction * (mTargetViewCords.y - yStart)
                    mScaleFactor = scaleStart + animation.animatedFraction * (1f - scaleStart)
                    mZoomableView!!.scaleX = mScaleFactor
                    mZoomableView!!.scaleY = mScaleFactor
                    obscureDecorView(if (mScaleFactor >= 1f) mScaleFactor else 1f)
                }
            })
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    mEndingZoomAction.run()
                }

                override fun onAnimationEnd(animation: Animator) {
                    mEndingZoomAction.run()
                }
            })
            animator.interpolator = mEndZoomingInterpolator
            animator.start()
        } else mEndingZoomAction.run()
    }

    private fun startZoomingView(view: View) {
        mZoomableView = ImageView(mTarget.context)
        mZoomableView!!.layoutParams = ViewGroup.LayoutParams(mTarget.width, mTarget.height)
        mZoomableView!!.setImageBitmap(getBitmapFromView(view))

        //show the view in the same coords
        mTargetViewCords = getViewAbsoluteCords(view)
        mZoomableView!!.x = mTargetViewCords.x.toFloat()
        mZoomableView!!.y = mTargetViewCords.y.toFloat()
        if (mShadow == null) mShadow = View(mTarget.context)
        mShadow!!.setBackgroundResource(0)
        addToDecorView(mShadow!!)
        addToDecorView(mZoomableView!!)

        //trick for simulating the view is getting out of his parent
        disableParentTouch(mTarget.parent)
        mTarget.visibility = View.INVISIBLE
        if (mConfig.isImmersiveModeEnabled) hideSystemUI()
        mZoomListener?.onViewStartedZooming(mTarget)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (mZoomableView == null) return false
        mScaleFactor *= detector.scaleFactor

        // Don't let the object get too large.
        mScaleFactor = MIN_SCALE_FACTOR.coerceAtLeast(mScaleFactor.coerceAtMost(MAX_SCALE_FACTOR))
        mZoomableView!!.scaleX = mScaleFactor
        mZoomableView!!.scaleY = mScaleFactor
        obscureDecorView(mScaleFactor)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return mZoomableView != null
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        mScaleFactor = 1f
    }

    private fun addToDecorView(v: View) {
        mTargetContainer.decorView.addView(v)
    }

    private fun removeFromDecorView(v: View?) {
        mTargetContainer.decorView.removeView(v)
    }

    private fun obscureDecorView(factor: Float) {
        //normalize value between 0 and 1
        var normalizedValue = (factor - MIN_SCALE_FACTOR) / (MAX_SCALE_FACTOR - MIN_SCALE_FACTOR)
        normalizedValue = Math.min(0.75f, normalizedValue * 2)
        val obscure = Color.argb((normalizedValue * 255).toInt(), 0, 0, 0)
        mShadow!!.setBackgroundColor(obscure)
    }

    private fun hideSystemUI() {
        mTargetContainer.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN) // hide status ba;
    }

    private fun showSystemUI() {
        mTargetContainer.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    private fun disableParentTouch(view: ViewParent) {
        view.requestDisallowInterceptTouchEvent(true)
        if (view.parent != null) disableParentTouch(view.parent)
    }

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_POINTER_DOWN = 1
        private const val STATE_ZOOMING = 2
        private const val MIN_SCALE_FACTOR = 1f
        private const val MAX_SCALE_FACTOR = 5f
    }
}
