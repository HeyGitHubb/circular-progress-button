package com.dd.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;
import android.widget.RelativeLayout;

public class CircularProgressButton extends RelativeLayout {

    public static final int IDLE_STATE_PROGRESS = 0;
    public static final int ERROR_STATE_PROGRESS = -1;

    private GradientDrawable background;

    private StateListDrawable mIdleStateDrawable;
    private StateListDrawable mCompleteStateDrawable;

    private StateManager mStateManager;
    private State mState;

    private float mCornerRadius;
    private boolean mIndeterminateProgressMode;
    private boolean mConfigurationChanged;

    private enum State {
        PROGRESS, IDLE, COMPLETE
    }

    private int mMaxProgress;
    private int mProgress;

    private boolean mMorphingInProgress;

    public CircularProgressButton(Context context) {
        super(context);
    }

    public CircularProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private View mViewContent;

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        mViewContent = findViewById(R.id.root);
        mCornerRadius = 90.0f;
        mMaxProgress = 100;
        mState = State.IDLE;
        mStateManager = new StateManager(this);
        initIdleStateDrawable();
        setBackgroundCompat(mIdleStateDrawable);
    }

    private void initCompleteStateDrawable() {
        mCompleteStateDrawable = new StateListDrawable();
        mCompleteStateDrawable.addState(StateSet.WILD_CARD, background);
    }

    private void initIdleStateDrawable() {
        if (background == null) {
            background = createDrawable();
        }
        mIdleStateDrawable = new StateListDrawable();
        mIdleStateDrawable.addState(StateSet.WILD_CARD, background);
    }

    private GradientDrawable createDrawable() {
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.cpb_background).mutate();
        drawable.setColor(Color.parseColor("#a0de686b"));
        drawable.setCornerRadius(mCornerRadius);
        return drawable;
    }

    @Override
    protected void drawableStateChanged() {
        if (mState == State.COMPLETE) {
            initCompleteStateDrawable();
            setBackgroundCompat(mCompleteStateDrawable);
        } else if (mState == State.IDLE) {
            initIdleStateDrawable();
            setBackgroundCompat(mIdleStateDrawable);
        }

        if (mState != State.PROGRESS) {
            super.drawableStateChanged();
        }
    }

    public void setIndeterminateProgressMode(boolean indeterminateProgressMode) {
        this.mIndeterminateProgressMode = indeterminateProgressMode;
    }

    private MorphingAnimation createMorphing() {
        mMorphingInProgress = true;
        MorphingAnimation animation = new MorphingAnimation(this, mViewContent, background);
        animation.setFromCornerRadius(45.0f);
        animation.setToCornerRadius(90.0f);

        animation.setFromWidth(getWidth());
        animation.setToWidth(getWidth());

        if (mConfigurationChanged) {
            animation.setDuration(MorphingAnimation.DURATION_INSTANT);
        } else {
            animation.setDuration(MorphingAnimation.DURATION_NORMAL);
        }
        mConfigurationChanged = false;
        return animation;
    }

    private MorphingAnimation createProgressMorphing(float fromCorner, float toCorner, int fromWidth, int toWidth) {
        mMorphingInProgress = true;

        MorphingAnimation animation = new MorphingAnimation(this, mViewContent, background);

        animation.setFromCornerRadius(fromCorner);
        animation.setToCornerRadius(toCorner);

        animation.setFromWidth(fromWidth);
        animation.setToWidth(toWidth);

        if (mConfigurationChanged) {
            animation.setDuration(MorphingAnimation.DURATION_INSTANT);
        } else {
            animation.setDuration(MorphingAnimation.DURATION_NORMAL);
        }

        mConfigurationChanged = false;

        return animation;
    }

    private void morphToProgress() {
        MorphingAnimation animation = createProgressMorphing(mCornerRadius, getHeight(), getWidth(), getHeight());
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mProgressStateListener);
        animation.start();
    }

    private OnAnimationEndListener mProgressStateListener = new OnAnimationEndListener() {
        @Override
        public void onAnimationEnd() {
            mMorphingInProgress = false;
            mState = State.PROGRESS;

            mStateManager.checkState(CircularProgressButton.this);
        }
    };

    private void morphProgressToComplete() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mCompleteStateListener);
        animation.start();
    }

    private void morphIdleToComplete() {
        MorphingAnimation animation = createMorphing();
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mCompleteStateListener);
        animation.start();
    }

    private OnAnimationEndListener mCompleteStateListener = new OnAnimationEndListener() {
        @Override
        public void onAnimationEnd() {
            mMorphingInProgress = false;
            mState = State.COMPLETE;
            mStateManager.checkState(CircularProgressButton.this);
        }
    };

    private void morphCompleteToIdle() {
        MorphingAnimation animation = createMorphing();
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mIdleStateListener);
        animation.start();
    }

    private OnAnimationEndListener mIdleStateListener = new OnAnimationEndListener() {
        @Override
        public void onAnimationEnd() {
            mMorphingInProgress = false;
            mState = State.IDLE;
            mStateManager.checkState(CircularProgressButton.this);
        }
    };

    private void morphIdleToError() {
        MorphingAnimation animation = createMorphing();
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mErrorStateListener);
        animation.start();
    }

    private void morphProgressToError() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(mErrorStateListener);
        animation.start();
    }

    private OnAnimationEndListener mErrorStateListener = new OnAnimationEndListener() {
        @Override
        public void onAnimationEnd() {
            mMorphingInProgress = false;
            mStateManager.checkState(CircularProgressButton.this);
        }
    };

    private void morphProgressToIdle() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());
        animation.setFromColor(Color.parseColor("#a0de686b"));
        animation.setToColor(Color.parseColor("#a0de686b"));
        animation.setListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                mMorphingInProgress = false;
                mState = State.IDLE;
                mStateManager.checkState(CircularProgressButton.this);
            }
        });

        animation.start();
    }

    /**
     * Set the View's background. Masks the API changes made in Jelly Bean.
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    public void setProgress(int progress) {
        // 50
        mProgress = progress;

        if (mMorphingInProgress || getWidth() == 0) {
            return;
        }

        mStateManager.saveProgress(this);

        if (mProgress >= mMaxProgress) {
            if (mState == State.PROGRESS) {
                morphProgressToComplete();
            } else if (mState == State.IDLE) {
                morphIdleToComplete();
            }
        } else if (mProgress > IDLE_STATE_PROGRESS) {
            if (mState == State.IDLE) {
                morphToProgress();
            } else if (mState == State.PROGRESS) {
                invalidate();
            }
        } else if (mProgress == ERROR_STATE_PROGRESS) {
            if (mState == State.PROGRESS) {
                morphProgressToError();
            } else if (mState == State.IDLE) {
                morphIdleToError();
            }
        } else if (mProgress == IDLE_STATE_PROGRESS) {
            if (mState == State.COMPLETE) {
                morphCompleteToIdle();
            } else if (mState == State.PROGRESS) {
                morphProgressToIdle();
            }
        }
    }

    public int getProgress() {
        return mProgress;
    }

    @Override public void setBackgroundColor(int color) {
        background.setColor(color);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mProgress = mProgress;
        savedState.mIndeterminateProgressMode = mIndeterminateProgressMode;
        savedState.mConfigurationChanged = true;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mProgress = savedState.mProgress;
            mIndeterminateProgressMode = savedState.mIndeterminateProgressMode;
            mConfigurationChanged = savedState.mConfigurationChanged;
            super.onRestoreInstanceState(savedState.getSuperState());
            setProgress(mProgress);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    static class SavedState extends BaseSavedState {

        private boolean mIndeterminateProgressMode;
        private boolean mConfigurationChanged;
        private int mProgress;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mProgress = in.readInt();
            mIndeterminateProgressMode = in.readInt() == 1;
            mConfigurationChanged = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
            out.writeInt(mIndeterminateProgressMode ? 1 : 0);
            out.writeInt(mConfigurationChanged ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
