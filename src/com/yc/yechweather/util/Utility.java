package com.yc.yechweather.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

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
			String currentTemp = dataObject.getString("wendu");
			String message = dataObject.getString("ganmao");
			JSONArray array = dataObject.getJSONArray("forecast");
			//解析json字符串，获取当天的详细天气信息
			JSONObject weatherInfo = array.getJSONObject(0);
			String temp1 = weatherInfo.getString("low");
			String temp2 = weatherInfo.getString("high");
			String weatherDesp = weatherInfo.getString("type");
			String publishTime = weatherInfo.getString("date");
			saveWeatherInfo(context,cityName,temp1,temp2,currentTemp,
									weatherDesp,publishTime,message);
			//解析json字符串，获取未来几天天气的预测(简要信息)
			for(int i=0;i<array.length();i++){
				JSONObject forecastInfo = array.getJSONObject(i);
				//去掉 "低温","高温"两个字
				String forecastTemp1 = forecastInfo.getString("low").substring(2);
				String forecastTemp2 = forecastInfo.getString("high").substring(2);
				String forecastWeatherDesp = forecastInfo.getString("type");
				String forecastPublishTime = forecastInfo.getString("date");
				saveForecastWeatherInfo(context,i,array.length(),forecastTemp1,forecastTemp2,forecastWeatherDesp,forecastPublishTime,message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存预测的天气信息
	 * @param i : 哪一天的索引(1代表明天，2后天...)
	 * @param length : 解析的数组的长度，代表预测的天数
	 */
	private static void saveForecastWeatherInfo(Context context,int i,int length, String forecastTemp1,
			String forecastTemp2, String forecastWeatherDesp,
			String forecastPublishTime,String message) {
		
		SharedPreferences.Editor editor =
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		if(i == 0){
			editor.putString("message", message);//为当天保存 “感冒指数”
		}
		editor.putInt("forecastDays", length);
		editor.putString("forecastTemp1"+i, forecastTemp1);
		editor.putString("forecastTemp2"+i, forecastTemp2);
		editor.putString("forecastWeatherDesp"+i, forecastWeatherDesp);
		editor.putString("forecastPublishTime"+i, forecastPublishTime);
		editor.commit();
	}

	/**
	 * 将服务器返回的所有天气信息存储到 SharedPreferences 文件
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String temp1, String temp2, String currentTemp,String weatherDesp,String publishTime,String message) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		SharedPreferences.Editor editor =
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("current_temp", currentTemp);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.putString("message", message);//感冒指数
		editor.commit();
	}
	
	/**
	 * 将 List 类型数据保存成String类型
	 * @param <T>
	 */
	public static <T> String list2String(List<T> list)
			throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(list);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String listString = new String(Base64.encode(
				byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return listString;
	}

	/**
	 * 将String还原成list
	 * @param <T>
	 * @param listString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> string2List(String listString) throws Exception {
		byte[] mobileBytes = Base64.decode(listString.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				mobileBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		List<HashMap<String, Object>> list = 
				(List<HashMap<String, Object>>) objectInputStream.readObject();
		objectInputStream.close();
		return (List<T>) list;
	}

}
