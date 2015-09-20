package com.yc.yechweather.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * viewPager 的适配器
 * 有状态的 ，只会有前3个存在其他销毁，  前1个， 中间， 下1个 
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments; 
	public MyFragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}
	
//	@Override
//	public int getItemPosition(Object object) {
//		return PagerAdapter.POSITION_NONE;
//		//return PagerAdapter.POSITION_UNCHANGED;
//	}
	@Override
	public int getCount() {
		return fragments.size();
	}

}
