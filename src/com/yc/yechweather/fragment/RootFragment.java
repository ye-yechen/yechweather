package com.yc.yechweather.fragment;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.yc.yechweather.R;

public class RootFragment extends Fragment implements OnClickListener {

	// define fragment
	private CityWeatherFragment mCityWeatherFragment; // 今日天气页面
	private ForecastFragment mForecastFragment; // 预测天气页面
	private SetFragment mSetFragment; // 设置页面
	// define fragment view
	private View mWeatherLayout;
	private View mForecastLayout;
	private View mSetLayout;
	private FragmentManager manager;

	public static RootFragment newInstance(Bundle args) {
		RootFragment f = new RootFragment();
		f.setArguments(args);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.root_layout, container, false);
		initView(view);
		manager = getChildFragmentManager();
		
		setTabSelection(0);
		return view;
	}

	public void initView(View view) {
		mWeatherLayout = view.findViewById(R.id.menu_item_index_layout);
		mForecastLayout = view.findViewById(R.id.menu_item_seek_layout);
		mSetLayout = view.findViewById(R.id.menu_item_mine_layout);
		mWeatherLayout.setOnClickListener(this);
		mForecastLayout.setOnClickListener(this);
		mSetLayout.setOnClickListener(this);
	}

	private void setTabSelection(int index) {
		FragmentTransaction transaction = manager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if(mCityWeatherFragment == null){
				Bundle args = new Bundle();
				args.putString("selectedCityName",
						getArguments().getString("selectedCityName"));
				mCityWeatherFragment = CityWeatherFragment.newInstance(args);
				transaction.add(R.id.rootFrame, mCityWeatherFragment);
				Toast.makeText(getActivity(), "today", Toast.LENGTH_SHORT).show();
			} else {
				transaction.show(mCityWeatherFragment);
			}
			break;
		case 1:
			if (mForecastFragment == null) {

				mForecastFragment = ForecastFragment.newInstance(null);
				transaction.add(R.id.rootFrame, mForecastFragment);
				Toast.makeText(getActivity(), "forecast", Toast.LENGTH_SHORT).show();
			} else {
				transaction.show(mForecastFragment);
			}
			break;
		case 2:
			if (mSetFragment == null) {
				mSetFragment = SetFragment.newInstance(null);
				transaction.add(R.id.rootFrame, mSetFragment);
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
		if (mCityWeatherFragment != null) {
			transaction.hide(mCityWeatherFragment);
		}
		if (mSetFragment != null) {
			transaction.hide(mSetFragment);
		}
		if (mForecastFragment != null) {
			transaction.hide(mForecastFragment);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
