package com.yc.yechweather.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yc.yechweather.R;

public class ForecastFragment extends Fragment {

	public static ForecastFragment newInstance(Bundle args) {
		ForecastFragment f = new ForecastFragment();
		f.setArguments(args);
		return f;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_forecast_layout, container,false);
		System.out.println(">>>>>>>>>");
		return view;
	}
}
