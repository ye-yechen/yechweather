package com.yc.yechweather.util;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
