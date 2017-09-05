package com.lyz.subway;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScllorTabView extends View {

	private int mTabNum, mCurrentNum;
	private float mWidth, mTabWidth, mOffset;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mBeginColor;
	private int mEndColor;
	private LinearGradient gradient;
	private int width;

	public ScllorTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ScllorTableView);
		width = a.getDimensionPixelSize(0, 40);
		a.recycle();
	}

	public void setTabNum(int n) {
		mTabNum = n;
	}

	public void setCurrentNum(int n) {
		mCurrentNum = n;
		mOffset = 0;
	}

	public void setOffset(int position, float offset) {
		// if (offset == 0) {
		// return;
		// }
		mCurrentNum = position;
		mOffset = offset;
		invalidate();
	}

	public void setTabWidth(int mTabWidth) {
		this.mTabWidth = mTabWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTabWidth == 0) {
			mWidth = getWidth();
			mTabWidth = mWidth / mTabNum;
		}

		float left = (mCurrentNum + mOffset) * mTabWidth + width / 2;
		final float right = (mCurrentNum + mOffset) * mTabWidth + mTabWidth
				- width / 2;
		final float top = getPaddingTop();
		final float bottom = getHeight() - getPaddingBottom();

		mPaint.setColor(mBeginColor);
		canvas.drawRect(left, top, right, bottom, mPaint);
	}

	public void setSelectedColor(int color, int color2) {
		mBeginColor = color;
		mEndColor = color2;

	}

}
