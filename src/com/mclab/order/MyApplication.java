package com.mclab.order;

import com.mclab.order.network.NetWorkStatusThread;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		NetWorkStatusThread nwst = new NetWorkStatusThread(this);
		nwst.start();
	}

}
