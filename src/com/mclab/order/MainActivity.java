package com.mclab.order;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mclab.order.entity.Order;
import com.mclab.order.entity.OrderManager;
import com.mclab.order.entity.UserLoginStatusManager;
import com.mclab.order.network.NetConfig;
import com.mclab.order.network.NetStatusManager;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int ORDER_SUCCESS = 0;
	private static final int WS_CONN_SUCCESS = 1;

	private TextView mTVshowNum;
	private Button mBTNCall;

	AtomicInteger mAtomicInteger = new AtomicInteger();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ORDER_SUCCESS:
				processOrder((String) msg.obj);
				break;

			case WS_CONN_SUCCESS:
				Toast.makeText(getApplicationContext(), "已经连接到服务器",
						Toast.LENGTH_LONG).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		if (!UserLoginStatusManager.getInstance(getApplicationContext())
				.getIsLogin()) {

			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
		}

	}

	protected void onResume() {
		super.onResume();

		if (NetStatusManager.isNetWorkAviable) {
			initWSClient();
		} else {
			Toast.makeText(this, "网络没有准备好，请检查网络后重启应用", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void init() {

		mTVshowNum = (TextView) findViewById(R.id.tv_showNum_activity_main);
		mBTNCall = (Button) findViewById(R.id.btn_order_activity_main);

		mBTNCall.setOnClickListener(this);

	}

	public WSClient mWSClient = null;

	private void initWSClient() {
		Log.e(TAG, "login url = " + NetConfig.WEBSOCKETURL);
		if (mWSClient != null) {
			return;
		}
		try {

			mWSClient = new WSClient(this, mHandler, new URI(
					NetConfig.WEBSOCKETURL), new Draft_17());
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "login url = " + NetConfig.WEBSOCKETURL);
			}

			mWSClient.connect();
			Log.e(TAG, "client");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			Log.e(TAG, "client e2");
		}
		Log.e(TAG, "client s1");

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_order_activity_main) {
			mAtomicInteger.set(12);

			for (int i = 1; i <= 12; i++) {
				OrderManager.getInstance().add(i + "",
						new Order(i + "", i, true));
			}

			Toast.makeText(this, "不用真点12次吧，模拟一下，直接12次了。", Toast.LENGTH_LONG)
					.show();
			mBTNCall.setClickable(false);
			setTVShowNum();
		}
	}

	private void setTVShowNum() {
		String mAlreadyCallString = getResources().getString(
				R.string.already_called);

		mTVshowNum.setText(String.format(mAlreadyCallString,
				mAtomicInteger.get()));
	}

	public void processOrder(String orderId) {

		int num = mAtomicInteger.incrementAndGet();
		Order order = new Order(orderId, num, true);
		OrderManager.getInstance().add(orderId, order);
		setTVShowNum();

		try {

			JSONObject json = new JSONObject();
			json.put("type", "orderesponse");

			JSONObject data = new JSONObject();
			data.put("result", "0");
			data.put("num", num);
			data.put("orderid", orderId);

			json.put("data", data);

			mWSClient.send(json.toString());

		} catch (JSONException e) {
			Log.e(TAG, "processOrder:" + e.getMessage());
		}

	}

	class WSClient extends WebSocketClient {

		private Context mContext;
		private Handler mHandler;

		public WSClient(Context context, Handler handler, URI serverUri,
				Draft draft) {
			super(serverUri, draft);
			mContext = context;
			mHandler = handler;
		}

		public WSClient(URI serverURI) {
			super(serverURI);
		}

		@Override
		public void onOpen(ServerHandshake handshakedata) {
			Log.e(TAG, "opened connection");
			Message msg = Message.obtain();
			msg.what = ORDER_SUCCESS;
			mHandler.sendMessage(msg);
			sendAuthInfo();
		}

		private void sendAuthInfo() {
			JSONObject json = new JSONObject();

			try {
				json.put("type", "auth");

				JSONObject userInfoObject = new JSONObject();

				userInfoObject.put("userid", UserLoginStatusManager
						.getInstance(mContext).getUserId());
				userInfoObject.put("passwd", UserLoginStatusManager
						.getInstance(mContext).getPasswd());

				json.put("data", userInfoObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (BuildConfig.DEBUG) {
				Log.e(TAG, json.toString());
			}
			send(json.toString());

		}

		@Override
		public void onMessage(String message) {

			try {
				JSONObject json = new JSONObject(message);

				String type = json.getString("type");
				JSONObject data = (JSONObject) json.get("data");
				if (type.equals("orderrequest")) {
					String orderId = data.getString("orderid");

					Message msg = Message.obtain();
					msg.obj = orderId;
					msg.what = ORDER_SUCCESS;
					mHandler.sendMessage(msg);

				}

			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

		}

		@Override
		public void onFragment(Framedata fragment) {
			Log.e(TAG, "received fragment: "
					+ new String(fragment.getPayloadData().array()));
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			Log.e(TAG, "Connection closed by "
					+ (remote ? "remote peer" : "us"));
		}

		@Override
		public void onError(Exception ex) {
			ex.printStackTrace();
		}

	}

}
