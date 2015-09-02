package com.yc.yechweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.yc.yechweather.R;
import com.yc.yechweather.adapter.MyFragmentAdapter;
import com.yc.yechweather.fragment.CityWeatherFragment;

public class WeatherActivity extends FragmentActivity {

	MyFragmentAdapter adapter = null;
	// 已经添加的成市片段
	List<Fragment> fragments = new ArrayList<Fragment>();
	// 已添加的城市名称
	List<String> addedCities = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_acticity);
		FragmentManager manager = getSupportFragmentManager();
//		fragments = loadCityFragmentList(this);
//		System.out.println("fragments: " + fragments);
//		// 第一次进程序
//		if (fragments.size() == 0) {
			CityWeatherFragment fragment = CityWeatherFragment.newInstance(null);
			fragments.add(fragment);
//			fragments.add(fragment);
//			saveCityFragmentList(this, fragments);
//		}
//		// 已经添加了城市
//		if (getIntent().getBooleanExtra("isAddCity", false)) {
//			String cityName = getIntent().getStringExtra("city_name");
//			addedCities = loadCityNameList(this);
//			if (addedCities.size() == 0) {
//				Bundle args = new Bundle();
//				args.putString("cityName",cityName);
//				CityWeatherFragment fragment = CityWeatherFragment.newInstance(args);
//				fragments.add(fragment);
//				saveCityFragmentList(this, fragments);
//				addedCities.add(cityName);
//				saveCityNameList(this, addedCities);
//			}
//			System.out.println("addedCities: " + addedCities);
//			for (int i = 0; i < addedCities.size(); i++) {
//				if (cityName.equals(addedCities.get(i))) {
//					break;
//				}
//				if (i == addedCities.size()-1) {// 列表中没有这个城市名称
//					Bundle args = new Bundle();
//					args.putString("cityName",cityName);
//					CityWeatherFragment fragment = CityWeatherFragment.newInstance(args);
//					System.out.println("))))"+fragment.getArguments());
//					fragments.add(fragment);
//					saveCityFragmentList(this, fragments);
//					addedCities.add(cityName);
//					saveCityNameList(this, addedCities);
//					break;
//				}
//			}
//		}
		adapter = new MyFragmentAdapter(manager, fragments);
		// 设定适配器
		ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
		vp.setAdapter(adapter);
	}

//	private boolean saveCityFragmentList(Context context, List<Fragment> list) {
//		SharedPreferences.Editor editor = PreferenceManager
//				.getDefaultSharedPreferences(context).edit();
//		try {
//			String objString = Utility.list2String(list);
//			editor.putString("objString", objString);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return editor.commit();
//	}
//
//	private List<Fragment> loadCityFragmentList(Context context) {
//		SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(context);
//		String objString = prefs.getString("objString", "");
//		if (objString != "" && objString.length() > 0) {
//			try {
//				List<Fragment> list = Utility.string2List(objString);
//				return list;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return fragments;
//	}
//
//	public boolean saveCityNameList(Context context, List<String> list) {
//		SharedPreferences.Editor editor = PreferenceManager
//				.getDefaultSharedPreferences(context).edit();
//		try {
//			String nameString = Utility.list2String(list);
//			editor.putString("nameString", nameString);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return editor.commit();
//	}
//
//	public List<String> loadCityNameList(Context context) {
//		SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(context);
//		String nameString = prefs.getString("nameString", "");
//		if (nameString != "" && nameString.length() > 0) {
//			try {
//				List<String> list = Utility.string2List(nameString);
//				return list;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return addedCities;
//	}
}
