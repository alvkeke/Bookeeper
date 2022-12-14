package com.alvkeke.bookeeper.components.slidelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Date;

public class SlideListView extends ListView{

	private SlideListViewCallback mCallback;

	private int mScreenWidth;
	private int mHiddenWidth;

	private int mRightSideThreshold;
	private int mLeftSideThreshold;

	private int mDownX, mDownY;
	private int mPointPosition;
	private ViewGroup mPointChild;
	private LinearLayout.LayoutParams mItemLayoutParams;
	boolean mIsSlide;
	boolean mIsChecked;
	private boolean mIsHiddenShowed;

	private long mDownTime;
	private int mLongClickTimeInterval;
	private boolean mIsClickBlocked;
	private Thread mLongClickThread;

	public SlideListView(Context context)
	{
		super(context);
		init(context);
	}

	public SlideListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public SlideListView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context)
	{
		mCallback = new SlideListViewCallback() {
			@Override
			public boolean canSlideOutRightEdge(int pos)
			{
				return false;
			}

			@Override
			public boolean canSlideOutLeftEdge(int pos)
			{
				return false;
			}

			@Override
			public boolean SlideToRightEdge(int position)
			{
				return false;
			}

			@Override
			public boolean SlideToLeftEdge(int position)
			{
				return false;
			}

			@Override
			public void onTouchDown(int pos, MotionEvent ev)
			{

			}

			@Override
			public void onTouchMove(int pos, MotionEvent ev)
			{

			}

			@Override
			public void onTouchUp(int pos, MotionEvent ev)
			{

			}

			@Override
			public boolean onShowViewClick(int pos)
			{
				return false;
			}

			@Override
			public boolean onHideViewClick(int pos)
			{
				return false;
			}

			@Override
			public boolean onShowViewLongClick(int pos)
			{
				return false;
			}

			@Override
			public boolean onHideViewLongClick(int pos)
			{
				return false;
			}
		};

		setSelector(new ColorDrawable(Color.TRANSPARENT));

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		assert wm != null;
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;

		mLeftSideThreshold = mScreenWidth/10;
		mRightSideThreshold = 9*mScreenWidth/10;

		mIsHiddenShowed = false;
		mLongClickTimeInterval = 500;

	}

	public void setCallback(SlideListViewCallback mCallback)
	{
		this.mCallback = mCallback;
	}

	public boolean setSlideThreshold(float threshold)
	{
		if (threshold >=0.5) return false;
		mLeftSideThreshold = (int) (mScreenWidth * threshold);
		mRightSideThreshold = (int) (mScreenWidth * (1-threshold));
		return true;
	}

	public void setLongClickTimeInterval(int time)
	{
		mLongClickTimeInterval = time;
	}

	@Override
	protected void handleDataChanged()
	{
		super.handleDataChanged();
		hideHiddenLayout();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{

		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			mDownTime = new Date().getTime();
			mLongClickThread = new Thread(new LongClickTimer());
			mLongClickThread.start();

			ViewGroup tmpView;

			mDownX = (int) ev.getX();
			mDownY = (int) ev.getY();
			mIsSlide = false;
			mIsChecked = false;
			mIsClickBlocked = false;

			mPointPosition = pointToPosition(mDownX, mDownY);
			tmpView = (ViewGroup) getChildAt(mPointPosition - getFirstVisiblePosition());
			if (tmpView == null)
			{
				return false;
			}
			if (mIsHiddenShowed)
			{
				if (!tmpView.equals(mPointChild))
				{
					hideHiddenLayout();
				}
			}
			mPointChild = tmpView;
			mHiddenWidth = mPointChild.getChildAt(1).getLayoutParams().width;
			mItemLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();
			mItemLayoutParams.width = mScreenWidth;
			mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);

			mCallback.onTouchDown(mPointPosition, ev);

		}
		else if (ev.getAction() == MotionEvent.ACTION_MOVE)
		{

			int differX = (int) (ev.getX() - mDownX);
			int differX_a = Math.abs(differX);
			int differY_a = (int) Math.abs(ev.getY() - mDownY);

			if (!mIsChecked && (differX_a > 10 || differY_a > 10))
			{
				mIsChecked = true;
				if (differX_a > differY_a)
				{
					mIsSlide = true;
				}
			}

			if (mIsSlide)
			{
				cancelClickListen();
				if (mIsHiddenShowed)
				{
					if (!mCallback.canSlideOutLeftEdge(mPointPosition) && differX < 0)
						differX = 0;
					if (!mCallback.canSlideOutRightEdge(mPointPosition) && differX > mHiddenWidth)
						differX = mHiddenWidth;

					mItemLayoutParams.leftMargin = differX - mHiddenWidth;
					mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
				}
				else
				{
					if (!mCallback.canSlideOutLeftEdge(mPointPosition) && differX <= -mHiddenWidth)
						differX = -mHiddenWidth;
					if (!mCallback.canSlideOutRightEdge(mPointPosition) && differX >= 0)
						differX = 0;

					mItemLayoutParams.leftMargin = differX;
					mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
				}
			}
			else
			{
				// 只要存在手指移动, 则取消长按计时
				int tmpPos = pointToPosition((int) ev.getX(), (int) ev.getY());
				if (tmpPos != mPointPosition)
				{
					cancelClickListen();
				}
			}

			mCallback.onTouchMove(mPointPosition, ev);
		}
		else if (ev.getAction() == MotionEvent.ACTION_UP)
		{
			// 完成点击判断
			if (mIsSlide)
			{
				int limitDistance;
				if (mIsHiddenShowed)
					limitDistance = 3* mHiddenWidth / 4;
				else
					limitDistance = mHiddenWidth / 2;

				if (mCallback.canSlideOutLeftEdge(mPointPosition) && ev.getX() < mLeftSideThreshold)
				{
					if (mCallback.SlideToLeftEdge(mPointPosition))
					{
						mIsHiddenShowed = false;
						return false;
					}
				}
				else if (mCallback.canSlideOutRightEdge(mPointPosition) && ev.getX() > mRightSideThreshold)
				{
					if (mCallback.SlideToRightEdge(mPointPosition))
					{
						mIsHiddenShowed = false;
						return false;
					}
				}

				if (-mItemLayoutParams.leftMargin >= limitDistance)
				{
					showHiddenLayout();
				}
				else
				{
					hideHiddenLayout();
				}
			}
			else
			{
				if (!mIsClickBlocked)
				{
					if (!mIsHiddenShowed || mDownX <= (mScreenWidth-mHiddenWidth))
					{
						mCallback.onShowViewClick(mPointPosition);
						if (new Date().getTime() - mDownTime < mLongClickTimeInterval)
						{
							mLongClickThread.interrupt();
						}
					}
					else
					{
						mCallback.onHideViewClick(mPointPosition);
						if (new Date().getTime() - mDownTime < mLongClickTimeInterval)
						{
							mLongClickThread.interrupt();
						}
					}
				}
			}
			mCallback.onTouchUp(mPointPosition, ev);
		}

		if (mIsSlide) return true;
		return super.onTouchEvent(ev);
	}

	private class LongClickTimer implements Runnable{

		@Override
		public void run()
		{
			try
			{
				Thread.sleep(mLongClickTimeInterval);
				if (!mIsHiddenShowed || mDownX <= mScreenWidth - mHiddenWidth)
				{
					mIsClickBlocked = mCallback.onShowViewLongClick(mPointPosition);
				}
				else
				{
					mIsClickBlocked = mCallback.onHideViewLongClick(mPointPosition);
				}

			} catch (InterruptedException ignored) { }

		}
	}

	private void cancelClickListen()
	{
		if (!mLongClickThread.isInterrupted()) mLongClickThread.interrupt();
		mIsClickBlocked = true;
	}

	private void hideHiddenLayout()
	{
		mItemLayoutParams.leftMargin = 0;
		mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
		mIsHiddenShowed = false;
	}

	private void showHiddenLayout()
	{
		mItemLayoutParams.leftMargin = -mHiddenWidth;
		mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
		mIsHiddenShowed = true;
	}

}
