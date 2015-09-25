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
public class StartActivity extends Activity implements OnClickListener {
	// 确定按钮
	private Button ok;
	// 取消按钮
	private Button cancle;
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

	//已经存在的城市列表，为了删除城市时删除对应的城市fragment
	private List<String> existCityList = new ArrayList<String>();
	// 当前定位城市
	private TextView currentLoc;
	private HashMap<String, Object> map = new HashMap<String, Object>();
	// 已添加的城市
	List<String> addedCities = new ArrayList<String>();

	SharedPreferences.Editor editor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_layout);
		editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
		currentLoc = (TextView) findViewById(R.id.current_location);
		ok = (Button) findViewById(R.id.ok);
		cancle = (Button) findViewById(R.id.cancle);
		add = (Button) findViewById(R.id.add);
		set = (Button) findViewById(R.id.set);
		
		cityListView = (ListView) findViewById(R.id.selected_city);
		// 设置按钮，点击后在城市列表的每一项后添加删除图标
		set.setOnClickListener(this);
		add.setOnClickListener(this);
		ok.setOnClickListener(this);
		cancle.setOnClickListener(this);
		cityList = loadCityList(StartActivity.this,"listString");
		simpleAdapter = new SimpleAdapter(this, cityList,// 需要绑定的数据
				R.layout.city_list_item,// 每一行的布局
				// 动态数组中的数据源的键对应到定义布局的View中
				new String[] { "ItemImage", "ItemText" }, new int[] {
						R.id.ItemImage, R.id.ItemText });
		cityListView.setAdapter(simpleAdapter);// 为ListView绑定适配器
		// 从ChooseAreaActivity 选好了城市跳转过来
		if (getIntent().getBooleanExtra("add_success", false)) {

			String cityName = getIntent().getStringExtra("add_this_city");
			map.put("ItemImage", R.drawable.del);// 加入图片
			map.put("ItemText", cityName);
			cityList.add(map);
			// 保存城市列表
			saveCityList(StartActivity.this, cityList,"listString");
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
		currentLoc.setText(Const.addedCity);
		currentLoc.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			saveCityList(this, existCityList, "existCityList");
			existCityList.clear();
			saveCityList(StartActivity.this, cityList,"listString");
			cancle.setVisibility(View.GONE);
			set.setVisibility(View.VISIBLE);
			ok.setVisibility(View.GONE);
			add.setVisibility(View.VISIBLE);
			cityList = loadCityList(StartActivity.this,"listString");
			
			simpleAdapter = new SimpleAdapter(this, cityList,// 需要绑定的数据
					R.layout.city_list_item,// 每一行的布局
					// 动态数组中的数据源的键对应到定义布局的View中
					new String[] { "ItemImage", "ItemText" }, new int[] {
							R.id.ItemImage, R.id.ItemText });
			cityListView.setAdapter(simpleAdapter);// 为ListView绑定适配器
			break;
		case R.id.cancle:
			existCityList.clear();
			cityList = loadCityList(StartActivity.this,"listString");
			simpleAdapter = new SimpleAdapter(this, cityList,// 需要绑定的数据
					R.layout.city_list_item,// 每一行的布局
					// 动态数组中的数据源的键对应到定义布局的View中
					new String[] { "ItemImage", "ItemText" }, new int[] {
							R.id.ItemImage, R.id.ItemText });
			cityListView.setAdapter(simpleAdapter);// 为ListView绑定适配器
			cancle.setVisibility(View.GONE);
			set.setVisibility(View.VISIBLE);
			ok.setVisibility(View.GONE);
			add.setVisibility(View.VISIBLE);
			break;
		case R.id.set:
			saveCityList(StartActivity.this, cityList,"listString");// 设置之前保存旧的状态
			cancle.setVisibility(View.VISIBLE);
			set.setVisibility(View.GONE);
			ok.setVisibility(View.VISIBLE);
			add.setVisibility(View.GONE);
			// 全选遍历ListView的选项，每个选项就相当于布局配置文件中的RelativeLayout
			for (int i = 0; i < cityListView.getCount(); i++) {
				RelativeLayout layout = (RelativeLayout) cityListView
						.getChildAt(i);
				ImageView image = (ImageView) layout.getChildAt(0);
				image.setId(i);
				image.setVisibility(View.VISIBLE);
				image.setFocusable(true);// 让image获得焦点
				image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//添加到列表，便于在删除城市时清理对应城市的fragment
						existCityList.add((String) cityList.get(v.getId()).get("ItemText"));
						cityList.remove(v.getId());
						simpleAdapter.notifyDataSetChanged();
						cityListView.invalidate();
					}
				});
			}
			break;
		case R.id.add:
			Intent intent = new Intent(StartActivity.this,
					ChooseAreaActivity.class);
			intent.putExtra("addCity", true);
			intent.putExtra("isFromStartActivity", true);
			startActivityForResult(intent, 1);
//			startActivity(intent);
//			finish();
			break;
		case R.id.current_location:
			locateToMain(Const.addedCity);
			break;

		default:
			break;
		}
	}

	/**
	 * 查询对应城市的天气信息
	 */
	private void locateToMain(String cityName) {
		Intent intent = new Intent();
		intent.putExtra("city_name", cityName);
		intent.putExtra("isLocated", true);
		intent.putExtra("isAddCity", true);
		setResult(Const.ISFROMSTARTACTIVITY, intent);
		//startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String cityName = data.getStringExtra("add_this_city");
		map.put("ItemImage", R.drawable.del);// 加入图片
		map.put("ItemText", cityName);
		cityList.add(map);
		// 保存城市列表
		saveCityList(StartActivity.this, cityList,"listString");
		simpleAdapter.notifyDataSetChanged();
		cityListView.setSelection(0); 
	}
	/**
	 * 保存城市列表
	 * @param <T>
	 */
	public <T> boolean saveCityList(Context context,List<T> list,String key) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		try {
			String listString = Utility.list2String(list);
			editor.putString(key, listString);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return editor.commit();
	}

	/**
	 * 读取城市列表
	 * @param <T>
	 * 
	 * @param <T>
	 */
	public <T> List<T> loadCityList(Context context,String key) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String listString = prefs.getString(key, "");
		if (listString != "" && listString.length() > 0) {
			try {
				List<HashMap<String, Object>> list = Utility
						.string2List(listString);
				return (List<T>) list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (List<T>) cityList;
	}

	/**
	 * 捕获 back 按键
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
		//startActivity(intent);
//		intent.putExtra("city_name", cityName);
//		intent.putExtra("isLocated", true);
//		intent.putExtra("isAddCity", true);
		setResult(Const.ISFROMSTARTACTIVITY, intent);
		//setResult(Const.ISFROMSTARTACTIVITY, intent);
		finish();
	}

}
