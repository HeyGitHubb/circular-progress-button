package com.dd.sample;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

class MorphingAnimation {

    public static final int DURATION_NORMAL = 400;
    public static final int DURATION_INSTANT = 1;

    private OnAnimationEndListener mListener;

    private int mDuration;

    private int mFromWidth;
    private int mToWidth;

    private int mFromColor;
    private int mToColor;

    private float mFromCornerRadius;
    private float mToCornerRadius;
    private View mViewRoot, mViewContent;
    private GradientDrawable mDrawableRoot;

    public MorphingAnimation(View root, View content,  GradientDrawable drawableRoot) {
        mViewRoot = root;
        mDrawableRoot = drawableRoot;
        mViewContent = content;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setListener(OnAnimationEndListener listener) {
        mListener = listener;
    }

    public void setFromWidth(int fromWidth) {
        Log.e(">>>>", ">>>> setFromWidth fromWidth: " + fromWidth);

        mFromWidth = fromWidth;
    }

    public void setToWidth(int toWidth) {
        Log.e(">>>>", ">>>> setToWidth  toWidth: " + toWidth);
        mToWidth = toWidth;
    }

    public void setFromColor(int fromColor) {
        mFromColor = fromColor;
    }

    public void setToColor(int toColor) {
        mToColor = toColor;
    }

    public void setFromCornerRadius(float fromCornerRadius) {
        mFromCornerRadius = fromCornerRadius;
    }

    public void setToCornerRadius(float toCornerRadius) {
        mToCornerRadius = toCornerRadius;
    }

    public void start() {
        ValueAnimator widthAnimationC = ValueAnimator.ofInt(mFromWidth, mToWidth);
        widthAnimationC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                int leftOffset;
                Log.e(">>>>", ">>>> value:" + value);
                if (mFromWidth > mToWidth) {
                    leftOffset = (mFromWidth - value);
                } else {
                    leftOffset = (mToWidth - value);
                }

                Log.e(">>>>", ">>>> " + leftOffset);

                mDrawableRoot.setBounds(leftOffset, 0, mViewRoot.getWidth(), mViewRoot.getHeight());
            }
        });

        ValueAnimator widthAnimation = ValueAnimator.ofInt(mFromWidth, mToWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                int leftOffset;
                Log.e(">>>>", ">>>> value:" + value);
                if (mFromWidth > mToWidth) {
                    leftOffset = (mFromWidth - value);
                } else {
                    leftOffset = (mToWidth - value);
                }

                Log.e(">>>>", ">>>> " + leftOffset);

                mViewContent.layout(leftOffset, 0, mViewRoot.getWidth(), mViewRoot.getHeight());
            }
        });

        //ObjectAnimator bgColorAnimation = ObjectAnimator.ofInt(mDrawableRoot, "color", mFromColor, mToColor);
        //bgColorAnimation.setEvaluator(new ArgbEvaluator());

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(mDrawableRoot, "cornerRadius", mFromCornerRadius, mToCornerRadius);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mDuration);
        animatorSet.playTogether(widthAnimationC, widthAnimation, cornerAnimation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }
}