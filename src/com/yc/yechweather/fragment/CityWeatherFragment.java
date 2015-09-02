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
 * ��ʾ����������fragment
 * 
 * @author Administrator
 *
 */
public class CityWeatherFragment extends Fragment implements OnClickListener,
		Serializable {
	private static final long serialVersionUID = 1L;
	private LocationClient locationClient;// ��λSDK�ĺ�����
	private LinearLayout weatherInfoLayout;
	// ������ʾ������
	private TextView cityNameText;
	// ��ʾ����ʱ��
	private TextView publishText;
	// ��ʾ����������Ϣ
	private TextView weatherDespText;
	// ��ʾ����1
	private TextView temp1Text;
	// ��ʾ����2
	private TextView temp2Text;
	// ��ʾ��ǰ����
	private TextView currentDateText;
	// ��ʾ��ǰ�¶�
	private TextView currentTempText;
	// �л�����
	private Button switchCity;

	// ˢ������
	private Button refreshWeather;

	// ��λ�ĳ���
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
		 * �ٶȶ�λ
		 */
		// ����������ӳ���(�� ��Ӧ��ʱ������� Activity)
		if (!getActivity().getIntent().getBooleanExtra("isAddCity", false)) {
			locationClient = new LocationClient(getActivity()
					.getApplicationContext());
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
					Log.i("data", "--" + locatedCityName);
					locationClient.stop();
					Const.locatedCity = locatedCityName;
					publishText.setText("ͬ����...");
					weatherInfoLayout.setVisibility(View.INVISIBLE);
					cityNameText.setVisibility(View.INVISIBLE);
					String address = "http://wthrcdn.etouch.cn/weather_mini?city="
							+ locatedCityName;
					queryFromServer(address);
					//
				}
			}); // ע���������
			initLocation();
			locationClient.start();
		}
		// ��ʼ�����ؼ�
		initView(view);

		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		String countyName = getActivity().getIntent().getStringExtra(
				"county_name");
		String cityName = getActivity().getIntent().getStringExtra("city_name");
		if (!TextUtils.isEmpty(countyName)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			String address = "http://wthrcdn.etouch.cn/weather_mini?city="
					+ countyName;
			queryFromServer(address);
		} else {
			// û���ؼ����ž�ֱ����ʾ���ش洢������
			showWeather();
		}
		//
		if (!TextUtils.isEmpty(cityName)
				|| getActivity().getIntent()
						.getBooleanExtra("isLocated", false)) {
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			String address = "http://wthrcdn.etouch.cn/weather_mini?city="
					+ cityName;
			queryFromServer(address);
		}
		return view;
	}

	private void initView(View view) {
		// ��ʼ�����ؼ�
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
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(String address) {
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// �������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��...");
					}
				});
			}

		});
	}

	/**
	 * �� SharedPreferences �ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("publish_time", "") + " ����");
		currentDateText.setText(prefs.getString("current_date", ""));
		currentTempText.setText(prefs.getString("current_temp", ""));
		if (prefs.getString("weather_desp", "").equals("����")
				|| prefs.getString("weather_desp", "").equals("����")
				|| prefs.getString("weather_desp", "").equals("����")) {
			setWeatherImage(R.drawable.bigrain);
		} else if (prefs.getString("weather_desp", "").equals("������")
				|| prefs.getString("weather_desp", "").equals("����")) {
			setWeatherImage(R.drawable.lightningrain);
		} else if (prefs.getString("weather_desp", "").equals("��")) {
			setWeatherImage(R.drawable.yintian);
		} else if (prefs.getString("weather_desp", "").equals("����")) {
			setWeatherImage(R.drawable.duoyun);
		} else if (prefs.getString("weather_desp", "").equals("��")) {
			setWeatherImage(R.drawable.sun);
		} else if (prefs.getString("weather_desp", "").equals("С��")) {
			setWeatherImage(R.drawable.smallrain);
		} else if (prefs.getString("weather_desp", "").contains("ѩ")) {
			setWeatherImage(R.drawable.bigsnow);
		} else if (prefs.getString("weather_desp", "").contains("��")) {
			setWeatherImage(R.drawable.fog);
		}
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

		// ���� �Զ�������������
		Intent intent = new Intent(getActivity(), AutoUpdateService.class);
		getActivity().startService(intent);
	}

	/**
	 * ����������Ӧ��ͼƬ
	 * 
	 * @param resId
	 */
	private void setWeatherImage(int resId) {

		// �Ƴ�֮ǰ��ͼƬ
		if (layout != null) {
			layout.removeAllViews();
		}
		ImageView item = new ImageView(getActivity());
		item.setImageResource(resId);// ����ͼƬ
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);// �븸������������
		lp.topMargin = 10;
		item.setId(1);// �������View ��id
		item.setLayoutParams(lp);// ���ò��ֲ���
		layout.addView(item);// RelativeLayout������View
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
			publishText.setText("ͬ����...");
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
	 * ��ʼ����λ��Ϣ
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ø߾��ȶ�λ��λģʽ
		option.setCoorType("bd09ll");// ���ðٶȾ�γ������ϵ��ʽ
		// option.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ1000ms
		option.setIsNeedAddress(true);// �������þ���λ�ã�ֻ�����綨λ�ſ���
		locationClient.setLocOption(option);
	}

}