package com.mclab.order.network;

import android.util.Log;

import com.mclab.order.BuildConfig;

public class NetConfig {

	private static String TAG = NetConfig.class.getSimpleName();

	public static final String SERVERIP_PORT = "138.128.205.173//mma-server";

	public static final String BASEURL = "http://" + SERVERIP_PORT + "/";

	public static final int TIMEOUT = 30 * 1000;
	public static final String LOGINURL = BASEURL + "login";

	public static String getLoginURL(String userid, String passwd) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(LOGINURL);
		stringBuilder.append("?");
		stringBuilder.append("userid=" + userid);
		stringBuilder.append("&");
		stringBuilder.append("passwd=" + passwd);

		if (BuildConfig.DEBUG) {
			Log.e(TAG, "login url = " + stringBuilder.toString());
		}

		return stringBuilder.toString();

	}

	public static final String WEBSOCKETURL = "ws://" + SERVERIP_PORT
			+ "/WebSocketServelet";

}