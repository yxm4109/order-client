package com.mclab.order.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserLoginStatusManager {

	@SuppressWarnings("unused")
	private static final String TAG = UserLoginStatusManager.class
			.getSimpleName();
	private static final String USERID = "userid";
	private static final String PASSWD = "passwd";
	private static final String ISLOGIN = "islogin";

	private static UserLoginStatusManager userLoginStatusManager = null;
	private Context mContext;

	private SharedPreferences mSp;

	private String mUserId = null;
	private String mPasswd = null;
	private boolean mIsLogin = false;

	Editor mEditor = null;

	private UserLoginStatusManager(Context context) {
		mContext = context;
		mSp = mContext.getSharedPreferences("userlogin", Context.MODE_PRIVATE);
		mUserId = mSp.getString(USERID, null);
		mPasswd = mSp.getString(PASSWD, null);
		mIsLogin = mSp.getBoolean(ISLOGIN, false);
		mEditor = mSp.edit();
	}

	public static UserLoginStatusManager getInstance(Context context) {
		if (userLoginStatusManager == null) {
			userLoginStatusManager = new UserLoginStatusManager(context);
		}
		return userLoginStatusManager;
	}

	public String getUserId() {
		return mUserId;
	}

	public String getPasswd() {
		return mPasswd;
	}

	public boolean getIsLogin() {
		return mIsLogin;
	}

	public void setUserId(String userId) {
		mUserId = userId;
		mEditor.putString("userid", userId);
		mEditor.apply();
	}

	public void setPasswd(String passwd) {
		mPasswd = passwd;
		mEditor.putString("passwd", passwd);
		mEditor.apply();
	}

	public void setIsLgin(boolean isLogin) {
		mIsLogin = isLogin;
		mEditor.putBoolean(ISLOGIN, isLogin);
		mEditor.apply();
	}

}
