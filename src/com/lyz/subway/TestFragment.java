package com.lyz.subway;

import java.util.ArrayList;
import java.util.HashMap;

import com.lyz.subway.TestFragment;
import com.lyz.subway.R;

import CustomView.NewListView;
import CustomView.RoundProgressBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Chronometer.OnChronometerTickListener;

public class TestFragment extends Fragment {

	public static String AD_DOWNLOAD_ACTION = null;

	public IntentFilter intentFilter;

	public interface MyListener {
		public void showMessage(int index);
	}

	private MyListener mListener;

	private RoundProgressBar mRoundProgressBarLI;
	private RoundProgressBar mRoundProgressBarLO;
	private TextView textViewL;
	private TextView textViewL2;
	private TextView textViewL3;
	private int progressLI = 0;
	private int progressLO = 0;

	private RoundProgressBar mRoundProgressBarRI;
	private RoundProgressBar mRoundProgressBarRO;
	private TextView textViewR;
	private TextView textViewR3;
	private int progressRI = 0;
	private int progressRO = 0;

	private Button starttime;
	private Button suspendtime;
	private Button stoptime;
	private Chronometer chronometner;
	private ScrollView scrollview;
	private NewListView listView;
	private int miss = 0;
	private TextView machineid;
	private TextView stationid;
	private Thread thread;
	private int flag = -1;
	private int JISHUQI = 0;
	private Context mContext;
	public StatisticsFragment f1;

	protected String resource;

	// @Override
	// public void onAttach(Activity activity)
	// {/*判断宿主activity是否实现了接口MyListener*/
	// super.onAttach(activity);
	// try {
	// mListener = (MyListener) activity;
	// }catch (ClassCastException e) {
	// throw new ClassCastException(getActivity().getClass().getName()
	// +" must implements interface MyListener");
	// }
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_test, container, false);

		Log.e("--------------------------------", "第二个创建view");

		f1 = new StatisticsFragment(null);

		init(view);
		onclick();
		listinit();

		return view;
	}

	private void progressBarThread() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Log.e("当前线程：", Thread.currentThread().getName());// 这里打印de结果会是main

				while (true) {

					if (flag == 1) {
						flag = -1;
						break;
					}

					mRoundProgressBarLI
							.setProgress((int) (Math.random() * 100));
					mRoundProgressBarLO.setProgress(60);
					String msgL = mRoundProgressBarLI.getProgress() + "";

					mRoundProgressBarRI
							.setProgress((int) (Math.random() * 100));
					mRoundProgressBarRO.setProgress(80);
					String msgR = mRoundProgressBarRI.getProgress() + 200 + "";

					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("msgL", msgL);
					bundle.putString("msgR", msgR);
					message.setData(bundle);
					handler.sendMessage(message);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};

		thread = new Thread(runnable);
	}

	private static String FormalMiss(int miss) {
		String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;

		String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0"
				+ (miss % 3600) / 60;

		String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0"
				+ (miss % 3600) % 60;

		return hh + ":" + mm + ":" + ss;
	}

	private void onclick() {
		// TODO Auto-generated method stub
		starttime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// f1.startch();
				// mListener.showMessage(1);

				Intent intent = new Intent("111");
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
						intent);
				start();

				chronometner
						.setOnChronometerTickListener(new OnChronometerTickListener() {

							@Override
							public void onChronometerTick(Chronometer ch) {
								// TODO Auto-generated method stub
								miss++;
								ch.setText(FormalMiss(miss));
							}
						});

				chronometner.start();
				starttime.setEnabled(false);
				suspendtime.setEnabled(true);

			}
		});

		suspendtime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// f1.suspendch();

				if (JISHUQI == 0) {
					suspend();

					chronometner.stop();
					suspendtime.setText("恢复");
					JISHUQI = 1;
					Intent intent = new Intent("222111");
					LocalBroadcastManager.getInstance(getActivity())
							.sendBroadcast(intent);

				} else if (JISHUQI == 1) {
					start();

					chronometner
							.setOnChronometerTickListener(new OnChronometerTickListener() {

								@Override
								public void onChronometerTick(Chronometer ch) {
									// TODO Auto-generated method stub
									miss++;
									ch.setText(FormalMiss(miss));
								}
							});
					Intent intent = new Intent("222222");
					LocalBroadcastManager.getInstance(getActivity())
							.sendBroadcast(intent);

					chronometner.start();
					suspendtime.setText("暂停");
					JISHUQI = 0;
				}

			}
		});
		stoptime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				restoreDefault();

				// f1.stopch();
				Intent intent = new Intent("333");
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
						intent);
				// thread.stop();
				chronometner.stop();
				miss = 0;
				chronometner.setText("00:00:00");
				starttime.setEnabled(true);
				suspendtime.setEnabled(false);
				suspendtime.setText("暂停");
				JISHUQI = 0;
			}
		});
		machineid.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				machineidset();

				return false;
			}
		});
		stationid.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				stationidset();
				return false;
			}
		});
	}

	private void init(View view) {
		// TODO Auto-generated method stub
		starttime = (Button) view.findViewById(R.id.start);
		suspendtime = (Button) view.findViewById(R.id.suspend);
		suspendtime.setEnabled(false);
		stoptime = (Button) view.findViewById(R.id.stop);
		chronometner = (Chronometer) view.findViewById(R.id.chronometer1);
		chronometner.setText("00:00:00");

		Typeface fontFace = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/digifaw.ttf");
		chronometner.setTypeface(fontFace);
		scrollview = (ScrollView) view.findViewById(R.id.scrollView1);
		scrollview.smoothScrollTo(0, 0);
		listView = (NewListView) view.findViewById(R.id.list);
		machineid = (TextView) view.findViewById(R.id.machineid);
		stationid = (TextView) view.findViewById(R.id.stationid);

		mRoundProgressBarLI = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBarLI);
		mRoundProgressBarLO = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBarLO);
		textViewL = (TextView) view.findViewById(R.id.textL);
		textViewL2 = (TextView) view.findViewById(R.id.msgL2);
		textViewL3 = (TextView) view.findViewById(R.id.msgL3);

		textViewL2.setText("设定" + "25" + "人/min");
		textViewL3.setText("标称" + "60" + "人/min");

		mRoundProgressBarRI = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBarRI);
		mRoundProgressBarRO = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBarRO);
		textViewR = (TextView) view.findViewById(R.id.textR);
		textViewR3 = (TextView) view.findViewById(R.id.msgR3);

		textViewR3.setText("标称<" + "300" + "ms");

	}

	private void listinit() {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("in", "123");
		map1.put("out", "1");
		map1.put("people", "普通成人");
		map1.put("SJT", "123");
		map1.put("SVT", "1");
		list.add(map1);
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("in", "0");
		map2.put("out", "0");
		map2.put("people", "老人");
		map2.put("SJT", "0");
		map2.put("SVT", "0");
		list.add(map2);
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("in", "3");
		map3.put("out", "0");
		map3.put("people", "学生");
		map3.put("SJT", "3");
		map3.put("SVT", "0");
		list.add(map3);
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("in", "0");
		map4.put("out", "0");
		map4.put("people", "残疾人");
		map4.put("SJT", "0");
		map4.put("SVT", "0");
		list.add(map4);

		SimpleAdapter listAdapter = new SimpleAdapter(getActivity(), list,
				R.layout.item1, new String[] { "in", "out", "people", "SJT",
						"SVT" }, new int[] { R.id.in, R.id.out, R.id.people,
						R.id.SJT, R.id.SVT });
		listView.setAdapter(listAdapter);
		listView.setParentScrollView(scrollview);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Bundle bundle = msg.getData();
			String msgL = bundle.getString("msgL");
			String msgR = bundle.getString("msgR");

			textViewL.setText(msgL);
			textViewR.setText(msgR);
		}
	};

	private void restoreDefault() {
			flag = 1;
			textViewL.setText("0");
			textViewR.setText("0");
			mRoundProgressBarLI.setProgress(0);
			mRoundProgressBarLO.setProgress(0);
			mRoundProgressBarRI.setProgress(0);
			mRoundProgressBarRO.setProgress(0);
	}

	public void machineidset() {
		final LayoutInflater layoutInflater = LayoutInflater
				.from(getActivity());
		final View machineidset = layoutInflater.inflate(R.layout.machineidset,
				null);

		new AlertDialog.Builder(getActivity()).setTitle("请输入设备号")
				.setView(machineidset)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText idset;

						idset = (EditText) machineidset
								.findViewById(R.id.newmachineid);

						machineid.setText("设备编号：" + idset.getText().toString());
					}

				}).setNeutralButton("取消", null).show();

	}

	public void stationidset() {
		final LayoutInflater layoutInflater = LayoutInflater
				.from(getActivity());
		final View stationidset = layoutInflater.inflate(R.layout.stationidset,
				null);
		new AlertDialog.Builder(getActivity()).setTitle("请输入车站号")
				.setView(stationidset)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText idset;
						idset = (EditText) stationidset
								.findViewById(R.id.newstationid);
						stationid.setText("车站编号：" + idset.getText().toString());
					}
				}).setNeutralButton("取消", null).show();
	}

	private void start() {
		// if (flag == -1 || flag == 1) {
		// flag = 0;
		// progressBarThread();
		// thread.start();
			flag = -1;
			progressBarThread();
			thread.start();
	}

	private void suspend() {
		// if (flag == 0) {
		// flag = 1;
		// }
		flag = 1;
	}

}
