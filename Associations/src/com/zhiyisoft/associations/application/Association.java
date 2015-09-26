package com.zhiyisoft.associations.application;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import com.zhiyisoft.associations.activity.base.BaseActivity;
import com.zhiyisoft.associations.api.Api;
import com.zhiyisoft.associations.api.Api.EventImpl;
import com.zhiyisoft.associations.cache.base.DiskLruCache;
import com.zhiyisoft.associations.config.Config;
import com.zhiyisoft.associations.model.ModelUser;
import com.zhiyisoft.associations.util.Anim;

/**
 * author：qiuchunjia time：下午2:16:14 类描述：
 * 这个类是应用的application用來存一下常用的變量，獲取用戶認證等等子類的
 *
 */

public class Association extends Application {
	private BaseActivity mActivity;
	private static Association mApp;
	/** 定义一个user，整個app都用它 */
	private static ModelUser mUser;

	/** 定义一个线程池，整個app都用它 */
	private ExecutorService mExecutor;
	public static HttpClient mHttpClient;
	/** api区域 */
	private Api.LoginImpl mLogin;
	private Api.LeagueImpl mLeague;
	private Api.EventImpl mEvent;
	private Api.SchoolImpl mSchool;
	private Api.BaseSettingImpl mBaseSetting;
	private Api.RegisterImpl mRegister;

	/** api区域结束 */
	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
	}

	/**
	 * @return 返回上下文
	 */
	public static Context getContext() {
		return mApp;
	}

	public void setActivity(BaseActivity activity) {
		this.mActivity = activity;
	}

	public BaseActivity getActivity() {
		return mActivity;
	}

	private static DiskLruCache mDiskCache;
	// 让内存有100m 最开始把单位设置错了设置为8b，导致存不进去，卧槽 ，fuck，浪费老子两个小时！
	private int mDiskCacheSize = 100 * 1024 * 1024;
	private String mFileName = "Association";

	// ------------------------获取缓存-----------------------------------------------------------
	public DiskLruCache getCache() {
		if (mDiskCache == null) {
			File file = getDiskCacheDir(getApplicationContext(), mFileName);
			if (!file.exists()) {
				file.mkdirs();
			}
			try {
				mDiskCache = DiskLruCache.open(file,
						getAppVersion(getApplicationContext()), 1,
						mDiskCacheSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mDiskCache;
	}

	/**
	 * 获取使用过的缓存
	 * 
	 * @return
	 */
	public float getUsedCache() {
		mDiskCache = getCache();
		float count = mDiskCache.size();
		float mCountM = count / (1024 * 1024);
		float b = (float) (Math.round(mCountM * 100)) / 100; // 取两位小数
		return b;
	}

	/**
	 * 清理掉缓存
	 * 
	 * @return
	 */
	public boolean cleanCache() {
		mDiskCache = getCache();
		try {
			mDiskCache.delete();
			mDiskCache = null;
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取app版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 获取内存卡路径
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	@SuppressLint("NewApi")
	private File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	// ------------------------获取缓存 end------------------------------------
	/**
	 * 获取用户的信息，主要是获取手机号码，登录密码，认证信息
	 * 
	 * @return
	 */
	public static ModelUser getUser() {

		mUser = new ModelUser();
		SharedPreferences preferences = getContext().getSharedPreferences(
				Config.USER_DATA, MODE_PRIVATE);
		String mobile = preferences.getString(Config.MOBILE, null);
		String pwd = preferences.getString(Config.PWD, null);
		String userId = preferences.getString(Config.USERID, null);
		String oauth_token = preferences.getString(Config.OAUTH_TOKEN, null);
		String oauth_token_secret = preferences.getString(
				Config.OAUTH_TOKEN_SECRET, null);
		String school_id = preferences.getString(Config.SCHOOL_ID, null);
		String uname = preferences.getString(Config.UNAME, null);
		String sex = preferences.getString(Config.SEX, null);
		String is_init = preferences.getString(Config.IS_INIT, null);
		String faceurl = preferences.getString(Config.FACEURL, null);
		String school_name = preferences.getString(Config.SCHOOL_NAME, null);
		String autograph = preferences.getString(Config.AUTOGRAPH, null);
		String email = preferences.getString(Config.EMAIL, null);
		mUser.setMobile(mobile);
		mUser.setPwd(pwd);
		mUser.setUserid(userId);
		mUser.setOauth_token(oauth_token);
		mUser.setOauth_token_secret(oauth_token_secret);
		mUser.setschool_id(school_id);
		mUser.setUname(uname);
		mUser.setSex(sex);
		mUser.setIs_init(is_init);
		mUser.setFaceurl(faceurl);
		mUser.setSchool_name(school_name);
		mUser.setAutograph(autograph);
		mUser.setEmail(email);
		// 如果mobile为为空的话，就说明根本就没有登录过，这个时候就把muser设置为空，到时候供外不调用
		System.out.println(mUser.toString());
		return mUser;
	}

	/**
	 * 把获取的user保存到SharePreference，方便下次调用
	 * 
	 * @param user
	 *            需要保存的用户
	 */
	public void saveUser(ModelUser user) {
		SharedPreferences preferences = mActivity.getSharedPreferences(
				Config.USER_DATA, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		if (user.getMobile() != null) {
			editor.putString(Config.MOBILE, user.getMobile());
		}
		if (user.getPwd() != null) {
			editor.putString(Config.PWD, user.getPwd());
		}

		editor.putString(Config.USERID, user.getUserid());
		editor.putString(Config.OAUTH_TOKEN, user.getOauth_token());
		editor.putString(Config.OAUTH_TOKEN_SECRET,
				user.getOauth_token_secret());
		editor.putString(Config.SCHOOL_ID, user.getschool_id());
		editor.putString(Config.UNAME, user.getUname());
		editor.putString(Config.SEX, user.getSex());
		editor.putString(Config.IS_INIT, user.getIs_init());
		editor.putString(Config.FACEURL, user.getFaceurl());
		editor.putString(Config.SCHOOL_NAME, user.getSchool_name());
		editor.putString(Config.AUTOGRAPH, user.getAutograph());
		editor.putString(Config.EMAIL, user.getEmail());
		editor.commit();
	}

	public ExecutorService getExecutor() {
		if (mExecutor == null) {
			// 获取当前系统的CPU 数目 （这样更加考虑各个手机的性能问题）
			int cpuNums = Runtime.getRuntime().availableProcessors();
			mExecutor = Executors.newFixedThreadPool(cpuNums * 4);
		}
		return mExecutor;
	}

	/**
	 * 关闭线程
	 */
	public void shutDownExecutor() {
		if (mExecutor != null) {
			mExecutor.shutdownNow();
		}
	}

	/**
	 * @return 获取主机地址
	 */
	public static String getHostUrl() {
		// TODO 以后把地址写到xml里面
		return "http://daxs.zhiyicx.com/index.php";
	}

	/**
	 * 08-15 06:52:01.629: W/SingleClientConnManager(2450): Invalid use of
	 * SingleClientConnManager: connection still allocated.
	 * 报错信息为采用单例的ingleClientConnManager，连接还在持续中
	 * 
	 * ，控制mHttpClient被一个线程访问完后才能被其它线程访问
	 * 
	 * */
	public static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			HttpParams params = new BasicHttpParams();
			ConnManagerParams.setMaxTotalConnections(params, 100);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			// Create an HttpClient with the ThreadSafeClientConnManager.
			// This connection manager must be used if more than one thread will
			// be using the HttpClient.
			ClientConnectionManager cm = new ThreadSafeClientConnManager(
					params, schemeRegistry);
			mHttpClient = new DefaultHttpClient(cm, params);
		}
		return mHttpClient;
	}

	// -----------------------------获取api------------------------------------------------------
	public Api.LoginImpl getLoginIm() {
		if (mLogin == null) {
			mLogin = new Api.LoginImpl();
		}
		return mLogin;
	}

	public Api.LeagueImpl getLeagueIm() {
		if (mLeague == null) {
			mLeague = new Api.LeagueImpl();
		}
		return mLeague;
	}

	public EventImpl getEventFIm() {
		if (mEvent == null) {
			mEvent = new Api.EventImpl();
		}
		return mEvent;
	}

	public Api.SchoolImpl getmSchoolIm() {
		if (mSchool == null) {
			mSchool = new Api.SchoolImpl();
		}
		return mSchool;
	}

	public Api.BaseSettingImpl getmBaseSettingIm() {
		if (mBaseSetting == null) {
			mBaseSetting = new Api.BaseSettingImpl();
		}
		return mBaseSetting;
	}

	public Api.RegisterImpl getRegisterIm() {
		if (mRegister == null) {
			mRegister = new Api.RegisterImpl();
		}
		return mRegister;
	}

	// -----------------------------获取api 结束-----------------------------
	public void startActivity(Activity now, Class<? extends Activity> target,
			Bundle data) {
		Intent intent = new Intent();
		intent.setClass(now, target);
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
		System.out.println("now" + now);
		System.out.println("target" + target);
		Anim.in(now);
	}

	public void startActivity(Activity now, Class<? extends Activity> target,
			Bundle data, int flag) {
		Intent intent = new Intent();
		intent.setClass(now, target);
		intent.setFlags(flag); // 注意本行的FLAG设置
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
		Anim.in(now);
	}

	public void startActivityForResult(Activity now,
			Class<? extends Activity> target, Bundle data) {
		Intent intent = new Intent();
		intent.setClass(now, target);
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivityForResult(intent, BaseActivity.GET_DATA_FROM_ACTIVITY);
		Anim.in(now);
	}

	/**
	 * 判断网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static String getCache_path() {
		return "cache_path";
	}
}
