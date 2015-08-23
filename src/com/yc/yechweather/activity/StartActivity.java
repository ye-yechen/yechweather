package com.yc.yechweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yc.yechweather.R;
import com.yc.yechweather.model.City;

/**
 * 程序启动时的界面
 */
public class StartActivity extends Activity {

	private LocationClient locationClient;// 定位SDK的核心类
	//选中的城市
	private String selectedCity;
	private String cityName;
	// 当前定位城市
	private TextView currentLoc;
	// 定位按钮(重新定位)
	private Button reLocate;
	// 添加按钮(添加其他城市)
	private Button add;
	// 设置按钮
	private Button set;
	//
	private ListView cityListView;
	// 添加的城市列表
	private List<String> cityList = new ArrayList<String>();
	// ListView 的适配器
	private ArrayAdapter<String> adapter;
	private static int CITY_NUMS;

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
				locationClient.stop();
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_layout);
		currentLoc = (TextView) findViewById(R.id.current_location);
		reLocate = (Button) findViewById(R.id.re_locate);
		add = (Button) findViewById(R.id.add);
		set = (Button) findViewById(R.id.set);

		cityListView = (ListView) findViewById(R.id.selected_city);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, cityList);
		cityListView.setAdapter(adapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedCity = cityList.get(position);
				locateToMain(selectedCity);
			}
		});
		reLocate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locationClient.start();
			}
		});
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this,
						ChooseAreaActivity.class);
				intent.putExtra("addCity", true);
				startActivity(intent);
				finish();
			}
		});
		//从ChooseAreaActivity 选好了城市跳转过来
		if (getIntent().getBooleanExtra("add_success", false)) {
			cityName = getIntent().getStringExtra("add_this_city");
			cityList.add(cityName);
			adapter.notifyDataSetChanged();
			cityListView.setSelection(0);
		}
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
