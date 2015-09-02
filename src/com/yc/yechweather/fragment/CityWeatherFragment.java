package com.yc.yechweather.fragment;

import java.io.Serializable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yc.yechweather.R;
import com.yc.yechweather.activity.StartActivity;
import com.yc.yechweather.service.AutoUpdateService;
import com.yc.yechweather.util.Const;
import com.yc.yechweather.util.HttpCallbackListener;
import com.yc.yechweather.util.HttpUtil;
import com.yc.yechweather.util.Utility;

/**
 * 显示城市天气的fragment
 * 
 * @author Administrator
 *
 */
public class CityWeatherFragment extends Fragment implements OnClickListener,
		Serializable {
	private static final long serialVersionUID = 1L;
	private LocationClient locationClient;// 定位SDK的核心类
	private LinearLayout weatherInfoLayout;
	// 用于显示城市名
	private TextView cityNameText;
	// 显示发布时间
	private TextView publishText;
	// 显示天气描述信息
	private TextView weatherDespText;
	// 显示气温1
	private TextView temp1Text;
	// 显示气温2
	private TextView temp2Text;
	// 显示当前日期
	private TextView currentDateText;
	// 显示当前温度
	private TextView currentTempText;
	// 切换城市
	private Button switchCity;

	// 刷新天气
	private Button refreshWeather;

	// 定位的城市
	private static String locatedCityName;
	RelativeLayout layout;

	public static CityWeatherFragment newInstance(Bundle args) {
		CityWeatherFragment f = new CityWeatherFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.weather_layout, container, false);

		/*
		 * 百度定位
		 */
		// 如果不是添加城市(即 打开应用时进入的主 Activity)
		if (!getActivity().getIntent().getBooleanExtra("isAddCity", false)) {
			locationClient = new LocationClient(getActivity()
					.getApplicationContext());
			locationClient.registerLocationListener(new BDLocationListener() {

				@Override
				public void onReceiveLocation(BDLocation location) {
					if (location.getLocType() == BDLocation.TypeGpsLocation) {// 通过GPS定位
						locatedCityName = location.getCity();
						locatedCityName = locatedCityName.substring(0,
								locatedCityName.length() - 1);
					} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 通过网络连接定位
						locatedCityName = location.getCity();
						locatedCityName = locatedCityName.substring(0,
								locatedCityName.length() - 1);
					}
					Log.i("data", "--" + locatedCityName);
					locationClient.stop();
					Const.locatedCity = locatedCityName;
					publishText.setText("同步中...");
					weatherInfoLayout.setVisibility(View.INVISIBLE);
					cityNameText.setVisibility(View.INVISIBLE);
					String address = "http://wthrcdn.etouch.cn/weather_mini?city="
							+ locatedCityName;
					queryFromServer(address);
					//
				}
			}); // 注册监听函数
			initLocation();
			locationClient.start();
		}
		// 初始化各控件
		initView(view);

		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		String countyName = getActivity().getIntent().getStringExtra(
				"county_name");
		String cityName = getActivity().getIntent().getStringExtra("city_name");
		if (!TextUtils.isEmpty(countyName)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			String address = "http://wthrcdn.etouch.cn/weather_mini?city="
					+ countyName;
			queryFromServer(address);
		} else {
			// 没有县级代号就直接显示本地存储的天气
			showWeather();
		}
		//
		if (!TextUtils.isEmpty(cityName)
				|| getActivity().getIntent()
						.getBooleanExtra("isLocated", false)) {
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			String address = "http://wthrcdn.etouch.cn/weather_mini?city="
					+ cityName;
			queryFromServer(address);
		}
		return view;
	}

	private void initView(View view) {
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) view
				.findViewById(R.id.weather_info_layout);
		// weatherImage = (ImageView) findViewById(R.id.weather_image);
		cityNameText = (TextView) view.findViewById(R.id.city_name);
		publishText = (TextView) view.findViewById(R.id.publish_text);
		weatherDespText = (TextView) view.findViewById(R.id.weather_desp);
		temp1Text = (TextView) view.findViewById(R.id.temp1);
		temp2Text = (TextView) view.findViewById(R.id.temp2);
		currentDateText = (TextView) view.findViewById(R.id.current_date);
		currentTempText = (TextView) view.findViewById(R.id.current_temp);
		switchCity = (Button) view.findViewById(R.id.switch_city);
		refreshWeather = (Button) view.findViewById(R.id.refresh_weather);
		layout = (RelativeLayout) view.findViewById(R.id.weather_type);
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(String address) {
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// 处理服务器返回的天气信息
				Utility.handleWeatherResponse(getActivity(), response);
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showWeather();
					}
				});
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						publishText.setText("同步失败...");
					}
				});
			}

		});
	}

	/**
	 * 从 SharedPreferences 文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("publish_time", "") + " 发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		currentTempText.setText(prefs.getString("current_temp", ""));
		if (prefs.getString("weather_desp", "").equals("暴雨")
				|| prefs.getString("weather_desp", "").equals("大雨")
				|| prefs.getString("weather_desp", "").equals("中雨")) {
			setWeatherImage(R.drawable.bigrain);
		} else if (prefs.getString("weather_desp", "").equals("雷阵雨")
				|| prefs.getString("weather_desp", "").equals("阵雨")) {
			setWeatherImage(R.drawable.lightningrain);
		} else if (prefs.getString("weather_desp", "").equals("阴")) {
			setWeatherImage(R.drawable.yintian);
		} else if (prefs.getString("weather_desp", "").equals("多云")) {
			setWeatherImage(R.drawable.duoyun);
		} else if (prefs.getString("weather_desp", "").equals("晴")) {
			setWeatherImage(R.drawable.sun);
		} else if (prefs.getString("weather_desp", "").equals("小雨")) {
			setWeatherImage(R.drawable.smallrain);
		} else if (prefs.getString("weather_desp", "").contains("雪")) {
			setWeatherImage(R.drawable.bigsnow);
		} else if (prefs.getString("weather_desp", "").contains("雾")) {
			setWeatherImage(R.drawable.fog);
		}
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

		// 启动 自动更新天气服务
		Intent intent = new Intent(getActivity(), AutoUpdateService.class);
		getActivity().startService(intent);
	}

	/**
	 * 设置天气对应的图片
	 * 
	 * @param resId
	 */
	private void setWeatherImage(int resId) {

		// 移除之前的图片
		if (layout != null) {
			layout.removeAllViews();
		}
		ImageView item = new ImageView(getActivity());
		item.setImageResource(resId);// 设置图片
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);// 与父容器的左侧对齐
		lp.topMargin = 10;
		item.setId(1);// 设置这个View 的id
		item.setLayoutParams(lp);// 设置布局参数
		layout.addView(item);// RelativeLayout添加子View
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(getActivity(), StartActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			getActivity().finish();
			break;

		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String cityName = prefs.getString("city_name", "");
			if (!TextUtils.isEmpty(cityName)) {
				String address = "http://wthrcdn.etouch.cn/weather_mini?city="
						+ cityName;
				queryFromServer(address);
			}
			break;
		default:
			break;
		}
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
