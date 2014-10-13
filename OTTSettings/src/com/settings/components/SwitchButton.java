package com.settings.components;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.settings.ottsettings.R;
import com.settings.utils.SettingUtils;

public class SwitchButton extends LinearLayout implements OnClickListener {

	private static final int FLAG_MOVE_TRUE = 1; // 向左滑动标识

	private static final int FLAG_MOVE_FALSE = 2; // 向右滑动标识

	// 上下文对象
	private Context context;

	private RelativeLayout sv_container; // 开关外层样式

	private ImageView iv_switch_cursor; // 开关邮标的ImageView

	private TextView switch_text_true; // true的文字信息控件

	private TextView switch_text_false; // false的文字信息控件

	private boolean isChecked = true; // 是否已开

	private OnCheckedChangeListener onCheckedChangeListener; // 用于监听isChecked是否有改变

	private int margin = 1; // 游标离边缘位置(这个值视图片而定, 主要是为了图片能显示正确)

	private int bg_left; // 背景左

	private int bg_right; // 背景右

	private int cursor_left; // 游标左部

	private int cursor_top; // 游标顶部

	private int cursor_right; // 游标右部

	private int cursor_bottom; // 游标底部

	private Animation animation; // 移动动画

	private int currentFlag = FLAG_MOVE_TRUE; // 当前移动方向flag

	private float textSize = 0;

	private int padding = 0;

	private int cursorWidth = 0;

	private CountDownTimer timer = null;

	private OnClickListener clickListner = null;

	public SwitchButton(Context context) {
		this(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 初始化
		if (findViews()) {
			initView();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// 如果right和bottom已经被设置过，需要重新在这里设置cursor的位置，不然画面刷新后动态设置的位置会被重置
		if (cursor_right > 0 && cursor_bottom > 0) {
			iv_switch_cursor.layout(cursor_left, cursor_top, cursor_right, cursor_bottom);
		}

		// 获取所需要的值
		bg_left = sv_container.getLeft();
		bg_right = sv_container.getRight();
		cursor_left = iv_switch_cursor.getLeft();
		cursor_top = iv_switch_cursor.getTop();
		cursor_right = iv_switch_cursor.getRight();
		cursor_bottom = iv_switch_cursor.getBottom();
	}

	private int getCursorWidth() {
		if (cursorWidth == 0) {
			cursorWidth = iv_switch_cursor.getWidth();
		}
		return cursorWidth;
	}

	@Override
	public void onClick(View arg0) {
		if (clickListner != null) {
			clickListner.onClick(arg0);
		}
		changeChecked(!isChecked);
	}

	/**
	 * 设置文本大小
	 */
	public void setTextSize(float textSize) {
		this.textSize = textSize;
		initialSize();
	}

	public void setText(CharSequence textOn, CharSequence textOff) {
		switch_text_true.setText(textOn);
		switch_text_false.setText(textOff);
	}

	// 找到所有的组件
	private boolean findViews() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lt_component_switchbutton, this);
		// 设置点击之后，变更选择状态。
		super.setOnClickListener(this);

		sv_container = (RelativeLayout) view.findViewById(R.id.sv_container);
		switch_text_true = (TextView) view.findViewById(R.id.switch_text_true);
		switch_text_false = (TextView) view.findViewById(R.id.switch_text_false);

		iv_switch_cursor = (ImageView) view.findViewById(R.id.iv_switch_cursor);
		if (SettingUtils.hasEmpty(sv_container, switch_text_false, switch_text_true, iv_switch_cursor)) {
			return false;
		}
		return true;
	}

	// 初始化组件的大小
	private void initialSize() {

		if (textSize <= 0) {
			textSize = getResources().getDimensionPixelSize(R.dimen.gl_text_size_22sp);
		}
		
		if (padding <= 0) {
			padding = getResources().getDimensionPixelSize(R.dimen.gl_small_padding_4dp);
		}

		float height = textSize * 2 + padding;
		int containerWidth = (int) (height / 0.6);
		int containerHeight = (int) height;

		sv_container.getLayoutParams().width = containerWidth * 2;
		sv_container.getLayoutParams().height = containerHeight;

		iv_switch_cursor.getLayoutParams().width = containerWidth;
		iv_switch_cursor.getLayoutParams().height = containerHeight - 2;

		cursorWidth = containerWidth;

		switch_text_true.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		switch_text_false.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

		changeTextColor();
	}

	private void initView() {
		// 设置控件大小
		initialSize();

		// 游标不能可以点击，只能拖动。
		iv_switch_cursor.setClickable(false);
		iv_switch_cursor.setOnTouchListener(new OnTouchListener() {

			int lastX; // 最后的X坐标

			public boolean onTouch(View v, MotionEvent event) {
				if (timer == null) {
					timer = new FingerMoveTimer();
				} else {
					timer.cancel();
					timer = null;
					timer = new FingerMoveTimer();
				}
				timer.start();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();

					cursor_left = v.getLeft();
					cursor_top = v.getTop();
					cursor_right = v.getRight();
					cursor_bottom = v.getBottom();
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;

					cursor_left = v.getLeft() + dx;
					cursor_right = v.getRight() + dx;

					// 超出边界处理
					if (cursor_left <= bg_left + margin) {
						cursor_left = bg_left + margin;
						cursor_right = cursor_left + v.getWidth();
					}
					if (cursor_right >= bg_right - margin) {
						cursor_right = bg_right - margin;
						cursor_left = cursor_right - v.getWidth();
					}

					v.layout(cursor_left, cursor_top, cursor_right, cursor_bottom);

					lastX = (int) event.getRawX();
					break;
				case MotionEvent.ACTION_UP:
					timer.cancel();

					calculateIscheck();
					break;
				}
				return true;
			}
		});
	}

	/**
	 * 设置按钮在表示“真”和“假”的时候显示的文本。
	 */
	public void setTextForValue(String text, boolean value) {
		if (text.equals("")) {
			return;
		}
		if (value) {
			switch_text_true.setText(text);
		} else {
			switch_text_false.setText(text);
		}
	}

	/**
	 * 改变字体显示颜色
	 */
	private void changeTextColor() {
		if (isChecked) {
			switch_text_true.setTextColor(getResources().getColor(R.color.text_button_highlight));
			switch_text_false.setTextColor(getResources().getColor(R.color.text_dark));
		} else {
			switch_text_true.setTextColor(getResources().getColor(R.color.text_dark));
			switch_text_false.setTextColor(getResources().getColor(R.color.text_button_highlight));
		}
	}

	/**
	 * 计算处于true或是false区域, 并做改变处理
	 */
	private void calculateIscheck() {
		float center = (float) ((bg_right - bg_left) / 2.0);
		float cursor_center = (float) ((cursor_right - cursor_left) / 2.0);
		if (cursor_left + cursor_center <= center) {
			changeChecked(true);
		} else {
			changeChecked(false);
		}
	}

	public void changeChecked(boolean isChecked) {
		changeChecked(isChecked, true);
	}

	/**
	 * 改变checked, 根据checked移动游标
	 * 
	 * @param isChecked
	 */
	public void changeChecked(boolean isChecked, boolean fireEvent) {
		// if (this.isChecked == isChecked) {
		// return;
		// }
		if (onCheckedChangeListener != null && fireEvent) {
			boolean cancel = onCheckedChangeListener.onCheckedChanging(this, isChecked);
			if (cancel) {
				if (this.isChecked) {
					currentFlag = FLAG_MOVE_TRUE;
				} else {
					currentFlag = FLAG_MOVE_FALSE;
				}

				// 动画形式，移动游标
				cursorMove();
				return;
			}
		}

		this.isChecked = isChecked;

		// 如果是选中状态，向True的方向移动，否则移向False
		if (isChecked) {
			currentFlag = FLAG_MOVE_TRUE;
		} else {
			currentFlag = FLAG_MOVE_FALSE;
		}

		// 动画形式，移动游标
		cursorMove();

		// 改变控件字体颜色
		changeTextColor();

		if (onCheckedChangeListener != null && fireEvent) {
			onCheckedChangeListener.onCheckedChanged(this, isChecked);
		}
	}

	/**
	 * 游标移动
	 */
	private void cursorMove() {
		// 这里说明一点, 动画本可设置animation.setFillAfter(true)
		// 令动画进行完后停在最后位置. 但这里使用这样方式的话.
		// 再次拖动图片会出现异常(具体原因我没找到)
		// 所以最后只能使用onAnimationEnd回调方式再layout游标
		animation = null;
		final int toX;
		if (currentFlag == FLAG_MOVE_TRUE) {
			toX = cursor_left - bg_left - margin;
			animation = new TranslateAnimation(0, -toX, 0, 0);
		} else {
			toX = bg_right - margin - cursor_right;
			animation = new TranslateAnimation(0, toX, 0, 0);
		}
		animation.setDuration(100);
		animation.setInterpolator(new LinearInterpolator());
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// 计算动画完成后游标应在的位置
				if (currentFlag == FLAG_MOVE_TRUE) {
					cursor_left -= toX;
					cursor_right = cursor_left + getCursorWidth();
				} else {
					cursor_right = bg_right - margin;
					cursor_left = cursor_right - getCursorWidth();
				}
				// 这里不能马上layout游标正确位置, 否则会有一点点闪屏
				// 为了美观, 这里迟了一点点调用layout方法, 便不会闪屏
				iv_switch_cursor.postDelayed(new Runnable() {
					@Override
					public void run() {
						iv_switch_cursor.layout(cursor_left, cursor_top, cursor_right, cursor_bottom);
					}
				}, 5);
			}
		});
		iv_switch_cursor.startAnimation(animation);
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	@Override
	public void setOnClickListener(OnClickListener onclickListner) {
		clickListner = onclickListner;
	}

	/**
	 * isChecked值改变监听器
	 */
	public interface OnCheckedChangeListener {

		void onCheckedChanged(SwitchButton sb, boolean changeTo);

		boolean onCheckedChanging(SwitchButton sb, boolean changeTo);
	}

	/**
	 * 手指移动的计时器
	 * */
	private class FingerMoveTimer extends CountDownTimer {

		public FingerMoveTimer() {
			super(200, 50);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			invalidate();
		}

		@Override
		public void onFinish() {
			calculateIscheck();
		}
	}
}
