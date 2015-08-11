package com.zhiyisoft.associations.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.zhiyisoft.associations.R;
import com.zhiyisoft.associations.activity.base.BaseActivity;
import com.zhiyisoft.associations.adapter.MyViewPagerAdapter;
import com.zhiyisoft.associations.adapter.base.BAdapter;
import com.zhiyisoft.associations.fragment.FragmentMoveMyCreate;
import com.zhiyisoft.associations.fragment.FragmentMoveMyJoin;
import com.zhiyisoft.associations.fragment.FragmentMoveMyWatch;
import com.zhiyisoft.associations.fragment.base.BaseFragment;
import com.zhiyisoft.associations.listview.base.BaseListView;
import com.zhiyisoft.associations.model.base.Model;
import com.zhiyisoft.associations.util.UIUtils;

/**
 * author：qiuchunjia time：上午9:53:45 类描述：这个类是实现
 *
 */

public class MoveMyActivity extends BaseActivity {
	private TextView tv_my_join;
	private TextView tv_my_watch;
	private TextView tv_my_create;
	private TextView mTextBottemLine;
	private ViewPager mViewPager;
	private BaseListView works_display_lv;
	private List<Model> mlist = new ArrayList<Model>();
	private BAdapter mAdapter;
	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	public String setCenterTitle() {
		return "我的活动";
	}

	@Override
	public void initIntent() {

	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_move_my;
	}

	@Override
	public void initView() {
		tv_my_join = (TextView) findViewById(R.id.tv_my_join);
		tv_my_watch = (TextView) findViewById(R.id.tv_my_watch);
		tv_my_create = (TextView) findViewById(R.id.tv_my_create);
		mTextBottemLine = (TextView) findViewById(R.id.bottom_line);
		initViewPager();
	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.move_my_viewpager);
		mFragments.add(new FragmentMoveMyJoin());
		mFragments.add(new FragmentMoveMyWatch());
		mFragments.add(new FragmentMoveMyCreate());
		mViewPager.setAdapter(new MyViewPagerAdapter(mFManager, mFragments));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private float mCurrentDes = 0;
			private float moveWidth = UIUtils
					.getWindowWidth(getApplicationContext()) / 3;

			@Override
			public void onPageSelected(int pos) {
				mViewPager.setCurrentItem(pos);
				Animation animation = null;
				switch (pos) {
				case 0:
					animation = new TranslateAnimation(mCurrentDes, 0, 0, 0);
					mCurrentDes = 0 * moveWidth;
					break;
				case 1:
					animation = new TranslateAnimation(mCurrentDes, moveWidth,
							0, 0);
					mCurrentDes = 1 * moveWidth;
					break;
				case 2:
					animation = new TranslateAnimation(mCurrentDes,
							moveWidth * 2, 0, 0);
					mCurrentDes = 2 * moveWidth;
					break;
				}
				animation.setFillAfter(true);
				animation.setDuration(300);
				mTextBottemLine.startAnimation(animation);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void initListener() {
		tv_title_right.setOnClickListener(this);
		tv_my_join.setOnClickListener(this);
		tv_my_watch.setOnClickListener(this);
		tv_my_create.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			initPopWindow();
			showPop(tv_title_right, 0, 10);
			break;
		// --------------------------PopupWindow的界面控件监听器------------------
		case R.id.ll_essay:
			Bundle data = new Bundle();
			mApp.startActivity(this, MoveSendEssayActivity.class, data);
			break;
		case R.id.ll_pic:
			mApp.startActivity(this, MoveSendPhotoActivity.class, null);
			break;
		case R.id.ll_music:
			Bundle data2 = new Bundle();
			mApp.startActivity(this, MoveSendMusicActivity.class, data2);
			break;
		case R.id.ll_vedio:
			Bundle data3 = new Bundle();
			mApp.startActivity(this, MoveSendVedioActivity.class, data3);
			break;
		case R.id.tv_my_join:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.tv_my_watch:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.tv_my_create:
			mViewPager.setCurrentItem(2);
			break;
		}

	}

	// --------------------------PopupWindow的界面控件-----------------------------------------
	private PopupWindow mPopupWindow;
	private LinearLayout ll_essay;
	private LinearLayout ll_pic;
	private LinearLayout ll_music;
	private LinearLayout ll_vedio;

	/**
	 * 初始化popWindow
	 * */
	private void initPopWindow() {
		if (mPopupWindow == null) {
			View popView = mInflater.inflate(R.layout.move_works_send_style,
					null);
			mPopupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					200);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					R.color.main_gray_color));
			mPopupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					mPopupWindow = null;
					setWindowAlpha(1);
				}
			});
			setWindowAlpha(0.7f);
			// 设置popwindow出现和消失动画
			initPopWidge(popView);
			setPopListener();
		}
	}

	/**
	 * 设置popWindow监听器
	 */
	private void setPopListener() {
		ll_essay.setOnClickListener(this);
		ll_pic.setOnClickListener(this);
		ll_music.setOnClickListener(this);
		ll_vedio.setOnClickListener(this);
	}

	/**
	 * 初始化popwindow里面的控件
	 * 
	 * @param popView
	 */
	private void initPopWidge(View popView) {
		ll_essay = (LinearLayout) popView.findViewById(R.id.ll_essay);
		ll_pic = (LinearLayout) popView.findViewById(R.id.ll_pic);
		ll_music = (LinearLayout) popView.findViewById(R.id.ll_music);
		ll_vedio = (LinearLayout) popView.findViewById(R.id.ll_vedio);
	}

	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent, int x, int y) {
		// 设置popwindow显示位置
		mPopupWindow.showAsDropDown(parent, x, y);
		// 获取popwindow焦点
		mPopupWindow.setFocusable(true);
		// 设置popwindow如果点击外面区域，便关闭。
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
	}

	/**
	 * 设置屏幕的透明度
	 * 
	 * @param alpha
	 *            需要设置透明度
	 */
	private void setWindowAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}

}
