package ir.alirezaivaz.zoomy

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.animation.Interpolator
import androidx.fragment.app.DialogFragment

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */
object Zoomy {
    private var mDefaultConfig = ZoomyConfig()
    fun setDefaultConfig(config: ZoomyConfig) {
        mDefaultConfig = config
    }

    fun unregister(view: View) {
        view.setOnTouchListener(null)
    }

    class Builder {
        private var mDisposed = false
        private var mConfig: ZoomyConfig? = null
        private var mTargetContainer: TargetContainer?
        private var mTargetView: View? = null
        private var mZoomListener: ZoomListener? = null
        private var mZoomInterpolator: Interpolator? = null
        private var mTapListener: TapListener? = null
        private var mLongPressListener: LongPressListener? = null
        private var mdDoubleTapListener: DoubleTapListener? = null

        constructor(activity: Activity) {
            mTargetContainer = ActivityContainer(activity)
        }

        constructor(dialog: Dialog) {
            mTargetContainer = DialogContainer(dialog)
        }

        constructor(dialogFragment: DialogFragment) {
            mTargetContainer = DialogFragmentContainer(dialogFragment)
        }

        fun target(target: View?): Builder {
            mTargetView = target
            return this
        }

        fun animateZooming(animate: Boolean): Builder {
            checkNotDisposed()
            if (mConfig == null) mConfig = ZoomyConfig()
            mConfig!!.isZoomAnimationEnabled = animate
            return this
        }

        fun enableImmersiveMode(enable: Boolean): Builder {
            checkNotDisposed()
            if (mConfig == null) mConfig = ZoomyConfig()
            mConfig!!.isImmersiveModeEnabled = enable
            return this
        }

        fun interpolator(interpolator: Interpolator?): Builder {
            checkNotDisposed()
            mZoomInterpolator = interpolator
            return this
        }

        fun zoomListener(listener: ZoomListener?): Builder {
            checkNotDisposed()
            mZoomListener = listener
            return this
        }

        fun tapListener(listener: TapListener?): Builder {
            checkNotDisposed()
            mTapListener = listener
            return this
        }

        fun longPressListener(listener: LongPressListener?): Builder {
            checkNotDisposed()
            mLongPressListener = listener
            return this
        }

        fun doubleTapListener(listener: DoubleTapListener?): Builder {
            checkNotDisposed()
            mdDoubleTapListener = listener
            return this
        }

        fun register() {
            checkNotDisposed()
            if (mConfig == null) mConfig = mDefaultConfig
            requireNotNull(mTargetContainer) { "Target container must not be null" }
            requireNotNull(mTargetView) { "Target view must not be null" }
            mTargetView!!.setOnTouchListener(
                ZoomableTouchListener(
                    mTargetContainer!!, mTargetView!!,
                    mConfig!!, mZoomInterpolator, mZoomListener, mTapListener, mLongPressListener,
                    mdDoubleTapListener
                )
            )
            mDisposed = true
        }

        private fun checkNotDisposed() {
            check(!mDisposed) { "Builder already disposed" }
        }
    }
}
