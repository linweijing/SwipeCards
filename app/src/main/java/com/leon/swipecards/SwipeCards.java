package com.leon.swipecards;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Leon on 2017/6/7.
 */

public class SwipeCards extends ViewGroup {

    private static final String TAG = "SwipeCards";

    private int mCenterX;
    private int mCenterY;

    private ViewDragHelper mViewDragHelper;

    private static final int MAX_DEGREE = 60;
    private static final float MAX_ALPHA_RANGE = 0.5f;

    private int mCardGap = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

    public SwipeCards(Context context) {
        this(context, null);
    }

    public SwipeCards(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = mCenterX - child.getMeasuredWidth() / 2;
            int top = mCenterY - child.getMeasuredHeight() / 2 + mCardGap * (getChildCount() - i);
            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(TAG, "onViewPositionChanged: " + left);
            //计算位置改变后，与原来位置的中心点变化量
            int diffX = left + changedView.getWidth() / 2 - mCenterX;
            float ratio = diffX * 1.0f / getWidth();
            float degree = MAX_DEGREE * ratio;
            changedView.setRotation(degree);
            float alpha = 1 - Math.abs(ratio) * MAX_ALPHA_RANGE;
            changedView.setAlpha(alpha);
        }

        @Override
        public void onViewReleased(final View releasedChild, float xvel, float yvel) {
            final int left = releasedChild.getLeft();
            if (left > getWidth() * 0.5f) {
                int finalLeft = getWidth() + (getHeight() - getWidth()) / 2;
                int finalTop = releasedChild.getTop();
                mViewDragHelper.smoothSlideViewTo(releasedChild, finalLeft, finalTop);
                invalidate();
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(false)) {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
