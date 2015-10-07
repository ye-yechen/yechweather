package com.yc.yechweather.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yc.yechweather.R;
import com.yc.yechweather.adapter.MyFragmentAdapter;
import com.yc.yechweather.fragment.RootFragment;
import com.yc.yechweather.util.Const;
import com.yc.yechweather.util.Utility;

public class WeatherActivity extends FragmentActivity {

	private LocationClient locationClient;// 定位 SDK 核心类
	private static String locatedCityName;
	// MyFragmentAdapter adapter = null;
	// 已经添加的成市片段
	List<Fragment> fragments = new ArrayList<Fragment>();
	// 已添加的城市名称
	List<String> addedCities = new ArrayList<String>();
	ViewPager vp = null;
	FragmentManager manager = getSupportFragmentManager();
	MyFragmentAdapter adapter = new MyFragmentAdapter(manager, fragments);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (isNetConnected()) {//网络可用
			if (!getIntent().getBooleanExtra("isAddCity", false)) {
				locationClient = new LocationClient(getApplicationContext());
				locationClient
						.registerLocationListener(new BDLocationListener() {

							@Override
							public void onReceiveLocation(BDLocation location) {
								if (location.getLocType() == BDLocation.TypeGpsLocation) {// ͨ��GPS��λ
									locatedCityName = location.getCity();
									locatedCityName = locatedCityName.substring(
											0, locatedCityName.length() - 1);
								} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ͨ���������Ӷ�λ
									locatedCityName = location.getCity();
									locatedCityName = locatedCityName.substring(
											0, locatedCityName.length() - 1);
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
		} else {
			Toast.makeText(this, "当前网络不可用!请先联网!", Toast.LENGTH_SHORT).show();
		}
		setContentView(R.layout.main_acticity);
		vp = (ViewPager) findViewById(R.id.viewPager);
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @return
	 */
	private boolean isNetConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] infos = cm.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if (ni.isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 初次进入时定位得到的城市fragment
	 */
	private void setLocatedFragment() {
		Bundle args = new Bundle();
		args.putString("selectedCityName", Const.locatedCity);
		RootFragment fragment = RootFragment.newInstance(args);
		fragments.add(fragment);
		addedCities.add(Const.locatedCity);
		adapter.notifyDataSetChanged();
		saveCityNameList(this, addedCities, "nameString");
		// 设定适配器
		vp.setAdapter(adapter);
	}

	/**
	 * activity 恢复时执行
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		List<String> existCityList = loadCityNameList(WeatherActivity.this,
				"existCityList");
		if (existCityList.size() != 0) {
			for (int i = 0; i < existCityList.size(); i++) {
				for (int j = 0; j < fragments.size(); j++) {
					if (existCityList.get(i).equals(
							fragments.get(j).getArguments()
									.getString("selectedCityName"))) {
						fragments.remove(j);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
			existCityList.clear();
			saveCityNameList(WeatherActivity.this, existCityList,
					"existCityList");
		}

		// 由 StartActivity 跳转过来
		if (resultCode == Const.ISFROMSTARTACTIVITY) {
			String cityName = "";
			if (data != null) {
				cityName = data.getStringExtra("city_name");
			}
			System.out.println("99999" + cityName);
			addedCities = loadCityNameList(this, "nameString");
			for (int i = 0; i < addedCities.size(); i++) {
				if (cityName.equals("") || cityName.equals(addedCities.get(i))) {
					break;
				}
				if (i == addedCities.size() - 1) {// 列表中没有这个城市名称
					Bundle args = new Bundle();
					args.putString("selectedCityName", cityName);
					RootFragment fragment = RootFragment.newInstance(args);
					fragments.add(fragment);
					addedCities.add(cityName);
					adapter.notifyDataSetChanged();
					saveCityNameList(this, addedCities, "nameString");
					break;
				}
			}
		}
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
	public boolean saveCityNameList(Context context, List<String> list,
			String key) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		try {
			String nameString = Utility.list2String(list);
			editor.putString(key, nameString);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return editor.commit();
	}

	/**
	 * 读取城市名
	 */
	public List<String> loadCityNameList(Context context, String key) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String nameString = prefs.getString(key, "");
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
