package com.mclab.order;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mclab.order.entity.UserLoginStatusManager;
import com.mclab.order.network.NetConfig;
import com.mclab.order.network.NetStatusManager;
import com.mclab.order.tools.InputStreamUtils;
import com.mclab.order.tools.PasswdTools;

public class LoginActivity extends Activity implements OnClickListener {

	
	private static final String TAG = LoginActivity.class.getSimpleName();
	private static final int LOGIN_OK = 0;
	private static final int LOGIN_FAIL = 1;

	private ProgressDialog mProcessDialog;

	private EditText mETUserId;
	private EditText mETPasswd;
	private Button mBTNLogin;

	UserLoginStatusManager ulsm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		init();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	
	private void init() {
		ulsm = UserLoginStatusManager.getInstance(getApplicationContext());
		mETPasswd = (EditText) findViewById(R.id.et_passwd_login);
		mETUserId = (EditText) findViewById(R.id.et_userid_login);
		mBTNLogin = (Button) findViewById(R.id.btn_login_login);

		mBTNLogin.setOnClickListener(this);

		mProcessDialog = new ProgressDialog(this);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_login_login) {

			String userId = mETUserId.getText().toString().trim();
			String passwd = PasswdTools.getEncryptedPasswd(mETPasswd.getText()
					.toString().trim());

			// *******没开放注册，所以就在这把用户名写死了*******
			userId = "1234";
			passwd = "1234";

			ulsm.setUserId(userId);
			ulsm.setPasswd(passwd);

			LoginTask loginTask = new LoginTask(userId, passwd, this);

			loginTask.execute();

			mProcessDialog.show();

		}
	}

	private void processLoginResult(boolean isLoginSuccess) {

		if (isLoginSuccess) {

			ulsm.setIsLgin(isLoginSuccess);
			finish();
		} else {
			Toast.makeText(this, "失败了，重新试一下吧！", Toast.LENGTH_SHORT).show();
			mProcessDialog.cancel();
		}

	}

	private boolean checkNetStatus(Context context) {
		int netStatus = NetStatusManager.getNetState(getApplicationContext());

		if (netStatus == NetStatusManager.NET_CNNT_OK) {
			return true;
		} else {
			Looper.prepare();
			Toast.makeText(context, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
			mProcessDialog.cancel();
			Looper.loop();
			
			return false;
		}

	}

	class LoginTask extends AsyncTask<Void, Void, Integer> {

		String mUserId;
		String mPasswd;

		Context mContext;

		public LoginTask(String userId, String passwd, Context context) {
			mUserId = userId;
			mPasswd = passwd;
			mContext = context;
		}

		@Override
		protected void onPostExecute(Integer result) {

			super.onPostExecute(result);

			if (result==LOGIN_OK) {
				processLoginResult(true);
			} else {
				processLoginResult(false);
			}

		}

		@Override
		protected Integer doInBackground(Void... params) {

			if (!checkNetStatus(mContext)) {
				return LOGIN_FAIL;
			}
			HttpURLConnection httpUrl = null;
			try {
				httpUrl = (HttpURLConnection) new URL(NetConfig.getLoginURL(
						mUserId, mPasswd)).openConnection();
				httpUrl.setConnectTimeout(NetConfig.TIMEOUT);
				httpUrl.connect();
				try {
					String reString = InputStreamUtils
							.InputStreamTOString(httpUrl.getInputStream());
					
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		            Matcher m = p.matcher(reString);
		            String dest = m.replaceAll("");
					
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "resString=" + reString);
					}
					return Integer.parseInt(dest);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != httpUrl) {
					httpUrl.disconnect();
				}
				httpUrl = null;
			}

			return LOGIN_FAIL;
		}
	}

}