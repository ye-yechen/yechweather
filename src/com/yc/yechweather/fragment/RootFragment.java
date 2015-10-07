package com.yc.yechweather.fragment;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yc.yechweather.R;
import com.yc.yechweather.util.ScreenShotUtils;

public class RootFragment extends Fragment implements OnClickListener {

	// define fragment
	private CityWeatherFragment mCityWeatherFragment; // 今日天气页面
	private ForecastFragment mForecastFragment; // 预测天气页面
	//private SetFragment mSetFragment; // 设置页面
	// define fragment view
	private View mWeatherLayout;
	private View mForecastLayout;
	//private View mSetLayout;
	private FragmentManager manager;
	private View shareView;//分享的图片按钮

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
		//mSetLayout = view.findViewById(R.id.menu_item_mine_layout);
		mWeatherLayout.setOnClickListener(this);
		mForecastLayout.setOnClickListener(this);
		//mSetLayout.setOnClickListener(this);
		shareView = view.findViewById(R.id.menu_item_mine_image);
		shareView.setOnClickListener(this);
	}

	private void setTabSelection(int index) {
		
		FragmentTransaction transaction = manager.beginTransaction();
		hideFragments(transaction);
		Bundle args;
		switch (index) { 
		case 0:
			//if(mCityWeatherFragment == null){
				System.out.println("5555555"+getArguments().getString("selectedCityName"));
				args = new Bundle();
				args.putString("selectedCityName",
						getArguments().getString("selectedCityName"));
				mCityWeatherFragment = CityWeatherFragment.newInstance(args);
				transaction.add(R.id.rootFrame, mCityWeatherFragment);
//			} else {
//				System.out.println("44444");
//				transaction.show(mCityWeatherFragment);
//			}
			break;
		case 1:
			if (mForecastFragment == null) {
				args = new Bundle();
				args.putString("selectedCityName",
						getArguments().getString("selectedCityName"));
				mForecastFragment = ForecastFragment.newInstance(args);
				transaction.add(R.id.rootFrame, mForecastFragment);
			} else {
				transaction.show(mForecastFragment);
			}
			break;
//		case 2:
//			String myPath = "";
//			shareIntent(myPath);
//			break;
		default:
			break;
		}
		transaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (mCityWeatherFragment != null) {
			transaction.hide(mCityWeatherFragment);
		}
//		if (mSetFragment != null) {
//			transaction.hide(mSetFragment);
//		}
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
		case R.id.menu_item_mine_image:
			String savePath = "sdcard/"+System.currentTimeMillis()+".jpg";
			ScreenShotUtils.shotBitmap(getActivity(),savePath);
			shareIntent(savePath);
//		case R.id.menu_item_mine_layout:
//			setTabSelection(2);
			break;
		default:
			break;
		}
	}

	/**
	 * 分享所截图片功能
	 * @param myPath
	 */
	private void shareIntent(String myPath) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("image/jpeg");
        List<ResolveInfo> resInfo = 
        		getActivity().getPackageManager().queryIntentActivities(it, 0);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                targeted.setType("image/jpeg");
                ActivityInfo activityInfo = info.activityInfo;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
                // judgments : activityInfo.packageName, activityInfo.name, etc.
                if (activityInfo.packageName.contains("com.tencent.wblog") 
                		|| activityInfo.name.contains("com.tencent.wblog")
                		|| activityInfo.packageName.contains("com.tencent.mm") 
                		|| activityInfo.name.contains("com.tencent.mm")
                		|| activityInfo.packageName.contains("evernote") 
                		|| activityInfo.name.contains("evernote")
                		|| activityInfo.packageName.contains("com.sina.weibo") 
                		|| activityInfo.name.contains("com.sina.weibo")
                		|| activityInfo.packageName.contains("renren") 
                		|| activityInfo.name.contains("renren")
                		|| activityInfo.packageName.contains("com.tencent.MobileQQ") 
                		|| activityInfo.name.contains("com.tencent.MobileQQ")
//                		|| activityInfo.packageName.contains("com.qzone") 
//                		|| activityInfo.name.contains("com.tencent.sc")
                		|| activityInfo.packageName.contains("com.baidu.tiebacls3") 
                		|| activityInfo.name.contains("com.baidu.tiebacls3")) {
                	targeted.putExtra(Intent.EXTRA_STREAM, 
                				Uri.fromFile(new File(myPath)) ); 
                }else{
                	continue;
                }
                targeted.setPackage(activityInfo.packageName);
                targetedShareIntents.add(targeted);
            }
            Intent chooserIntent = 
            		Intent.createChooser(targetedShareIntents.remove(0), "分享到");
            if (chooserIntent == null) {
                return;
            }
            // A Parcelable[] of Intent or LabeledIntent objects as set with
            // putExtra(String, Parcelable[]) of additional activities to place
            // a the front of the list of choices, when shown to the user with a
            // ACTION_CHOOSER.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            try {
                startActivity(chooserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "没有可用于分享的应用", Toast.LENGTH_SHORT).show();
            }
        } else {
			
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
