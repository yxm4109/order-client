package com.mclab.order.entity;

import java.util.HashMap;

public class OrderManager {

	@SuppressWarnings("unused")
	private static final String TAG = UserLoginStatusManager.class
			.getSimpleName();

	private static OrderManager orderManager = null;

	private HashMap<String, Order> mMap;

	private OrderManager() {
		mMap = new HashMap<String, Order>();
	}

	public static OrderManager getInstance() {
		if (orderManager == null) {
			orderManager = new OrderManager();
		}
		return orderManager;
	}

	public void add(String ID, Order order) {
		mMap.put(ID, order);
		saveToDB();
	}

	private void saveToDB() {
		// 占个坑

	}

	public int getTotal() {
		return mMap.size();
	}

}
