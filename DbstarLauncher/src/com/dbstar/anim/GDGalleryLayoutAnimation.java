package com.dbstar.anim;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;

public class GDGalleryLayoutAnimation extends LayoutAnimationController {

	public GDGalleryLayoutAnimation(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GDGalleryLayoutAnimation(Animation animation) {
		super(animation);
	}

	public GDGalleryLayoutAnimation(Animation animation, float delay) {
		super(animation, delay);
	}
	
	/**
     * Returns the amount of milliseconds by which the specified view's
     * animation must be delayed or offset. Subclasses should override this
     * method to return a suitable value.
     *
     * This implementation returns <code>child animation delay</code>
     * milliseconds where:
     *
     * <pre>
     * child animation delay = child index * delay
     * </pre>
     *
     * The index is retrieved from the
     * {@link android.view.animation.LayoutAnimationController.AnimationParameters}
     * found in the view's {@link android.view.ViewGroup.LayoutParams}.
     *
     * @param view the view for which to obtain the animation's delay
     * @return a delay in milliseconds
     *
     * @see #getAnimationForView(android.view.View)
     * @see #getDelay()
     * @see #getTransformedIndex(android.view.animation.LayoutAnimationController.AnimationParameters)
     * @see android.view.ViewGroup.LayoutParams
     */
    protected long getDelayForView(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        AnimationParameters params = lp.layoutAnimationParameters;

        if (params == null) {
            return 0;
        }

        final float delay = getDelay() * mAnimation.getDuration();
        final long viewDelay = (long) (getTransformedIndex(params) * delay);
        final float totalDelay = delay * params.count;

        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }

        float normalizedDelay = viewDelay / totalDelay;
        normalizedDelay = mInterpolator.getInterpolation(normalizedDelay);

        return (long) (normalizedDelay * totalDelay);
    }
}
