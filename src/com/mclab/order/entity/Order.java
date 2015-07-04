package com.mclab.order.entity;

public class Order {

	private String id;
	private int num;
	private boolean isDealed;

	public String getId() {
		return id;
	}

	public int getNum() {
		return num;
	}

	public boolean getIsDealed() {
		return isDealed;
	}

	public void setDealed(boolean isDealed) {
		this.isDealed = isDealed;
	}

	public Order(String id, int num, boolean isDealed) {
		super();
		this.id = id;
		this.num = num;
		this.isDealed = isDealed;
	}

}
