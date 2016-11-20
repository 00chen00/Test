package com.wan3456.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.activity.InfoActivity;
import com.wan3456.sdk.tools.Helper;

@SuppressLint({ "ClickableViewAccessibility", "HandlerLeak" })
public class FloatView extends FrameLayout implements OnTouchListener {
	private final int HANDLER_TYPE_HIDE_LOGO = 100;// 隐藏LOGO
	private final int HANDLER_TYPE_CANCEL_ANIM = 101;// 退出动画
	private WindowManager.LayoutParams mWmParams;
	private WindowManager mWindowManager;
	private Context mContext;
	private ImageView mIvFloatLogo;
	private ImageView mIvFloatLoader;
	private LinearLayout mLlFloatMenu;
	private TextView account_t;
	private TextView close_t;
	private FrameLayout sp_t;
	private FrameLayout gift_t;
	private FrameLayout tj_t;
	private FrameLayout kf_t;
	private FrameLayout message_t;
	private FrameLayout mFlFloatLogo;
	private ImageView gift_point;
	private ImageView tj_point;
	private ImageView kf_point;
	private ImageView sp_point;
	private boolean mIsRight;// logo是否在右边
	private boolean mCanHide;// 是否允许隐藏
	private float mTouchStartX;
	private float mTouchStartY;
	private int mScreenWidth;
	private int mScreenHeight;
	private boolean mDraging;
	private boolean mShowLoader = true;
    private int statusBarHeight;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private SharedPreferences sharedPreferences;
	private boolean isShowPoint = false;
	final Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
				// 比如隐藏悬浮框
				if (mCanHide) {
					mCanHide = false;
					if (mIsRight) {
						if (isShowPoint) {
							mIvFloatLogo.setImageResource(Helper
									.getResDraw(mContext,
											"wan3456_image_float_right_point"));
						} else {
							mIvFloatLogo.setImageResource(Helper.getResDraw(
									mContext, "wan3456_image_float_right"));
						}
					} else {
						if (isShowPoint) {
							mIvFloatLogo
									.setImageResource(Helper.getResDraw(
											mContext,
											"wan3456_image_float_left_point"));
						} else {
							mIvFloatLogo.setImageResource(Helper.getResDraw(
									mContext, "wan3456_image_float_left"));
						}
					}
					mWmParams.alpha = 0.7f;
					mWindowManager.updateViewLayout(FloatView.this, mWmParams);
					refreshFloatMenu(mIsRight);
					mLlFloatMenu.setVisibility(View.GONE);
				}
			} else if (msg.what == HANDLER_TYPE_CANCEL_ANIM) {
				mIvFloatLoader.clearAnimation();
				mIvFloatLoader.setVisibility(View.GONE);
				mShowLoader = false;
			}
			super.handleMessage(msg);
		}
	};

	public FloatView(Context context) {
		super(context);
		sharedPreferences = context.getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		init(context);
	}

	private void init(Context mContext) {
		this.mContext = mContext;

		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		// 更新浮动窗口位置参数 靠边
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		this.mWmParams = new WindowManager.LayoutParams();
		// 设置window type
		if (sharedPreferences.getString("Brand", "").equals("Xiaomi")
				&& sharedPreferences.getBoolean("IsV6", false) == true) {
			mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		} else {
			mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		}
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		// } else {
		// mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// }
		// 设置图片格式，效果为背景透明
		mWmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧置
		mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

		mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		mWmParams.x = 0;
		mWmParams.y =(mScreenHeight-getStatusBarHeight())/2;

		// 设置悬浮窗口长宽数据
		mWmParams.width = LayoutParams.WRAP_CONTENT;
		mWmParams.height = LayoutParams.WRAP_CONTENT;
		addView(createView(mContext));
		mWindowManager.addView(this, mWmParams);
		mTimer = new Timer();
		setVisibility(View.GONE);
		// hide();
	}
	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// 更新浮动窗口位置参数 靠边
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		int oldX = mWmParams.x;
		int oldY = mWmParams.y;
		switch (newConfig.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:// 横屏
			if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {
				mWmParams.x = oldX;
				mWmParams.y = oldY;
			}
			break;
		case Configuration.ORIENTATION_PORTRAIT:// 竖屏
			if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {
				mWmParams.x = oldX;
				mWmParams.y = oldY;
			}
			break;
		}
		mWindowManager.updateViewLayout(this, mWmParams);
	}

	/**
	 * 创建Float view
	 * 
	 * @param context
	 * @return
	 */
	private View createView(final Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// 从布局文件获取浮动窗口视图
		View rootFloatView = inflater.inflate(
				Helper.getLayoutId(context, "wan3456_widget_float_view"), null);
		mFlFloatLogo = (FrameLayout) rootFloatView.findViewById(Helper
				.getResId(context, "wan3456_float_view"));

		mIvFloatLogo = (ImageView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_view_icon_imageView"));
		mIvFloatLoader = (ImageView) rootFloatView.findViewById(Helper
				.getResId(context, "wan3456_float_view_icon_notify"));
		mLlFloatMenu = (LinearLayout) rootFloatView.findViewById(Helper
				.getResId(context, "wan3456_float_menu"));
		account_t = (TextView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_account"));
		kf_t = (FrameLayout) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_kf"));
		gift_t = (FrameLayout) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_gift"));
		tj_t = (FrameLayout) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_tj"));
		message_t = (FrameLayout) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_message"));
		sp_t = (FrameLayout) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_sp"));
		close_t = (TextView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_close"));
		gift_point = (ImageView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_gift_point"));
		kf_point = (ImageView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_kf_point"));
		tj_point = (ImageView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_tj_point"));
		sp_point = (ImageView) rootFloatView.findViewById(Helper.getResId(
				context, "wan3456_float_sp_point"));
		if (sharedPreferences.getInt("isred_mes", 0) == 1) {
			message_t.setVisibility(View.VISIBLE);
		} else {
			message_t.setVisibility(View.GONE);
		}
		if (sharedPreferences.getInt("isred", 0) == 1) {
			tj_point.setVisibility(View.VISIBLE);
		}

		if (sharedPreferences.getInt("isred_peck", 0) == 1) {
			gift_point.setVisibility(View.VISIBLE);
		}
		if (sharedPreferences.getInt("isred_kf", 0) == 1) {
			kf_point.setVisibility(View.VISIBLE);
		}
		if (sharedPreferences.getInt("isred_ac", 0) == 1) {
			sp_point.setVisibility(View.VISIBLE);
		}

		account_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				openUserCenter();
			}
		});

		kf_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				openServiceCenter();
			}
		});
		gift_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				openGiftCenter();
			}
		});
		sp_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				openSpCenter();
			}
		});
		tj_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				openTjCenter();
			}
		});
		message_t.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLlFloatMenu.setVisibility(View.GONE);
				openMessageCenter();

			}
		});
		close_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mLlFloatMenu.setVisibility(View.GONE);
				doCloseFloat();
			}
		});
		checkShowPoint();
		rootFloatView.setOnTouchListener(this);
		rootFloatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mDraging) {
					if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
						mLlFloatMenu.setVisibility(View.GONE);
					} else {
						mLlFloatMenu.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		rootFloatView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		return rootFloatView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		removeTimerTask();
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			mIvFloatLogo.setImageResource(Helper.getResDraw(mContext,
					"wan3456_image_float_logo"));
			mWmParams.alpha = 1f;
			mWindowManager.updateViewLayout(this, mWmParams);
			mDraging = false;
			break;
		case MotionEvent.ACTION_MOVE:
			float mMoveStartX = event.getX();
			float mMoveStartY = event.getY();
			// 如果移动量大于3才移动
			if (Math.abs(mTouchStartX - mMoveStartX) > 3
					&& Math.abs(mTouchStartY - mMoveStartY) > 3) {
				mDraging = true;
				// 更新浮动窗口位置参数
				mWmParams.x = (int) (x - mTouchStartX);
				mWmParams.y = (int) (y - mTouchStartY);
				mWindowManager.updateViewLayout(this, mWmParams);
				mLlFloatMenu.setVisibility(View.GONE);
				return false;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			if (mWmParams.x >= mScreenWidth / 2) {
				mWmParams.x = mScreenWidth;
				mIsRight = true;
			} else if (mWmParams.x < mScreenWidth / 2) {
				mIsRight = false;
				mWmParams.x = 0;
			}
			mIvFloatLogo.setImageResource(Helper.getResDraw(mContext,
					"wan3456_image_float_logo"));
			refreshFloatMenu(mIsRight);
			timerForHide();
			mWindowManager.updateViewLayout(this, mWmParams);
			// 初始化
			mTouchStartX = mTouchStartY = 0;
			break;
		}
		return false;
	}

	private void removeTimerTask() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	private void removeFloatView() {
		try {
			mWindowManager.removeView(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 隐藏悬浮窗
	 */
	public void hide() {
		setVisibility(View.GONE);
		Message message = mTimerHandler.obtainMessage();
		message.what = HANDLER_TYPE_HIDE_LOGO;
		mTimerHandler.sendMessage(message);
		removeTimerTask();
	}

	/**
	 * 显示悬浮窗
	 */
	public void show() {

		if (getVisibility() != View.VISIBLE) {

			setVisibility(View.VISIBLE);
			if (mShowLoader) {

				mIvFloatLogo.setImageResource(Helper.getResDraw(mContext,
						"wan3456_image_float_logo"));
				mWmParams.alpha = 1f;
				mWindowManager.updateViewLayout(this, mWmParams);

				timerForHide();

				mShowLoader = false;
				Animation rotaAnimation = AnimationUtils.loadAnimation(
						mContext,
						Helper.getResAnm(mContext, "wan3456_loading_anim"));
				rotaAnimation.setInterpolator(new LinearInterpolator());
				mIvFloatLoader.startAnimation(rotaAnimation);
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						mTimerHandler
								.sendEmptyMessage(HANDLER_TYPE_CANCEL_ANIM);
					}
				}, 2000);
			}
		}
	}

	/**
	 * 刷新float view menu
	 * 
	 * @param right
	 */
	private void refreshFloatMenu(boolean right) {
		if (right) {
			FrameLayout.LayoutParams paramsFloatImage = (FrameLayout.LayoutParams) mIvFloatLogo
					.getLayoutParams();
			paramsFloatImage.gravity = Gravity.RIGHT;
			mIvFloatLogo.setLayoutParams(paramsFloatImage);
			FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo
					.getLayoutParams();
			paramsFlFloat.gravity = Gravity.RIGHT;
			mFlFloatLogo.setLayoutParams(paramsFlFloat);

			int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources()
							.getDisplayMetrics());
			int padding52 = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources()
							.getDisplayMetrics());
			LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) account_t
					.getLayoutParams();
			paramsMenuAccount.rightMargin = padding;
			paramsMenuAccount.leftMargin = padding;
			account_t.setLayoutParams(paramsMenuAccount);
			LinearLayout.LayoutParams paramsMenuGift = (LinearLayout.LayoutParams) gift_t
					.getLayoutParams();
			paramsMenuGift.rightMargin = padding;
			paramsMenuGift.leftMargin = padding;
			gift_t.setLayoutParams(paramsMenuGift);

			LinearLayout.LayoutParams paramsMenuSp = (LinearLayout.LayoutParams) sp_t
					.getLayoutParams();
			paramsMenuSp.rightMargin = padding;
			paramsMenuSp.leftMargin = padding;
			sp_t.setLayoutParams(paramsMenuSp);
			LinearLayout.LayoutParams paramsMenuTj = (LinearLayout.LayoutParams) tj_t
					.getLayoutParams();
			paramsMenuTj.rightMargin = padding;
			paramsMenuTj.leftMargin = padding;
			tj_t.setLayoutParams(paramsMenuTj);

			LinearLayout.LayoutParams paramsMenuKf = (LinearLayout.LayoutParams) kf_t
					.getLayoutParams();
			paramsMenuKf.rightMargin = padding;
			paramsMenuKf.leftMargin = padding;
			kf_t.setLayoutParams(paramsMenuKf);
			LinearLayout.LayoutParams paramsMenuMes = (LinearLayout.LayoutParams) message_t
					.getLayoutParams();
			paramsMenuMes.rightMargin = padding;
			paramsMenuMes.leftMargin = padding;
			message_t.setLayoutParams(paramsMenuMes);
			LinearLayout.LayoutParams paramsMenuClose = (LinearLayout.LayoutParams) close_t
					.getLayoutParams();
			paramsMenuClose.rightMargin = padding52;
			paramsMenuClose.leftMargin = padding;
			close_t.setLayoutParams(paramsMenuClose);
		} else {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIvFloatLogo
					.getLayoutParams();
			params.setMargins(0, 0, 0, 0);
			params.gravity = Gravity.LEFT;
			mIvFloatLogo.setLayoutParams(params);
			FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo
					.getLayoutParams();
			paramsFlFloat.gravity = Gravity.LEFT;
			mFlFloatLogo.setLayoutParams(paramsFlFloat);

			int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources()
							.getDisplayMetrics());
			int padding52 = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources()
							.getDisplayMetrics());

			LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) account_t
					.getLayoutParams();
			paramsMenuAccount.rightMargin = padding;
			paramsMenuAccount.leftMargin = padding52;
			account_t.setLayoutParams(paramsMenuAccount);
			LinearLayout.LayoutParams paramsMenuGift = (LinearLayout.LayoutParams) gift_t
					.getLayoutParams();
			paramsMenuGift.rightMargin = padding;
			paramsMenuGift.leftMargin = padding;
			gift_t.setLayoutParams(paramsMenuGift);

			LinearLayout.LayoutParams paramsMenuSp = (LinearLayout.LayoutParams) sp_t
					.getLayoutParams();
			paramsMenuSp.rightMargin = padding;
			paramsMenuSp.leftMargin = padding;
			sp_t.setLayoutParams(paramsMenuSp);
			LinearLayout.LayoutParams paramsMenuTj = (LinearLayout.LayoutParams) tj_t
					.getLayoutParams();
			paramsMenuTj.rightMargin = padding;
			paramsMenuTj.leftMargin = padding;
			tj_t.setLayoutParams(paramsMenuTj);
			LinearLayout.LayoutParams paramsMenuMes = (LinearLayout.LayoutParams) message_t
					.getLayoutParams();
			paramsMenuMes.rightMargin = padding;
			paramsMenuMes.leftMargin = padding;
			message_t.setLayoutParams(paramsMenuMes);

			LinearLayout.LayoutParams paramsMenuKf = (LinearLayout.LayoutParams) kf_t
					.getLayoutParams();
			paramsMenuKf.rightMargin = padding;
			paramsMenuKf.leftMargin = padding;
			kf_t.setLayoutParams(paramsMenuKf);
			LinearLayout.LayoutParams paramsMenuClose = (LinearLayout.LayoutParams) close_t
					.getLayoutParams();
			paramsMenuClose.rightMargin = padding;
			paramsMenuClose.leftMargin = padding;
			close_t.setLayoutParams(paramsMenuClose);
		}
	}

	/**
	 * 定时隐藏float view
	 */
	private void timerForHide() {
		mCanHide = true;
		// 结束任务
		if (mTimerTask != null) {
			try {
				mTimerTask.cancel();
				mTimerTask = null;
			} catch (Exception e) {
			}

		}
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = mTimerHandler.obtainMessage();
				message.what = HANDLER_TYPE_HIDE_LOGO;
				mTimerHandler.sendMessage(message);
			}
		};
		if (mCanHide) {
			mTimer.schedule(mTimerTask, 3000, 3000);
		}
	}

	/**
	 * 打开用户中心
	 */
	private void openUserCenter() {

		InfoActivity.show(mContext, InfoActivity.INDEX_ACCOUNT);
	}

	/**
	 * 打开客服页面
	 */
	private void openServiceCenter() {
		kf_point.setVisibility(View.GONE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isred_kf", 0);
		editor.commit();
		InfoActivity.show(mContext, InfoActivity.INDEX_SERVICE_CENTER);
		checkShowPoint();
	}

	/**
	 * 打开专区
	 */
	private void openSpCenter() {
		sp_point.setVisibility(View.GONE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isred_ac", 0);
		editor.commit();
		InfoActivity.show(mContext, InfoActivity.INDEX_ACTIVITY);
		checkShowPoint();
	}

	/**
	 * 打开礼包中心
	 */
	private void openGiftCenter() {
		gift_point.setVisibility(View.GONE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isred_peck", 0);
		editor.commit();
		InfoActivity.show(mContext, InfoActivity.INDEX_GIFT);
		checkShowPoint();
	}

	/**
	 * 打开新游推荐中心
	 */
	private void openTjCenter() {
		tj_point.setVisibility(View.GONE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isred", 0);
		editor.commit();
		InfoActivity.show(mContext, InfoActivity.INDEX_TJ);
		checkShowPoint();
	}

	/**
	 * 打开消息中心
	 */
	protected void openMessageCenter() {
		message_t.setVisibility(View.GONE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isred_mes", 0);
		editor.commit();
		InfoActivity.show(mContext, InfoActivity.INDEX_LAST_MESSAGE);
		checkShowPoint();

	}

	/**
	 * 隐藏浮窗
	 */
	protected void doCloseFloat() {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("shake", true);
		editor.commit();
		View toastRoot = LayoutInflater.from(mContext).inflate(
				Helper.getLayoutId(mContext, "wan3456_toast_view"), null);
		Toast toast = new Toast(mContext);
		toast.setView(toastRoot);
		toast.setGravity(Gravity.CENTER, 0, 0);
		TextView text = (TextView) toastRoot.findViewById(Helper.getResId(
				mContext, "wan3456_toast_mes"));
		text.setText("摇一摇手机，开启悬浮窗！");
		toast.setDuration(3000);
		toast.show();
		Wan3456.getInstance(mContext).close();

	}

	/**
	 * 是否Float view
	 */
	public void destroy() {
		hide();
		removeFloatView();
		removeTimerTask();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		try {
			mTimerHandler.removeMessages(1);
		} catch (Exception e) {
		}
	}
/**
 * 检查logo是否显示红点
 */
	private void checkShowPoint() {
		if (sharedPreferences.getInt("isred_peck", 0) == 0
				&& sharedPreferences.getInt("isred", 0) == 0
				&& sharedPreferences.getInt("isred_kf", 0) == 0
				&& sharedPreferences.getInt("isred_mes", 0) == 0
				&& sharedPreferences.getInt("isred_ac", 0) == 0) {
			isShowPoint = false;
		} else {
			isShowPoint = true;
		}
	}
}
