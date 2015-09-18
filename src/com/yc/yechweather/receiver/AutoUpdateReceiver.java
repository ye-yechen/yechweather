package com.yc.yechweather.receiver;

import com.yc.yechweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//一旦启动了 AutoUpdateService，就会在 onStartCommand 方法中设定
		//一个定时任务，这样在8小时后 AutoUpdateReceiver 的 onReceive 方法
		//就会得到执行，而这个方法会再次启动 AutoUpdateService，形成循环
		Intent i = new Intent(context,AutoUpdateService.class);
		context.startActivity(i);
	}

}
