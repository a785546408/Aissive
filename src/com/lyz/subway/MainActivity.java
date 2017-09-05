package com.lyz.subway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import subway_data.DataBaseHelper;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Chronometer.OnChronometerTickListener;

public class MainActivity extends FragmentActivity
		implements OnPageChangeListener, OnClickListener {
	public List<Map<String, Integer>> dbList = new ArrayList<Map<String, Integer>>();

	private ViewPager mViewPager;

	private List<Fragment> mList;

	private long exitTime = 0;

	private ScllorTabView mtab;

	private TextView setting;

	private TextView test;

	private TextView statistics;

	private StatisticsFragment frg;

	private Chronometer mainChronometer;
	
	private String SBID;
	private String CZID;

	private int miss = 0;
	private int JISHUQI = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		dbList = data();

		FirstPage firstpage = new FirstPage();
		TestFragment testfragment = new TestFragment();
		StatisticsFragment statisticsfragment = new StatisticsFragment(dbList);
		
		
		mList = new ArrayList<Fragment>();
		mList.add(firstpage);
		mList.add(testfragment);
		mList.add(statisticsfragment);

		
		setting = (TextView) this.findViewById(R.id.main_setting);
		test = (TextView) this.findViewById(R.id.main_test);
		statistics = (TextView) this.findViewById(R.id.main_statistics);

		setting.setOnClickListener(this);
		test.setOnClickListener(this);
		statistics.setOnClickListener(this);

		mViewPager = (ViewPager) this.findViewById(R.id.main_pagers);
		mViewPager.setAdapter(new TabsFragmentPagerAdapter(getSupportFragmentManager(), mList));
		mViewPager.setOffscreenPageLimit(3);// 设置一次性读入的page数量（默认是1）

		mtab = (ScllorTabView) this.findViewById(R.id.main_scllorTab);
		mtab.setTabNum(3);
		mtab.setSelectedColor(Color.WHITE, Color.rgb(58, 231, 115));

		mViewPager.setCurrentItem(0);
		mtab.setCurrentNum(0);
		mViewPager.setOnPageChangeListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> mList;

		public TabsFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			// TODO Auto-generated constructor stub
			mList = list;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mList.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		mtab.setOffset(arg0, arg1);
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mtab.setOffset(arg0, 0);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.main_setting:
			mtab.setOffset(0, 0);
			mViewPager.setCurrentItem(0, true);
			break;
		case R.id.main_test:
			mtab.setOffset(1, 0);
			mViewPager.setCurrentItem(1, true);
			break;
		case R.id.main_statistics:
			mtab.setOffset(2, 0);
			mViewPager.setCurrentItem(2, true);

			break;
		default:
			break;
		}
	}

	public List data() {
		List<Map<String, Integer>> dbList = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> dbtemp = null;

		Cursor cursor;
		/*----------------------------为程序填充模拟数据-------------------------------*/

		DataBaseHelper dbHelper1 = new DataBaseHelper(MainActivity.this, "subway_db", 2);
		SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
		cursor = db1.rawQuery("select * from data", null);
		if (!cursor.moveToNext()) {
			for (int i = 0; i < 100; i++) {
				Random dbrandom = new Random();
				ContentValues value = new ContentValues();
				value.put("id_time", i);
				value.put("in_max", dbrandom.nextInt(45));
				value.put("in_avr", dbrandom.nextInt(45));
				value.put("in_min", dbrandom.nextInt(45));
				db1.insert("data", null, value);
			}
		}

		/*---------------------------------------------------------------------------*/

		/*------------------------------从数据库中取出数据------------------------------*/
		DataBaseHelper dbHelper2 = new DataBaseHelper(MainActivity.this, "subway_db", 2);
		SQLiteDatabase db2 = dbHelper2.getReadableDatabase();
		cursor = db2.rawQuery("select * from data", null);
		while (cursor.moveToNext()) {
			dbtemp = new HashMap<String, Integer>();
			dbtemp.put("in_max", cursor.getInt(cursor.getColumnIndex("in_max")));
			dbtemp.put("in_avr", cursor.getInt(cursor.getColumnIndex("in_avr")));
			dbtemp.put("in_min", cursor.getInt(cursor.getColumnIndex("in_min")));
			dbList.add(dbtemp);
		}

		/*----------------------------------------------------------------------------*/

		return dbList;
	}

	private static String FormalMiss(int miss) {
		String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;

		String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;

		String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;

		return hh + ":" + mm + ":" + ss;
	}
	
	public String getSBID(){
		return SBID;
	}
	
	public String getCZID(){
		return CZID;
	}
	
	public void setSBID(String SBID){
		this.SBID = SBID;
	}
	
    public void setCZID(String CZID){
    	this.CZID = CZID;
    }

}
