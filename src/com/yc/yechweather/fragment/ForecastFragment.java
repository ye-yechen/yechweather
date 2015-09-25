package com.yc.yechweather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.yechweather.R;

public class ForecastFragment extends Fragment {

	//预测的天气类型对应的图片
	private ImageView weatherType0;
	private ImageView weatherType1;
	private ImageView weatherType2;
	private ImageView weatherType3;
	private ImageView weatherType4;
	//预测的日期
	private TextView day1; 
	private TextView day2; 
	private TextView day3; 
	private TextView day4; 
	//预测的气温
	private TextView temperature0;
	private TextView temperature1;
	private TextView temperature2;
	private TextView temperature3;
	private TextView temperature4;
	//当天的感冒指数
	private TextView message;
	
	//显示的城市名
	private TextView cityName;
	
	public static ForecastFragment newInstance(Bundle args) {
		ForecastFragment f = new ForecastFragment();
		f.setArguments(args);
		return f;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_forecast_layout, container,false);
		initView(view);
		cityName.setText(getArguments().getString("selectedCityName"));
		showWeather(null, temperature0, weatherType0, 0);
		showWeather(day1, temperature1, weatherType1, 1);
		showWeather(day2, temperature2, weatherType2, 2);
		showWeather(day3, temperature3, weatherType3, 3);
		showWeather(day4, temperature4, weatherType4, 4);
		return view;
	}
	
	public void initView(View view){
		weatherType0 = (ImageView) view.findViewById(R.id.weatherType);
		weatherType1 = (ImageView) view.findViewById(R.id.weatherType1);
		weatherType2 = (ImageView) view.findViewById(R.id.weatherType2);
		weatherType3 = (ImageView) view.findViewById(R.id.weatherType3);
		weatherType4 = (ImageView) view.findViewById(R.id.weatherType4);
		
		day1 = (TextView) view.findViewById(R.id.day1);
		day2 = (TextView) view.findViewById(R.id.day2);
		day3 = (TextView) view.findViewById(R.id.day3);
		day4 = (TextView) view.findViewById(R.id.day4);
		
		temperature0 = (TextView) view.findViewById(R.id.temperature);
		temperature1 = (TextView) view.findViewById(R.id.temperature1);
		temperature2 = (TextView) view.findViewById(R.id.temperature2);
		temperature3 = (TextView) view.findViewById(R.id.temperature3);
		temperature4 = (TextView) view.findViewById(R.id.temperature4);
		
		message = (TextView) view.findViewById(R.id.message);
		cityName = (TextView) view.findViewById(R.id.city_name);
	}
	
	/**
	 * 显示预测的天气
	 */
	private void showWeather(TextView tv1,TextView tv2,ImageView iv, int i) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getParentFragment().getActivity());
		//显示气温
		tv2.setText(prefs.getString("forecastTemp1"+i, "")
				+"~"+prefs.getString("forecastTemp2"+i, ""));
		//显示日期
		if(i != 0){ //不显示当天日期，以"today" 代替
			tv1.setText(prefs.getString("forecastPublishTime"+i, ""));
		} 
		if(i == 0){//显示当天感冒指数
			message.setText(prefs.getString("message", ""));
		}
		//显示天气类型
		//tv3.setText(prefs.getString("forecastWeatherDesp"+i, ""));
		if (prefs.getString("forecastWeatherDesp"+i, "").equals("大雨")
				|| prefs.getString("forecastWeatherDesp"+i, "").equals("暴雨")
				|| prefs.getString("forecastWeatherDesp"+i, "").equals("中雨")) {
			setWeatherImage(iv,R.drawable.bigrain);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").equals("雷阵雨")
				|| prefs.getString("forecastWeatherDesp"+i, "").equals("阵雨")) {
			setWeatherImage(iv,R.drawable.lightningrain);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").equals("阴")) {
			setWeatherImage(iv,R.drawable.yintian);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").equals("多云")) {
			setWeatherImage(iv,R.drawable.duoyun);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").equals("晴")) {
			setWeatherImage(iv,R.drawable.sun);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").equals("小雨")) {
			setWeatherImage(iv,R.drawable.smallrain);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").contains("雪")) {
			setWeatherImage(iv,R.drawable.bigsnow);
		} else if (prefs.getString("forecastWeatherDesp"+i, "").contains("雾")
				|| prefs.getString("forecastWeatherDesp"+i, "").contains("霾")) {
			setWeatherImage(iv,R.drawable.fog);
		}
	}
	
	/**
	 * 设置天气对应的图片
	 * @param resId
	 */
	private void setWeatherImage(ImageView iv,int resId) {
		iv.setImageResource(resId);
	}
}
