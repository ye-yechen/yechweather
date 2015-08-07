package com.yc.yechweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * 从服务器获取数据
	 * @param address
	 * @param listener
	 */
	public static void sendHttpResquest(final String address,
									final HttpCallbackListener listener){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = "";
					while( (line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						//回调 onFinish 方法
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					//回调 onError()方法
					listener.onError(e);
				} finally{
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
