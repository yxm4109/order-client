package com.mclab.order.network;

import android.content.Context;

public class NetWorkStatusThread extends Thread{

	Context mContext=null;
	
	public NetWorkStatusThread(Context context){
		mContext = context;
	}
	
	@Override
	public void run() {

		super.run();
		
		int gapTime=5*1000;
		
		while(true){
			NetStatusManager.getNetState(mContext);
			
			try {
				Thread.sleep(gapTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
