package com.yc.yechweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yc.yechweather.R;
import com.yc.yechweather.util.Const;

/**
 * 程序启动时的界面
 */
public class StartActivity extends Activity {

	// 定位按钮(重新定位)
	//private Button reLocate;
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

	// 当前定位城市
	private TextView currentLoc;

	// // 判断是否是从 WeatherActivity 跳转过来的(通过切换城市按钮)
	// private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_layout);

		currentLoc = (TextView) findViewById(R.id.current_location);
		//reLocate = (Button) findViewById(R.id.re_locate);
		add = (Button) findViewById(R.id.add);
		set = (Button) findViewById(R.id.set);

		cityListView = (ListView) findViewById(R.id.selected_city);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, cityList);
		cityListView.setAdapter(adapter);

		//设置按钮，点击后在城市列表的每一项后添加删除图标
		set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this,
						ChooseAreaActivity.class);
				intent.putExtra("addCity", true);
				intent.putExtra("isFromStartActivity", true);
				startActivity(intent);
				finish();
			}
		});
		loadCityList(StartActivity.this, cityList);
		// 从ChooseAreaActivity 选好了城市跳转过来
		if (getIntent().getBooleanExtra("add_success", false)) {
			String cityName = getIntent().getStringExtra("add_this_city");
			cityList.add(cityName);
			// 保存城市列表
			saveCityList(StartActivity.this, cityList);
			adapter.notifyDataSetChanged();
			cityListView.setSelection(0);
		}
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedCity = cityList.get(position);
				locateToMain(selectedCity);
			}
		});
		// isFromWeatherActivity = getIntent().getBooleanExtra(
		// "from_weather_activity", false);
		// if(isFromWeatherActivity){
		currentLoc.setText(Const.locatedCity);
		currentLoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locateToMain(Const.locatedCity);
			}
		});
		// }
	}

	/**
	 * 定位后直接查询对应城市的天气信息
	 */
	private void locateToMain(String cityName) {
		Intent intent = new Intent(StartActivity.this, WeatherActivity.class);
		intent.putExtra("city_name", cityName);
		intent.putExtra("isLocated", true);
		intent.putExtra("isAddCity", true);
		startActivity(intent);
		finish();
	}

	/**
	 * 保存城市列表
	 */
	public static boolean saveCityList(Context context, List<String> list) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putInt("Status_size", list.size());

		for (int i = 0; i < list.size(); i++) {
			editor.remove("Status_" + i);
			editor.putString("Status_" + i, list.get(i));
		}
		return editor.commit();
	}

	/**
	 * 读取城市列表
	 */
	public static void loadCityList(Context context, List<String> list) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		list.clear();
		int size = prefs.getInt("Status_size", 0);
		for (int i = 0; i < size; i++) {
			list.add(prefs.getString("Status_" + i, null));
		}
	}

	/**
	 * 捕获 back 按键，根据当前的级别来判断，此时应该返回市列表、省列表还是直接退出
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, StartActivity.class);
		startActivity(intent);
		finish();
	}
}
