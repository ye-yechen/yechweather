package com.yc.yechweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {

	/**
	 * 从服务器获取数据
	 * 
	 * @param address
	 * @param listener
	 */
	public static void sendHttpResquest(final String address,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection connection = null;
				try {
					System.out.println("-->" + address);
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					String response = "";
					if (connection.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
						InputStream is = connection.getInputStream(); // 获取输入流
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is, "utf-8"));
						String line = "";
						while ((line = reader.readLine()) != null) {
							response += line;
						}
					}

					if (listener != null) {
						// 回调 onFinish 方法
						listener.onFinish(response);
					}

				} catch (Exception e) {
					// 回调 onError()方法
					Log.i("data", "" + e);
					listener.onError(e);
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
