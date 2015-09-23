package com.yc.yechweather.fragment;

import java.io.Serializable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

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
		Serializable
{
	private static final long serialVersionUID = 1L;
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

	//define fragment
	private TodayFragment mtodayFragment;   //主页面
	private ForecastFragment  mForecastFragment;      //发现页面
	private SetFragment  mSetFragment; //个人中心页面
	// define fragment view
	private View mIndexLayout;
	private View mSeekLayout;
	private View mMineLayout;
	private RelativeLayout layout;
	private View view;
	private FragmentManager mFragmentManager; //管理器
	public static CityWeatherFragment newInstance(Bundle args) {
		CityWeatherFragment f = new CityWeatherFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (null != view) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (null != parent) {
				parent.removeView(view);
			}
		} else {
			view = inflater.inflate(R.layout.weather_layout, container, false);
			initView(view);// 控件初始化
			mFragmentManager = getChildFragmentManager();
			setTabSelection(0);
		}
		if (getArguments().getString("selectedCityName") != null) {
			publishText.setText("ͬ同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			String address = "http://wthrcdn.etouch.cn/weather_mini?city="
					+ getArguments().getString("selectedCityName");
			queryFromServer(address);
		}

		return view;
	}

	private void initView(View view) {
		// ��ʼ�����ؼ�
		weatherInfoLayout = 
				(LinearLayout) view.findViewById(R.id.weather_info_layout);
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
		mIndexLayout = view.findViewById(R.id.menu_item_index_layout);
    	mSeekLayout = view.findViewById(R.id.menu_item_seek_layout);
    	mMineLayout = view.findViewById(R.id.menu_item_mine_layout);
    
    	//设置监听事件
    	switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
    	mIndexLayout.setOnClickListener(this);
    	mSeekLayout.setOnClickListener(this);
    	mMineLayout.setOnClickListener(this);
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(String address) {
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// ������������ص�������Ϣ
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
						publishText.setText("ͬ同步中...");
					}
				});
			}

		});
	}

	/**
	 * �� SharedPreferences �ļ��ж�ȡ�洢��������Ϣ������ʾ��������我们
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
		if (prefs.getString("weather_desp", "").equals("大雨")
				|| prefs.getString("weather_desp", "").equals("暴雨")
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
		} else if (prefs.getString("weather_desp", "").contains("雾")
				|| prefs.getString("weather_desp", "").contains("霾")) {
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
		layout.addView(item);// RelativeLayout�����View
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(getActivity(), StartActivity.class);
			intent.putExtra("from_weather_activity", true);
			// startActivity(intent);
			startActivityForResult(intent, 0);
			// getActivity().finish();
			break;

		case R.id.refresh_weather:
			publishText.setText("ͬ同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String cityName = prefs.getString("city_name", "");
			if (!TextUtils.isEmpty(cityName)) {
				String address = "http://wthrcdn.etouch.cn/weather_mini?city="
						+ cityName;
				queryFromServer(address);
			}
			break;
		case R.id.menu_item_index_layout:
			setTabSelection(0);
			break;
		case R.id.menu_item_seek_layout:
			setTabSelection(1);
			break;
		case R.id.menu_item_mine_layout:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}
	
	 private void setTabSelection(int index) {  
		
	      FragmentTransaction transaction = mFragmentManager.beginTransaction();  
	       
	       hideFragments(transaction);  
	       switch (index) {  
	       case 0:  
//	    	   if(mtodayFragment == null){	   	
//	    		   mtodayFragment = TodayFragment.newInstance(null);
//	           	transaction.add(R.id.content, mtodayFragment);
//	           	Toast.makeText(getActivity(), "today", Toast.LENGTH_SHORT).show();
//	           } else { 
//	               transaction.show(mtodayFragment);  
//	           }  
	           break;
	       case 1: 
	           if (mForecastFragment == null) {  
	               
	        	   mForecastFragment = ForecastFragment.newInstance(null);  
	               transaction.add(R.id.content, mForecastFragment);  
	               Toast.makeText(getActivity(), "forecast", Toast.LENGTH_SHORT).show();
	           } else {  
	               transaction.show(mForecastFragment);  
	           }  
	           break;  
	       case 2:    
	           if (mSetFragment == null) {  
	        	   mSetFragment = SetFragment.newInstance(null);
	               transaction.add(R.id.content, mSetFragment);  
	               Toast.makeText(getActivity(), "set", Toast.LENGTH_SHORT).show();
	           } else {  
	               transaction.show(mSetFragment);  
	           }  
	           break;  
	       default:
	           	break;
	       }  
	       transaction.commit();  
	   }

	    private void hideFragments(FragmentTransaction transaction) {  
	        if (mtodayFragment != null) {  
	            transaction.hide(mtodayFragment);  
	        } 
	        if (mSetFragment != null) {  
	            transaction.hide(mSetFragment);  
	        }
	        if (mForecastFragment != null) {  
	            transaction.hide(mForecastFragment);  
	        } 
	        
	    }

}
