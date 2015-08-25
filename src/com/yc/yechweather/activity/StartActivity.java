package com.yc.yechweather.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yc.yechweather.R;
import com.yc.yechweather.util.Const;
import com.yc.yechweather.util.Utility;

/**
 * 程序启动时的界面
 */
public class StartActivity extends Activity {

	// 定位按钮(重新定位)
	// private Button reLocate;
	// 添加按钮(添加其他城市)
	private Button add;
	// 设置按钮
	private Button set;
	//
	private ListView cityListView;
	// 添加的城市列表
	private List<HashMap<String, Object>> cityList = new ArrayList<HashMap<String, Object>>();
	// ListView 的适配器
	private SimpleAdapter simpleAdapter;

	// 当前定位城市
	private TextView currentLoc;

	// // 判断是否是从 WeatherActivity 跳转过来的(通过切换城市按钮)
	// private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_layout);
		HashMap<String, Object> map = new HashMap<String, Object>();
		currentLoc = (TextView) findViewById(R.id.current_location);
		// reLocate = (Button) findViewById(R.id.re_locate);
		add = (Button) findViewById(R.id.add);
		set = (Button) findViewById(R.id.set);

		cityListView = (ListView) findViewById(R.id.selected_city);

		// 设置按钮，点击后在城市列表的每一项后添加删除图标
		set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			     //全选遍历ListView的选项，每个选项就相当于布局配置文件中的RelativeLayout  
			     for(int i = 0; i < cityListView.getCount(); i++){  
			    	 RelativeLayout layout = 
			    			 (RelativeLayout) cityListView.getAdapter().getView(i, null, null);  
			    	 ImageView image = (ImageView) layout.getChildAt(0);
			    	 //image.setImageResource(R.drawable.delete);
			    	 image.setVisibility(View.VISIBLE);
			    	 
			     }
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
		cityList = loadCityList(StartActivity.this);
		simpleAdapter = new SimpleAdapter(this, cityList,// 需要绑定的数据
				R.layout.city_list_item,// 每一行的布局
				// 动态数组中的数据源的键对应到定义布局的View中
				new String[] { "ItemImage", "ItemText" }, new int[] {
						R.id.ItemImage, R.id.ItemText });
		cityListView.setAdapter(simpleAdapter);// 为ListView绑定适配器
		// System.out.println("cityList--->" + cityList);
		// 从ChooseAreaActivity 选好了城市跳转过来
		if (getIntent().getBooleanExtra("add_success", false)) {

			String cityName = getIntent().getStringExtra("add_this_city");
			map.put("ItemImage", R.drawable.delete);// 加入图片
			map.put("ItemText", cityName);
			cityList.add(map);
			// 保存城市列表
			saveCityList(StartActivity.this, cityList);
			simpleAdapter.notifyDataSetChanged();
			cityListView.setSelection(0);
		}
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedCity = (String) cityList.get(position).get(
						"ItemText");
				locateToMain(selectedCity);
			}
		});
		currentLoc.setText(Const.locatedCity);
		currentLoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locateToMain(Const.locatedCity);
			}
		});
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
	public boolean saveCityList(Context context,
			List<HashMap<String, Object>> list) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putInt("Status_size", list.size());
		try {
			String listString = Utility.list2String(list);
			editor.putString("listString", listString);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return editor.commit();
	}

	/**
	 * 读取城市列表
	 */
	public List<HashMap<String, Object>> loadCityList(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String listString = prefs.getString("listString", "");
		if (listString != "" && listString.length() > 0) {
			try {
				List<HashMap<String, Object>> list = Utility
						.string2List(listString);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cityList;
	}

	/**
	 * 捕获 back 按键，根据当前的级别来判断，此时应该返回市列表、省列表还是直接退出
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}
}
