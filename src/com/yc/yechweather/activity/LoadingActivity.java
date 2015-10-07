package com.yc.yechweather.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.yc.yechweather.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * 程序进入时的加载页
 * @author Administrator
 *
 */
public class LoadingActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_layout);
		new Timer().schedule(new TimerTask() {  
            @Override  
            public void run() {  
                startActivity(new Intent(LoadingActivity.this, WeatherActivity.class));  
                finish();  
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);  
            }  
        }, 4000);  
	}

}
