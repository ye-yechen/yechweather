package com.yc.yechweather.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yc.yechweather.R;

public class TodayFragment extends Fragment {

	public static TodayFragment newInstance(Bundle args) {
		TodayFragment f = new TodayFragment();
		f.setArguments(args);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.weather_layout, container,false);

		return view;
	}

}
