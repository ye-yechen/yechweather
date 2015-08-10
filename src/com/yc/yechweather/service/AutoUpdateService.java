package com.yc.yechweather.service;

import com.yc.yechweather.receiver.AutoUpdateReceiver;
import com.yc.yechweather.util.HttpCallbackListener;
import com.yc.yechweather.util.HttpUtil;
import com.yc.yechweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		
		/**
		 * 设定定时任务
		 */
		AlarmManager manager =(AlarmManager) getSystemService(ALARM_SERVICE);
		int updateIntervel = 8 * 60 * 60 * 1000; //8小时的毫秒数
		long trrigerAtTime = SystemClock.elapsedRealtime() + updateIntervel;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		//参数含义: 工作类型，触发时间，能够执行广播的 PendingIntent
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trrigerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息
	 */
	protected void updateWeather() {
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(this);
		String  cityName = prefs.getString("city_name", "");
		String address = "http://wthrcdn.etouch.cn/weather_mini?city="+ cityName;
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}

}
