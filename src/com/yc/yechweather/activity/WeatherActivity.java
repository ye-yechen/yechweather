package com.yc.yechweather.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yc.yechweather.R;
import com.yc.yechweather.adapter.MyFragmentAdapter;
import com.yc.yechweather.fragment.CityWeatherFragment;
import com.yc.yechweather.util.Const;
import com.yc.yechweather.util.Utility;

public class WeatherActivity extends FragmentActivity {

	private LocationClient locationClient;// 定位 SDK 核心类
	private static String locatedCityName;
	MyFragmentAdapter adapter = null;
	// 已经添加的成市片段
	List<Fragment> fragments = new ArrayList<Fragment>();
	// 已添加的城市名称
	List<String> addedCities = new ArrayList<String>();
	FragmentManager manager = getSupportFragmentManager();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("oncreat----");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!getIntent().getBooleanExtra("isAddCity", false)) {
			locationClient = new LocationClient(getApplicationContext());
			locationClient.registerLocationListener(new BDLocationListener() {

				@Override
				public void onReceiveLocation(BDLocation location) {
					if (location.getLocType() == BDLocation.TypeGpsLocation) {// ͨ��GPS��λ
						locatedCityName = location.getCity();
						locatedCityName = locatedCityName.substring(0,
								locatedCityName.length() - 1);
					} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ͨ���������Ӷ�λ
						locatedCityName = location.getCity();
						locatedCityName = locatedCityName.substring(0,
								locatedCityName.length() - 1);
					}
					Log.i("data", "------------" + locatedCityName);
					locationClient.stop();
					Const.locatedCity = locatedCityName;
					Const.addedCity = locatedCityName;
					addedCities.add(locatedCityName);
					setLocatedFragment();
				}
			}); // ע���������
			initLocation();
			locationClient.start();
		}
		setContentView(R.layout.main_acticity);
		
	}
	
	/**
	 * 初次进入时定位得到的城市fragment
	 */
	private void setLocatedFragment() {
		CityWeatherFragment fragment = CityWeatherFragment.newInstance(null);
		fragments.add(fragment);
		adapter = new MyFragmentAdapter(manager, fragments);
		// 设定适配器
		ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
		vp.setAdapter(adapter);
	}
	
	/**
	 * activity 恢复时执行
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String cityName = data.getStringExtra("city_name");
		
		for (int i = 0; i < addedCities.size(); i++) {
			if (cityName.equals(addedCities.get(i))) {
				break;
			}
			if (i == addedCities.size()-1) {// 列表中没有这个城市名称
				Const.locatedCity = cityName;
				CityWeatherFragment fragment = CityWeatherFragment.newInstance(null);
				fragments.add(fragment);
				addedCities.add(cityName);
				adapter.notifyDataSetChanged();
				saveCityNameList(this, addedCities);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ø߾��ȶ�λ��λģʽ
		option.setCoorType("bd09ll");// ���ðٶȾ�γ������ϵ��ʽ
		// option.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ1000ms
		option.setIsNeedAddress(true);// �������þ���λ�ã�ֻ�����綨λ�ſ���
		locationClient.setLocOption(option);
	}
	
	/**
	 * 保存城市名，防止重复生成fragment
	 */
	public boolean saveCityNameList(Context context, List<String> list) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		try {
			String nameString = Utility.list2String(list);
			editor.putString("nameString", nameString);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return editor.commit();
	}

	/**
	 * 读取城市名
	 */
	public List<String> loadCityNameList(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String nameString = prefs.getString("nameString", "");
		if (nameString != "" && nameString.length() > 0) {
			try {
				List<String> list = Utility.string2List(nameString);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return addedCities;
	}
}
