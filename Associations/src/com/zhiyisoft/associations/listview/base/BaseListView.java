package com.zhiyisoft.associations.listview.base;

/***********************************************************************
 * Module:  BaseListView.java
 * Author:  qcj qq:260964739
 * Purpose: Defines the Class BaseListView
 ***********************************************************************/

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.zhiyisoft.associations.R;
import com.zhiyisoft.associations.activity.base.BaseActivity;
import com.zhiyisoft.associations.adapter.base.BAdapter;
import com.zhiyisoft.associations.application.Association;
import com.zhiyisoft.associations.model.base.Model;
import com.zhiyisoft.associations.widget.XListView;

/** listview的基类 ，任何listview都可以继承它，减少代码的冗余 */
public abstract class BaseListView extends XListView implements
		XListView.IXListViewListener {

	public BaseListView(Context context) {
		super(context, null);
		mContext = context;
		initSet(context);
		initXListView();
	}

	public BaseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initSet(context);
		initXListView();
	}

	private Context mContext;

	// /** mlist數據 */
	private List<Model> mList;
	/** activity */
	private BaseActivity mActivity;
	private BAdapter mAdapter;
	/**
	 * 最后可见的位置
	 */
	private int mLastVisiablPos;

	public Association mApp;

	/** 初始化设置 */
	public void initSet(Context context) {
		this.setScrollbarFadingEnabled(true);
		this.setCacheColorHint(0);
		this.setDividerHeight(2);
		mApp = (Association) context.getApplicationContext();
		mActivity = mApp.getActivity();
		mApp.setActivity(mActivity);
	}

	private void initXListView() {
		this.setPullRefreshEnable(true);
		this.setPullLoadEnable(true);
		this.setAutoLoadEnable(true);
		this.setRefreshTime(getTime());
		this.setXListViewListener(this);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.mAdapter = (BAdapter) adapter;
		mAdapter.setListView(this);
		this.mList = ((BAdapter) adapter).getList();
		Log.i("hhh", "---------------走到这里没有？setAdapter(ListAdapter adapter)");
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击加载更多
				if (view.getId() == R.id.footer_content) {
					Toast.makeText(mContext, "点击了加載更多", Toast.LENGTH_LONG)
							.show();
					return;
				}
				// 子类去实现他
				onClick(parent, view, position, id);
			}
		});

	}

	/** 重新每一個item的點擊事件 */
	public abstract void onClick(AdapterView<?> parent, View view,
			int position, long id);

	@Override
	public void onRefresh() {
		if (mAdapter != null) {
			mAdapter.doRefreshHeader();
		}
	}

	@Override
	public void onLoadMore() {
		if (mAdapter != null) {
			mAdapter.doRefreshFooter();
		}

	}

	private String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
				.format(new Date());
	}

	/**
	 * 取消下拉刷新 或者加载更多
	 */
	public void onLoad() {
		this.stopRefresh();
		this.stopLoadMore();
		this.setRefreshTime(getTime());
	}
}
