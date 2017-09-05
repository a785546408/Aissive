package com.lyz.subway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import CustomView.HorizontalBarView;
import CustomView.MyView;
import subway_data.DataBaseHelper;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Chronometer.OnChronometerTickListener;

public class StatisticsFragment extends Fragment {
	private static final int MSG_DATA_CHANGE = 0x11;
	private MyView mLineView;
	private Handler mHandler;
	private View view;
	private Chronometer chronometner2;
	private TextView machineID;
	private TextView stationID;
	private TextView InMaxText;
	private TextView InAvgText;
	private TextView InMinText;
	private TextView InConText;
	private TextView OutMaxText;
	private TextView OutAvgText;
	private TextView OutMinText;
	private TextView OutConText;
	public Thread LThread;
	public myThread thread_3;

	
	LocalBroadcastManager broadcastManager;
	IntentFilter intentFilter;
	BroadcastReceiver mReceiver1;
	BroadcastReceiver mReceiver2;
	BroadcastReceiver mReceiver3;

	int[] thisData0 = new int[] { 0, 0, 0, 0, 0 };
	String[] discribe0 = new String[] {"","","","","","" };
	String[] discribe1 = new String[] { "普通SJT", "老年票", "成人票", "团购票", "残盲卡" };
	
	HorizontalBarView horizontalBar1;

	private final static String X_KEY = "Xpos";
	private final static String Y_KEY = "Ypos";
	private final static String IN_MAX = "in_max";
	private final static String IN_AVR = "in_avr";
	private final static String IN_MIN = "in_min";

	public boolean flag = false;
	private static boolean flog1 = false;
	private static boolean flog2 = false;
	private static boolean flog3 = false;
	private static boolean exit = false;
	private static int flag_handler = 0;
	private Handler Handler1;


	public List<Map<String, Integer>> mListPoint = new ArrayList<Map<String, Integer>>();
	Map<String, Integer> temp = null;
	public List<Map<String, Integer>> dbList = new ArrayList<Map<String, Integer>>();

	private int miss = 0;
	private int JISHUQI = 0;

	private int mX = 0;

	public static int widthmin; 
	
	public String SBID,CZID;
	
	
	private int Cs = 0;

	private float XX;
	private float YY;
	private TextView dian;
	private RelativeLayout layout;
	private RelativeLayout layout1;
	private LinearLayout viewlayout;
	boolean visibility_Flag = true;

	public StatisticsFragment(List<Map<String, Integer>> mList) {
		// TODO Auto-generated constructor stub
		dbList = mList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("--------------------------------", "第三个创建view");
		

		view = inflater.inflate(R.layout.fragment_statistics, container, false);

		init(view);

//		refresh();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		final int count = width / 60 - 2;

		widthmin = (width - 50) / count;

		XX = viewlayout.getX();
		YY = viewlayout.getY();
		// temp = new HashMap<String, Integer>();
		// mListPoint.add(temp);
		// mLineView.setLinePoint(mListPoint, XX, YY);
	
		layout1.setOnClickListener(new MyButtonlisten1());

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				switch (msg.what) {
				case MSG_DATA_CHANGE:
					try {
						InMaxText.setText(dbList.get(Cs).get(IN_MAX) + "");
						InAvgText.setText(dbList.get(Cs).get(IN_AVR) + "");
						InMinText.setText(dbList.get(Cs).get(IN_MIN) + "");
						Cs += 1;
					} catch (IndexOutOfBoundsException e) {
//						// TODO: handle exception
						Toast.makeText(view.getContext(), "测试数据已完毕！", Toast.LENGTH_SHORT).show();
//						
					}
					finally {
						sendEmptyMessageDelayed(flag_handler, 1000);
						temp = new HashMap<String, Integer>();
						temp.put("Xpos", msg.arg1);
						temp.put("Ypos", msg.arg2);
						horizontalBar1.updateData1(getRandom(thisData0));
						horizontalBar1.updateDescribe(discribe1);
						mListPoint.add(temp);
						if (mListPoint.size() < count) {
							mLineView.setLinePoint(mListPoint, XX, YY, count);
						} else {
							mListPoint.remove(0);
							mLineView.setLinePoint(mListPoint, XX, YY, count);
						}
					}

					break;
					
	
					
				case 1:
						removeMessages(msg.what);
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};



//			LThread = new Thread(new Runnable() {
//
//				public void run() {
//					
//					while (!exit) {
//						Random random = new Random();
//						Message message = new Message();
//						message.what = MSG_DATA_CHANGE;
//						message.arg1 = mX;
//						message.arg2 = 480 - random.nextInt(45) * 10;
//						mHandler.sendMessage(message);
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//						mX += (widthmin * 2);
//					}
//				};
//			});

		


		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		intentFilter = new IntentFilter();
		intentFilter.addAction("111");
		intentFilter.addAction("222111");
		intentFilter.addAction("222222");
		intentFilter.addAction("333");
		 synchronized(this){ 

				mReceiver1 = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						// 收到广播后所作的操作
						String action = intent.getAction();
						if (action.equals("111")) {
							startch();
							setID();
//							broadcastManager.unregisterReceiver(mReceiver1);
						}
						if (action.equals("333")) {
							stopch();	
//							exit = false;
							flag = false;
							
						}
						if (action.equals("222111")) {
							suspendch();
							flag = true;
						}
						if (action.equals("222222")) {
							resumech();
							flag = false;
							
//							broadcastManager.unregisterReceiver(mReceiver2);
						}
					}
				};
				
				/*mReceiver2 = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						// 收到广播后所作的操作
						String action = intent.getAction();
						if (action.equals("222")) {
							suspendch();
//							broadcastManager.unregisterReceiver(mReceiver2);
						}
					}
					};*/
					/*mReceiver3 = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							// 收到广播后所作的操作
							String action = intent.getAction();
							if (action.equals("333")) {
						
									stopch();
									//	exit = false;

							}
						}
					};*/
				
				broadcastManager.registerReceiver(mReceiver1, intentFilter);
				//broadcastManager.registerReceiver(mReceiver2, intentFilter);
				//broadcastManager.registerReceiver(mReceiver3, intentFilter);
		 
		 } 
		
	}



	private void init(View view) {
		// TODO Auto-generated method stub

		dian = (TextView) view.findViewById(R.id.button1);
		layout = (RelativeLayout) view.findViewById(R.id.layout1);
		layout1 = (RelativeLayout) view.findViewById(R.id.dianid);
		mLineView = (MyView) view.findViewById(R.id.line);
		viewlayout = (LinearLayout) view.findViewById(R.id.Viewlayout);
		machineID = (TextView) view.findViewById(R.id.machineid);
		stationID = (TextView) view.findViewById(R.id.stationid);
		InMaxText = (TextView) view.findViewById(R.id.maxinstream);
		InAvgText = (TextView) view.findViewById(R.id.avginstream);
		InMinText = (TextView) view.findViewById(R.id.mininstream);
		InConText = (TextView) view.findViewById(R.id.coninstream);
		OutMaxText = (TextView) view.findViewById(R.id.maxoutstream);
		OutAvgText = (TextView) view.findViewById(R.id.avgoutstream);
		OutMinText = (TextView) view.findViewById(R.id.minoutstream);
		OutConText = (TextView) view.findViewById(R.id.conoutstream);
		horizontalBar1 = (HorizontalBarView) view.findViewById(R.id.horizontalBar1);
		horizontalBar1.initial();
		chronometner2 = (Chronometer) view.findViewById(R.id.chronometer2);
		chronometner2.setText("00:00:00");

		Typeface fontFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/digifaw.ttf");
		chronometner2.setTypeface(fontFace);
	}

public void startThread(){
	if (!flog2) {
//	myThread thread = new myThread();
//	thread.start();	
		thread_3 = new myThread();
		thread_3.start();
	flog2 = true;
	}
}



	public void startch() {

			
		
		exit = false;
		
		if (!flog1) {
			flag_handler = 0;
//			mTimeHandler.sendEmptyMessageDelayed(flag_handler, 1000);
			flog1 = true;
		}
		


		startThread();
		chronometner2.setOnChronometerTickListener(new OnChronometerTickListener() {

			@Override
			public void onChronometerTick(Chronometer ch) {
				// TODO Auto-generated method stub
				miss++;
				ch.setText(FormalMiss(miss));
			}
		});

		chronometner2.start();
	}

	public void suspendch() {
		
		
		if (JISHUQI == 0) {

//			thread_3.interrupt();
			chronometner2.stop();
			JISHUQI = 1;
		} else if (JISHUQI == 1) {
		
			chronometner2.setOnChronometerTickListener(new OnChronometerTickListener() {

				@Override
				public void onChronometerTick(Chronometer ch) {
					// TODO Auto-generated method stub
					miss++;
					ch.setText(FormalMiss(miss));
				}
			});

			chronometner2.start();

			JISHUQI = 0;
		}
	}

	public void resumech(){
		
//			horizontalBar1.initial();
			
	
	}
	
	public void stopch(){
		exit = true;
		flog2 = false;
		mListPoint.clear();
		mLineView.clearLine();		
		mLineView.invalidate();
		if (flog1) {
			flag_handler = 1;
//			mTimeHandler.sendEmptyMessageDelayed(flag_handler, 1000);
//			refreshHandler.sendEmptyMessageDelayed(flag_handler, 1000);
			flog1 = false;
		}
	
		InMaxText.setText("");
		InMinText.setText("");
		InAvgText.setText("");
//		thread_3.interrupt();//中断柱状图的线程
		horizontalBar1.initial();//重绘柱状图为初始状态
		mX = 0;
		chronometner2.stop();
		miss = 0;
		chronometner2.setText("00:00:00");
		JISHUQI = 0;
	}

	class MyButtonlisten1 implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (visibility_Flag) {
				layout.setVisibility(View.GONE);
				visibility_Flag = false;
			} else {
				layout.setVisibility(View.VISIBLE);
				visibility_Flag = true;
			}

		}
	}

	
//	Handler mTimeHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			try {
//				if (msg.what == 0) {
//					InMaxText.setText(dbList.get(Cs).get(IN_MAX) + "");
//					InAvgText.setText(dbList.get(Cs).get(IN_AVR) + "");
//					InMinText.setText(dbList.get(Cs).get(IN_MIN) + "");
//					Cs += 1;
//				
//				}else if(msg.what == 1){
//					removeMessages(msg.what);
//				}
//
//			} catch (IndexOutOfBoundsException e) {
//				// TODO: handle exception
//				Toast.makeText(view.getContext(), "测试数据已完毕！", Toast.LENGTH_SHORT).show();
//				
//			} finally {
//				super.handleMessage(msg);
//				sendEmptyMessageDelayed(flag_handler, 1000);
//			}
//
//		}
//	};

	int[] getRandom(int[] data) {
		int f = 0;
		for (int i = 0; i < data.length; i++) {
			f = (int) ((Math.random()) * 100 + 200);
			data[i] = f;
		}
		return data;
	}

//	private void refresh() {
//			try {
//				
//				thread_3 = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						while (!exit){
//						
//						refreshHandler.sendEmptyMessageDelayed(6, 1000);
//						while (!Thread.currentThread().isInterrupted()) {
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								Thread.currentThread().interrupt();
//							}
//							// 使用postInvalidate可以直接在线程中更新界面
//						}
//						}
//					}
//				});
//				thread_3.start();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		
//	}
	
	Handler refreshHandler = new  Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what ==  0) {
				horizontalBar1.updateData1(getRandom(thisData0));
				horizontalBar1.updateDescribe(discribe1);
				
			}
			sendEmptyMessageDelayed(flag_handler, 1000);
		}
	};


	private static String FormalMiss(int miss) {
		String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;

		String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;

		String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;

		return hh + ":" + mm + ":" + ss;
	}
	
	public void setID(){
		SBID = ((MainActivity)getActivity()).getSBID();
		CZID = ((MainActivity)getActivity()).getCZID();
//		machineID.setText("设备编号："+SBID);
//		stationID.setText("车站编号："+CZID);
	}
	
	public class myThread extends Thread{
		
		public void run() {
			
			while (!exit) {
				if (!flag) {

				Random random = new Random();
				Message message = new Message();
				message.what = MSG_DATA_CHANGE;
				message.arg1 = mX;
				message.arg2 = 480 - random.nextInt(45) * 10;
				
				mHandler.sendMessage(message);
				try {
					mX += (widthmin * 2);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
				
			}
		}
	}
	
	
}
