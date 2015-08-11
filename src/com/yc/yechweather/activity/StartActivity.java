package com.yc.yechweather.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yc.yechweather.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 程序启动时的界面
 */
public class StartActivity extends Activity {

	private LocationClient locationClient;// 定位SDK的核心类
	private String cityName;
	// 当前定位城市
	private TextView currentLoc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// 通过GPS定位
					cityName = location.getCity();
					cityName = cityName.substring(0, cityName.length() - 1);
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 通过网络连接定位
					cityName = location.getCity();
					cityName = cityName.substring(0, cityName.length() - 1);
				}
				Log.i("data", "--" + cityName);
				currentLoc.setText(cityName);
				currentLoc.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						locateToMain(cityName);
					}
				});
//				
			}
		}); // 注册监听函数
		initLocation();
		locationClient.start();
		setContentView(R.layout.start_layout);
		currentLoc = (TextView) findViewById(R.id.current_location);
	}


	/**
	 * 定位后直接查询对应城市的天气信息
	 */
	public void locateToMain(String cityName) {
		Intent intent = new Intent(StartActivity.this, WeatherActivity.class);
		intent.putExtra("city_name", cityName);
		intent.putExtra("isLocated", true);
		startActivity(intent);
		finish();
	}

	/**
	 * 初始化定位信息
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置高精度定位定位模式
		option.setCoorType("bd09ll");// 设置百度经纬度坐标系格式
		// option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);// 反编译获得具体位置，只有网络定位才可以
		locationClient.setLocOption(option);
	}

}
