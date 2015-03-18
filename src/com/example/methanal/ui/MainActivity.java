package com.example.methanal.ui;

import com.example.methanal.AppManager;
import com.example.methanal.R;
import com.example.methanal.custom.CustomViewPager;
import com.example.methanal.util.UIHealper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class MainActivity extends BaseActivity implements OnClickListener{
	private CustomViewPager mViewPager;
	private ImageButton mImageButtonMenuShareSetting;
	private ImageButton mImageButtonMenuCheckShow;
	private ImageButton mImageButtonMenuHistoryLog;
	private TextView mTextViewMainTitle;
	private TextView mTextViewHistoryLogEdit;
	private  final int CLICK_MENU_SHARE_SETTING = 1;
	private  final int CLICK_MUNU_CHECK_SHOW = 2;
	private  final int CLICK_MENU_HISTORY_LOG = 3;
	private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mImageButtonMenuShareSetting = (ImageButton) findViewById(R.id.ImageButtonMenuShareSetting);
        mImageButtonMenuShareSetting.setOnClickListener(this);
        mImageButtonMenuShareSetting.setTag(CLICK_MENU_SHARE_SETTING);
        
        mImageButtonMenuCheckShow = (ImageButton) findViewById(R.id.ImageButtonMenuCheckShow);
        mImageButtonMenuCheckShow.setOnClickListener(this);
        mImageButtonMenuCheckShow.setTag(CLICK_MUNU_CHECK_SHOW);
        
        mImageButtonMenuHistoryLog = (ImageButton) findViewById(R.id.ImageButtonMenuHistoryLog);
        mImageButtonMenuHistoryLog.setOnClickListener(this);
        mImageButtonMenuHistoryLog.setTag(CLICK_MENU_HISTORY_LOG);
        
        mTextViewMainTitle = (TextView) findViewById(R.id.TextViewMainTitle);
        mTextViewHistoryLogEdit = (TextView) findViewById(R.id.TextViewHistoryLogEdit);
        mTextViewHistoryLogEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, HistoryLogEditActivity.class);
				startActivity(intent);
			}
		});
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (CustomViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		
		mTextViewMainTitle.setText(R.string.main_check_show);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				switch (position) {
				case 0:
					mTextViewMainTitle.setText(R.string.main_share_setting);
					mImageButtonMenuShareSetting.setImageResource(R.drawable.menu_share_setting_on);
					mImageButtonMenuCheckShow.setImageResource(R.drawable.menu_check);
					mImageButtonMenuHistoryLog.setImageResource(R.drawable.menu_history);
					mTextViewHistoryLogEdit.setVisibility(View.GONE);
					break;
				case 1:
					mTextViewMainTitle.setText(R.string.main_check_show);
					mImageButtonMenuShareSetting.setImageResource(R.drawable.menu_share_setting);
					mImageButtonMenuCheckShow.setImageResource(R.drawable.menu_check_on);
					mImageButtonMenuHistoryLog.setImageResource(R.drawable.menu_history);
					mTextViewHistoryLogEdit.setVisibility(View.GONE);
					break;
				case 2:
					mTextViewMainTitle.setText(R.string.main_history_log);
					mImageButtonMenuShareSetting.setImageResource(R.drawable.menu_share_setting);
					mImageButtonMenuCheckShow.setImageResource(R.drawable.menu_check);
					mImageButtonMenuHistoryLog.setImageResource(R.drawable.menu_history_on);
					mTextViewHistoryLogEdit.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
			}
		});
    }

	private class SectionsPagerAdapter extends FragmentPagerAdapter {
		CheckFragment mCheckFragment;
		HistoryLogFragment mHistoryLogFragment;
		ShareSettingFragment mShareSettingFragment;
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			mCheckFragment = null;
			mHistoryLogFragment = null;
			mShareSettingFragment = null;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (mShareSettingFragment == null)
					mShareSettingFragment = new ShareSettingFragment();
				return mShareSettingFragment;
			case 1:
				if (mCheckFragment == null)
					mCheckFragment = new CheckFragment();
				return mCheckFragment;
			case 2:
				if (mHistoryLogFragment == null)
					mHistoryLogFragment = new HistoryLogFragment();
				return mHistoryLogFragment;

			}
			return null;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
	}
	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		switch (tag)
		{
			case CLICK_MENU_SHARE_SETTING:
				Log.i("hw", "CLICK_MENU_SHARE_SETTING");
				mViewPager.setCurrentItem(0);
				break;
			case CLICK_MUNU_CHECK_SHOW:
				mViewPager.setCurrentItem(1);
				break;
			case CLICK_MENU_HISTORY_LOG:
				mViewPager.setCurrentItem(2);
				break;
			default:break;
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	private static long firstTime;
	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			Log.i("huwei", getPackageName()+"程序退出！");
			AppManager.getAppManager().AppExit(getApplicationContext());
			super.onBackPressed();
		} else {
			UIHealper.DisplayToast(this, "再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	
	
}
