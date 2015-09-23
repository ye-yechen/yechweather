package com.yc.yechweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.yechweather.R;
import com.yc.yechweather.model.City;
import com.yc.yechweather.model.County;
import com.yc.yechweather.model.Province;
import com.yc.yechweather.model.YechWeatherDB;
import com.yc.yechweather.util.Const;
import com.yc.yechweather.util.HttpCallbackListener;
import com.yc.yechweather.util.HttpUtil;
import com.yc.yechweather.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private YechWeatherDB yechWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	// 省列表
	private List<Province> provinceList;
	// 市列表
	private List<City> cityList;
	// 县列表
	private List<County> countyList;
	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;
	// 当前选中的级别
	private int currentLevel;

	// 判断是否是从 WeatherActivity 跳转过来的(通过切换城市按钮)
	private boolean isFromStartActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromStartActivity = getIntent().getBooleanExtra(
				"isFromStartActivity", false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// 选择了城市,而且不是添加城市
		if (prefs.getBoolean("city_selected", false) 
				&& !getIntent().getBooleanExtra("addCity", false)) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		yechWeatherDB = YechWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyName = countyList.get(position)
							.getCountyName();
					//如果是点击了添加城市按钮，则将添加后的城市添加到城市列表
					if (getIntent().getBooleanExtra("addCity", false)) {
						Intent intent = new Intent(ChooseAreaActivity.this,StartActivity.class);
						intent.putExtra("add_success", true);
						intent.putExtra("add_this_city", countyName);
						setResult(Const.ISFROMCHOOSEACTIVITY, intent);
						//startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(ChooseAreaActivity.this,
								WeatherActivity.class);
						intent.putExtra("county_name", countyName);
						startActivity(intent);
						finish();
					}
				}
			}
		});
		queryProvinces();

	}

	/**
	 * 查询全国所有的县，优先从数据库查，没有就去服务器查
	 */
	private void queryCounties() {
		countyList = yechWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 查询全国所有的市，优先从数据库查，没有就去服务器查
	 */
	private void queryCities() {
		cityList = yechWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询全国所有的省，优先从数据库查，没有就去服务器查
	 */
	private void queryProvinces() {
		provinceList = yechWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 根据传入的代号从服务器查询省市县数据,并保存到数据库
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(yechWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(yechWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(yechWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread() 方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}

					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread() 方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 捕获 back 按键，根据当前的级别来判断，此时应该返回市列表、省列表还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromStartActivity) {
				Intent intent = new Intent(this, StartActivity.class);
				startActivity(intent);
				finish();
			}
			finish();
		}
	}

}
