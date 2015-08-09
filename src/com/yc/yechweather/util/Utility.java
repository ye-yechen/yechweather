package com.yc.yechweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yc.yechweather.model.City;
import com.yc.yechweather.model.County;
import com.yc.yechweather.model.Province;
import com.yc.yechweather.model.YechWeatherDB;

/**
 * 解析从服务器返回的数据
 * @author Administrator
 *
 */
public class Utility {
	
	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(YechWeatherDB 
			yechWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] arr = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(arr[0]);
					province.setProvinceName(arr[1]);
					//将解析出来的数据存储到 Province 表
					yechWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(YechWeatherDB 
			yechWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String c : allCities){
					String[] arr = c.split("\\|");
					City city = new City();
					city.setCityCode(arr[0]);
					city.setCityName(arr[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到 City 表
					yechWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(YechWeatherDB 
			yechWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] arr = c.split("\\|");
					County county = new County();
					county.setCountyCode(arr[0]);
					county.setCountyName(arr[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到 County 表
					yechWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的 json 数据，并将解析出的数据保存到本地
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject dataObject = jsonObject.getJSONObject("data");
			String cityName = dataObject.getString("city");
			
			JSONArray array = dataObject.getJSONArray("forecast");
			JSONObject weatherInfo = array.getJSONObject(0);
			String temp1 = weatherInfo.getString("low");
			String temp2 = weatherInfo.getString("high");
			String weatherDesp = weatherInfo.getString("type");
			String publishTime = weatherInfo.getString("date");
			saveWeatherInfo(context,cityName,temp1,temp2,
									weatherDesp,publishTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到 SharedPreferences 文件
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String temp1, String temp2, String weatherDesp,String publishTime) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		SharedPreferences.Editor editor =
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		//editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
