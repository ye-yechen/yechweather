package com.yc.yechweather.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yc.yechweather.R;

public class SetFragment extends Fragment {

	public static SetFragment newInstance(Bundle args) {
		SetFragment f = new SetFragment();
		f.setArguments(args);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_set_layout, container,false);

		return view;
	}

}
