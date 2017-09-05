package com.lyz.subway;

import java.io.BufferedReader;
import com.lyz.subway.R;
import com.tjs.utils.MyListAdapter;
import com.tjs.utils.TxtHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.SQLite.RW.SQLiteHelper;
import com.zl.bluetooth.Dialog;

import android.support.v4.app.FragmentActivity;

import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FirstPage extends Fragment {
	private static boolean isDistoryFlag = false;

	// 以下是蓝牙相关的控件或者功能注册

	private boolean adapterFlag = false;// 用于表示是否已经创建了一个adapter
	BluetoothAdapter bluetoothAdapter ;
	List<BluetoothDevice> scanresult = new ArrayList<BluetoothDevice>();
	private volatile boolean discoveryFinished;

	SimpleAdapter sim_adapter;

	private Handler _handler = new Handler();

	private Runnable _discoveryWorkder = new Runnable() {

		@Override
		public void run() {
			/* 开始搜索 */
			bluetoothAdapter.startDiscovery();
			Log.d("start_discovery", "000");
			while (true) {
				if (discoveryFinished) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

		}
	};

	private BroadcastReceiver foundReceiver = new BroadcastReceiver() {// 注册广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BroadCast", "注册广播");
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 获取查找到的蓝牙设备
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				scanresult.add(device);
				// getBlueToothInfomation();
				showDevice();
			}
		}
	};

	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BroadcastReceiver", "取消注册广播接收器");

			/* 卸载注册的接收器 */
			getActivity().unregisterReceiver(foundReceiver);
			getActivity().unregisterReceiver(this);
			discoveryFinished = true;
		}
	};

	/* 蓝牙功能相关变量注册结束 */

	private List<HashMap<String, Object>> deviceNameList = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> deviceNameList_cpy = new ArrayList<HashMap<String, Object>>();// 复制保存设备列表
	private TextView tv_device; // 设备的标题
	private TextView tv_testsetting; // 测试设置的标题
	private TextView tv_demomanager; // 样本管理的标题
	private TextView manager_Total_data; // 样本管理样本的个数
	private TextView manager_FileName_data; // 样本管理文件名
	private TextView select_button; // 样本管理的选择按钮
	private TextView tv_adddemo; // 样本管理中用来添加的标识
	private TextView TV_LOAD; // 显示加载中...文字
	private TextView testsetting_num; // 显示总人数
	private TextView testsetting_speed_num; // 显示速度
	private TextView dialog_info; // 打开页面显示的所选信息
	private TextView dialog_select; // 打开界面提示用的
	private CheckBox CB; // list 里 checkbox
	
	private TextView device_button;				//蓝牙设备扫描的按钮
	private TextView demomanager_button;		//样本管理打开的按钮

	
	private LinearLayout device_layout; // 设备的布局上面的那個卵東西
	private LinearLayout layout_device; // 设备的布局
	private LinearLayout select_layout; // 样本管理title的布局
	private LinearLayout layout_testsetting; // 测试设置的布局
	private LinearLayout layout_demomanager; // 样本管理的布局
	private LinearLayout layout_Total; // 样本测试总人数的布局
	private LinearLayout layout_Speed; // 样本测试速度的布局
	private LinearLayout layout_zuhe; // 样本测试人物组合的布局
	private LinearLayout add_demo_layout; // 添加样本的layout
	private Boolean isdevice = false; // 标记设备是否展开
	private Boolean manager = false; // 标记样本管理是否展开
	private Boolean setting = false; // 标记测试设置是否展开
	private ImageView im_triangle1; // 设备（title）的箭头
	private ImageView im_triangle2; // 样本管理（title）的箭头
	private ImageView im_triangle3; // 测试设置（title）的箭头
	private AlertDialog adddialog; // 增加样本用来提示的dialog
	private View tempview; // 用来临时存储list item的view的
	private Spinner sp_dialog; // 打开页面的spinner
	private Spinner sp1; // 组合里的spinner
	private Spinner sp2;
	private Spinner sp3;
	private CheckBox cb1; // 组合里的checkbox
	private CheckBox cb2;
	private CheckBox cb3;
	private CheckBox checkBox_chosen; // list item里的checkbox
	SQLiteHelper sqhelper;
	SQLiteDatabase db;
	Cursor cursor;
	private TxtHelper th = new TxtHelper(); // txt工具类
	// static String path = "/data/data/com.example.newsubway/demoFolder/"; //
	// 模拟器用路径
	static String path = "/sdcard/demoFolder/"; // 手机路径

	private ArrayList<String> demoData = new ArrayList<String>(); // demo的list
	private ArrayList<Boolean> status = new ArrayList<Boolean>();
	private ArrayList<String> filename = new ArrayList<String>();

	private MyListAdapter myadapter; // 自定义的adapter 处理样本管理
	private ArrayAdapter<String> adapter_dialog; // dialog里的 spinner的adapter
	private ArrayAdapter<String> adapter1; // spinner的适配器
	private ArrayAdapter<String> adapter2;
	private ArrayAdapter<String> adapter3;
	private String[] chosenDemo = new String[3]; // 用来记录组合中被选中的demo数据
	private int[] flag = new int[] { 1, 0, 0 }; // 用来记录三个spinner有几个被选中

	private ListView dv_listView; // 设备listview
	private ListView list; // 主页面的list
	private ListView device_list; // 第一项中扫描到的蓝牙设备的list
	private String demoname = ""; // 用来记录打开文件时选择的文件名
	int select_Tag = 1; // 用来记录打开、下载、保存

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("!---------------------", "已销毁");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_first, container, false);

		Log.e("--------------------------------", "第一个创建view");

		

		// 设置广播信息过滤
		IntentFilter found = new IntentFilter();
		
		
		found.addAction(BluetoothDevice.ACTION_FOUND);
		found.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		found.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		found.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(foundReceiver, found);

		IntentFilter filter = new IntentFilter();
		getActivity().registerReceiver(discoveryReceiver, filter);

		// 注册蓝牙相关服务结束

		init(v);
		th.makeRootDirectory(path); // 如果没有建立文件夹 先建一个
		th.makeFilePath(path, "demo.txt"); // 如果没有建立文件 先建一个demo用作演示
		initDemoList();				//初始化一个demo用作演示
		th.writeArrayToTxt(demoData, path,"demo.txt");//初始化一个demo用作演示
		showhide(); // 隐藏显示一级目录
		scanBluetoothDevices(v); // 设置扫描蓝牙设备点击功能
		SelectDemoManagerFunction(); // 选择样本管理的功能
		setDemoSettingLongClick(); // 样本设置的长点击响应
		addnewdomo(); // 添加新的demo

		return v;
	}

	public void initDemoList(){
		demoData.add("普通成人正常通行");
		demoData.add("普通成人推行李箱通行");
	}
	
	//初始化两个textview按钮的位置
	/*public void initButton(View v){
		TextView mTextView = new TextView(v.getContext());
		mTextView.setPadding(left, top, right, bottom);// 通过自定义坐标来放置你的控件
	}*/
	
	// 初始化
	private void init(View v) {
		// TODO Auto-generated method stub
		
		device_button = (TextView) v.findViewById(R.id.device_button);				//蓝牙设备扫描的按钮
		demomanager_button = (TextView) v.findViewById(R.id.demomanager_button);		//样本管理打开的按钮
		
		tv_device = (TextView) v.findViewById(R.id.device_title);
		tv_testsetting = (TextView) v.findViewById(R.id.setting_title);
		tv_demomanager = (TextView) v.findViewById(R.id.manager_title);
		manager_Total_data = (TextView) v.findViewById(R.id.manager_Total_data);
		manager_FileName_data = (TextView) v.findViewById(R.id.manager_FileName_data);
		testsetting_num = (TextView) v.findViewById(R.id.testsetting_num);
		testsetting_speed_num = (TextView) v.findViewById(R.id.testsetting_speed_num);
		tv_adddemo = (TextView) v.findViewById(R.id.add_demo);
		select_button = (TextView) v.findViewById(R.id.select);

		
		
		select_layout = (LinearLayout) v.findViewById(R.id.select_layout);
		device_layout = (LinearLayout) v.findViewById(R.id.device_layout);
		layout_device = (LinearLayout) v.findViewById(R.id.main1);
		layout_demomanager = (LinearLayout) v.findViewById(R.id.main2);
		layout_testsetting = (LinearLayout) v.findViewById(R.id.main3);
		layout_Total = (LinearLayout) v.findViewById(R.id.Total);
		layout_Speed = (LinearLayout) v.findViewById(R.id.Speed);
		layout_zuhe = (LinearLayout) v.findViewById(R.id.zuhe);
		add_demo_layout = (LinearLayout) v.findViewById(R.id.add_demo_layout);
		im_triangle1 = (ImageView) v.findViewById(R.id.jiantou1);
		im_triangle2 = (ImageView) v.findViewById(R.id.jiantou2);
		im_triangle3 = (ImageView) v.findViewById(R.id.jiantou3);
		sp1 = (Spinner) v.findViewById(R.id.spinner1);
		sp2 = (Spinner) v.findViewById(R.id.spinner2);
		sp3 = (Spinner) v.findViewById(R.id.spinner3);

		cb1 = (CheckBox) v.findViewById(R.id.checkBox1);
		cb2 = (CheckBox) v.findViewById(R.id.checkBox2);
		cb3 = (CheckBox) v.findViewById(R.id.checkBox3);
		checkBox_chosen = (CheckBox) v.findViewById(R.id.checkBox_chosen);
		layout_testsetting.setVisibility(View.GONE);
		layout_demomanager.setVisibility(View.GONE);
		dv_listView = (ListView) v.findViewById(R.id.device_list);
		list = (ListView) v.findViewById(R.id.demo_list);
		device_list = (ListView) v.findViewById(R.id.device_list);
	}

	// 初始化数据库
	private void setDataBase() {
		sqhelper = SQLiteHelper.getInstance(getActivity().getApplicationContext());
		sqhelper.getWritableDatabase();
	}

	// 给一级标题设置展开和合拢的属性
	private void showhide() {
		// TODO Auto-generated method stub
		// 将新增样本的layout隐藏
		add_demo_layout.setVisibility(View.GONE);

		// 设备的下拉隐藏
		tv_device.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("tv_device", "点击了“设备”两个字！");
				if (isdevice) {
					layout_device.setVisibility(View.GONE);
					im_triangle1.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
					isdevice = false;
				} else {
					layout_device.setVisibility(View.VISIBLE);
					im_triangle1.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
					isdevice = true;
				}
			}
		});
		im_triangle1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d("tv_device", "点击了箭头");
						if (isdevice) {
							layout_device.setVisibility(View.GONE);
							im_triangle1.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
							isdevice = false;
						} else {
							layout_device.setVisibility(View.VISIBLE);
							im_triangle1.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
							isdevice = true;
						}
					}
		});

		// 样本管理的下拉隐藏
		tv_demomanager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (manager) {
					layout_demomanager.setVisibility(View.GONE);
					im_triangle2.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
					manager = false;
				} else {
					layout_demomanager.setVisibility(View.VISIBLE);
					im_triangle2.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
					manager = true;
				}
			}
		});
		im_triangle2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (manager) {
					layout_demomanager.setVisibility(View.GONE);
					im_triangle2.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
					manager = false;
				} else {
					layout_demomanager.setVisibility(View.VISIBLE);
					im_triangle2.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
					manager = true;
				}
			}
		});
		
		// 测试设置的下拉隐藏
		tv_testsetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (setting) {
					layout_testsetting.setVisibility(View.GONE);
					im_triangle3.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
					setting = false;
				} else {
					layout_testsetting.setVisibility(View.VISIBLE);
					im_triangle3.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
					setting = true;
				}
			}
		});
		im_triangle3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (setting) {
					layout_testsetting.setVisibility(View.GONE);
					im_triangle3.setImageDrawable(getResources().getDrawable(R.drawable.triangle_right));
					setting = false;
				} else {
					layout_testsetting.setVisibility(View.VISIBLE);
					im_triangle3.setImageDrawable(getResources().getDrawable(R.drawable.triangle_down));
					setting = true;
				}
			}
		});

	}

	/* 蓝牙相关功能开始 */

	// 点击扫描蓝牙设备功能
	private void scanBluetoothDevices(View v) {
		device_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 蓝牙服务注册
				Log.e("蓝牙", "clicked!");
				
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bluetoothAdapter == null) {// 如果设备不支持蓝牙，则弹出提示
					Toast.makeText(getActivity(), "该设备不资瓷蓝牙功能！", Toast.LENGTH_SHORT).show();
				}

				if (!bluetoothAdapter.isEnabled()) {// 如果设备蓝牙被关闭了，则打开蓝牙
					bluetoothAdapter.enable();
					Log.e("蓝牙", "enabled!");
				} else {
					Toast.makeText(getActivity(), "蓝牙已开启！", Toast.LENGTH_SHORT).show();
				}

				// 清空蓝牙设备列表
				scanresult.clear();
				deviceNameList.clear();

				// 显示一个进度条框
				Dialog.indeterminateInternal(v.getContext(), _handler, "扫描蓝牙中,请稍等...", _discoveryWorkder,
						new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {
								for (; bluetoothAdapter.isDiscovering();) {
									bluetoothAdapter.cancelDiscovery();
								}
								discoveryFinished = true;
							}
						}, false);

			}
		});
	}

	private boolean isScaned(String deviceName) {
		// 判断是否扫描的同一设备

		for (HashMap<String, Object> hashmap : deviceNameList) {
			String exist_device_name = hashmap.get("deviceName").toString();
			// String exist_device_mac =
			// hashmap.get("deviceAddress").toString();
			if (exist_device_name.equals(deviceName)) {
				Log.e(deviceName + "已扫描到！", deviceName + "已扫描到！");
				return true;
			}

		}
		Log.e("未扫描到！", "未扫描到！");
		return false;
	}

	// SetBlueToothList 设置蓝牙列表的适配器
	private void setBlueToothList() {
		sim_adapter = new SimpleAdapter(getActivity().getApplicationContext(), deviceNameList, R.layout.device_item,
				new String[] { "deviceName", "deviceConnState" },
				new int[] { R.id.device_name_info, R.id.demomanager_information_load });
		_handler.post(new Runnable() {
			public void run() {
				dv_listView.setAdapter(sim_adapter);
				adapterFlag = true;
			}
		});
	}

	// 得到蓝牙信息
	private void getBlueToothInfomation() {
		deviceNameList.clear();
		HashMap<String, Object> item = new HashMap<String, Object>();
		for (BluetoothDevice device : scanresult) {
			Log.e("deviceName", device.getName());
			/* SimpleAdapter方法 */

			if (isScaned("设备ID：  " + device.getName()))// 如果目标设备名已存在，则
				continue;

			item.put("deviceName", "设备ID：  " + device.getName());// 获取设备名
			item.put("deviceConnState", ConnectState(device.getBondState()));// 获取设备连接状态
			item.put("deviceAddress", device.getAddress());//
			// 获取设备mac地址用于后面校验是否为同一设备
			// item.put("device", device);
			deviceNameList.add(item);
		}
		setBlueToothList();
	}

	private void showDevice() {

		if (adapterFlag) {
			// 如果已经绑定了adapter，则刷新数据
			Log.e("Adapter_Flag", "true");

			HashMap<String, Object> item = new HashMap<String, Object>();
			for (BluetoothDevice device : scanresult) {
				Log.e("deviceName", device.getName());

				if (isScaned("设备ID：  " + device.getName()))// 如果目标设备名已存在，则
					continue;

				item.put("deviceName", "设备ID：  " + device.getName());// 获取设备名
				item.put("deviceConnState", ConnectState(device.getBondState()));// 获取设备连接状态
				item.put("deviceAddress", device.getAddress());// 获取设备mac地址用于后面校验是否为同一设备
				// item.put("device", device);
				deviceNameList.add(item);
			}

			_handler.post(new Runnable() {

				@Override
				public void run() {
					sim_adapter.notifyDataSetChanged();
				}
			});

		} else {
			getBlueToothInfomation();
		}

	}

	private String ConnectState(int deviceBondState) {
		if (deviceBondState == 10) {

		} else if (deviceBondState == 12) {

		}

		switch (deviceBondState) {
		case 10:
			return "未连接";

		case 12:
			return "已连接";
		default:
			return "未知连接状态";
		}

	}
	/* 未实现方法 */

	/*
	 * // 点击配对方法 public void Paried(int position) { HashMap<String, Object> map
	 * = deviceNameList.get(position); BluetoothDevice device = null; int
	 * connectState = -1;// 蓝牙连接状态代码 device = (BluetoothDevice)
	 * map.get("device"); if (device != null) { connectState =
	 * device.getBondState();
	 * 
	 * switch (connectState) { // 未配对 case BluetoothDevice.BOND_NONE: // 配对 try
	 * { Method createBondMethod =
	 * BluetoothDevice.class.getMethod("createBond");
	 * createBondMethod.invoke(device); bluetoothAdapter.cancelDiscovery();
	 * bluetoothAdapter.startDiscovery(); } catch (Exception e) {
	 * e.printStackTrace(); } break; // 已配对 case BluetoothDevice.BOND_BONDED:
	 * try { // 连接 connect(device); } catch (IOException e) {
	 * e.printStackTrace(); } break; }
	 * 
	 * } }
	 * 
	 * 
	 * private UUID getUUID() { final TelephonyManager tm = (TelephonyManager)
	 * getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	 * 
	 * final String tmDevice, tmSerial, tmPhone, androidId; tmDevice = "" +
	 * tm.getDeviceId(); tmSerial = "" + tm.getSimSerialNumber(); androidId = ""
	 * + android.provider.Settings.Secure.getString(getContentResolver(),
	 * android.provider.Settings.Secure.ANDROID_ID);
	 * 
	 * UUID deviceUuid = new UUID(androidId.hashCode(), ((long)
	 * tmDevice.hashCode() << 32) | tmSerial.hashCode()); Log.e("UUID_String",
	 * deviceUuid.toString());
	 * 
	 * return deviceUuid; }
	 * 
	 * private void connect(BluetoothDevice device) throws IOException { UUID
	 * uuid = getUUID();// 获得本机UUID BluetoothSocket socket =
	 * device.createRfcommSocketToServiceRecord(uuid); socket.connect();
	 * //这里应有若干连接方法 Toast.makeText(getApplicationContext(), "连接设备",
	 * Toast.LENGTH_SHORT).show(); }
	 * 
	 * public void clearList() { deviceNameList.clear(); }
	 * 
	 * public List<HashMap<String, Object>> getResultList() { if
	 * (deviceNameList.size() != 0) { return this.deviceNameList; } else {
	 * return null; } }
	 * 
	 * public void cancleScan() { bluetoothAdapter.cancelDiscovery();
	 * Toast.makeText(getApplicationContext(), "已停止！",
	 * Toast.LENGTH_SHORT).show(); }
	 */
	/* 蓝牙相关功能结束 */

	// 选择样本管理的功能
	private void SelectDemoManagerFunction() {

		demomanager_button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showPopupMenu(select_button);
				return true;
			}
		});
		demomanager_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub

				switch (select_Tag) {
				case 1: {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("打开样本文件");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							selectFile(v);

						}
					});
					builder.setNegativeButton("取消", null);
					builder.create().show();
					break;
				}
				case 2: {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("下载选中的样本数据");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
					builder.setNegativeButton("取消", null);
					builder.create().show();
					break;
				}
				case 3: {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("保存当前样本数据");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							reserveData();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.create().show();
					break;
				}
				}

			}
		});
	}

	// 选择文件
	private void selectFile(View v) {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
			File Folderpath = new File(path);
			File[] files = Folderpath.listFiles();// 读取
			getFileName(files);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		// 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
		View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner, null);

		// 设置我们自己定义的布局文件作为弹出框的Content

		sp_dialog = (Spinner) contentView.findViewById(R.id.spinner_dialog);
		dialog_info = (TextView) contentView.findViewById(R.id.dialog_info);
		dialog_select = (TextView) contentView.findViewById(R.id.dialog_select);
		dialog_info.setText("请选择样本：");
		adapter_dialog = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, filename);
		adapter_dialog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_dialog.setAdapter(adapter_dialog);
		sp_dialog.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// demo[0] = filename.get(arg2);
				dialog_info.setText(filename.get(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		builder.setTitle("请选择样本");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setView(contentView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity().getApplicationContext(), "确定:", Toast.LENGTH_SHORT).show();
				manager_FileName_data.setText(dialog_info.getText().toString().trim());
				demoname = dialog_info.getText().toString().trim();
				openfileShowonListView(path + dialog_info.getText().toString().trim() + ".txt");
				setSpinner();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.create().show();

	}

	// 打开文件
	public void openfileShowonListView(String Path) {
		try {
			demoData.clear();
			File file = new File(Path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			int i = 0;
			while ((s = br.readLine()) != null) {
				demoData.add(s);
				i++;
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager_Total_data.setText(demoData.size() + "");
		for (int j = 0; j < demoData.size(); j++)
			status.add(j, false);
		setListView();
	}

	// 保存数据至txt
	public void reserveData() {
		File f = new File(path + demoname + ".txt");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write("");
			th.writeArrayToTxt(demoData, path, demoname + ".txt");
			manager_Total_data.setText(demoData.size() + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 得到文件夹中的txt名字
	private void getFileName(File[] files) {
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			filename.clear();
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());
					getFileName(file.listFiles());
					Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString() + file.getPath().toString());
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".txt")) {
						// HashMap map = new HashMap();
						String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
						Log.i("zeng", "文件名txt：   " + s);
						// map.put("Name", fileName.substring(0,
						// fileName.lastIndexOf(".")));
						filename.add(s);
					}
				}
			}
		}
	}

	// 显示menu
	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) // 得到被点击的item的itemId
				{
				case R.id.action_open: {

					/*
					 * 数据库方法 String path =
					 * getApplicationContext().getDatabasePath
					 * ("Demo2").getPath( ); //getAbsolutePath(); File file =
					 * new File(path); openFile(file);
					 */
					// String path =
					// "/data/data/com.example.newsubway/demoFolder/";

					// File file = new File(path+"demo.txt");

					// select_button.setText("打开");
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.open));
					select_Tag = 1;

					break;
				}
				case R.id.action_download: {
					// select_button.setText("下载");
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.download));
					select_Tag = 2;
					break;
				}
				case R.id.action_reserve: {

					/*
					 * 数据库方法 sqhelper.clearAllData(); for(int
					 * i=0;i<list.getChildCount();i++)
					 * sqhelper.insert(i+1,itemData.get(i));
					 */
					select_Tag = 3;
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.reserve));
					// select_button.setText("保存");

					break;
				}
				}
				Toast.makeText(getActivity().getBaseContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				Toast.makeText(getActivity().getBaseContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
			}
		});
		popupMenu.show();
	}

	// 数据库方法加载数据
	private void loadData() {
		cursor = sqhelper.query();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				demoData.add(cursor.getString(cursor.getColumnIndex("Demo")));
			}
			cursor.close();
		}
		manager_Total_data.setText(demoData.size() + "");
		/*
		 * itemData.add("普通成人非正常通行"); itemData.add("普通成人正常通行");
		 * itemData.add("普通成人前推行李箱通行");
		 */
		for (int i = 0; i < demoData.size(); i++)
			status.add(i, false);

	}

	// 样本设置的长点击响应
	private void setDemoSettingLongClick() {
		layout_Total.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(getActivity());
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("请输入总人数");
				builder.setView(et);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "确定:" + et.getText().toString(), Toast.LENGTH_SHORT).show();
						testsetting_num.setText(et.getText().toString());

					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
				builder.create().show();
				return true;
			}
		});
		layout_Speed.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(getActivity());
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("请输入速度");
				builder.setView(et);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "确定:" + et.getText().toString(), Toast.LENGTH_SHORT).show();
						testsetting_speed_num.setText(et.getText().toString() + " 人次/分钟");
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	// 显示demosetting里3个选择的spinner
	private void setSpinner() {
		adapter1 = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,
				demoData);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp1.setAdapter(adapter1);
		sp1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				chosenDemo[0] = adapter1.getItem(arg2);
				Toast.makeText(getActivity(), "您选择了" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					cb1.setChecked(true);// 选中
				}
			}
		});
		adapter2 = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,
				demoData);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp2.setAdapter(adapter2);
		sp2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				chosenDemo[1] = adapter2.getItem(arg2);
				Toast.makeText(getActivity(), "您选择了" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		cb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					flag[1] = 1;
				} else {
					flag[1] = 0;
				}
			}
		});
		adapter3 = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,
				demoData);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp3.setAdapter(adapter3);
		sp3.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				chosenDemo[2] = adapter3.getItem(arg2);
				Toast.makeText(getActivity(), "您选择了" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		cb3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					flag[2] = 1;
				} else {
					flag[2] = 0;
				}
			}
		});
		layout_zuhe.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("组合信息");
				String information = "";
				for (int j = 0; j < 3; j++) {
					if (flag[j] == 1)
						information += (j + 1) + "、" + chosenDemo[j] + " ";
				}
				builder.setMessage("所选的组合为 ：" + information);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "确定", Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	// 显示Listview的内容
	private void setListView() {
		// 初始化适配器
		myadapter = new MyListAdapter(demoData, status, getActivity());
		// 添加并且显示
		list.setAdapter(myadapter);

		if (!demoname.equals(""))
			add_demo_layout.setVisibility(View.VISIBLE);

		// 添加点击事件
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Item长点击事件", Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("样本信息");
				builder.setMessage("请对样本" + (arg2 + 1) + ":" + demoData.get(arg2) + "处理");
				builder.setPositiveButton("重新录制", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						TV_LOAD = (TextView) arg1.findViewById(R.id.demomanager_information_load);
						CB = (CheckBox) arg1.findViewById(R.id.checkBox_chosen);
						TV_LOAD.setText("加载中...");
						CB.setTextColor(Color.BLUE);
						tempview = arg1;
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Message message = new Message();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								message.what = 2;
								mHandler.sendMessage(message);
							}
						}).start();
					}
				});
				builder.setNegativeButton("删除样本", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String delete = demoData.get(arg2).toString();
						demoData.remove(arg2);
						status.remove(arg2);
						manager_Total_data.setText(demoData.size() + "");
						myadapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "成功删除" + delete, Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();
				return true;
			}
		});

	}

	// 添加新的样本
	private void addnewdomo() {
		tv_adddemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
				View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_alert_dialog, null);
				// 设置我们自己定义的布局文件作为弹出框的Content
				final TextView tname = (TextView) view.findViewById(R.id.tvname);
				final EditText name = (EditText) view.findViewById(R.id.etname);

				builder.setTitle("新增样本");
				builder.setMessage("请输入新样本的名字");
				// builder.setView(new EditText(parentContext));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setView(view);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss(); // 关闭dialog
						String saying = name.getText().toString().trim();
						// 添加样本到列表
						demoData.add(saying);
						status.add(false);
						myadapter.notifyDataSetChanged();
						// myadapter.notifyDataSetChanged();
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Message message = new Message();
								message.what = 1;
								mHandler.sendMessage(message);

								Message message3 = new Message();
								message3.what = 3;
								mHandler.sendMessage(message3);

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {

									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								Message message2 = new Message();
								message2.what = 2;
								mHandler.sendMessage(message2);

							}
						}).start();

						Toast.makeText(getActivity().getApplicationContext(), list.getCount() + "", Toast.LENGTH_SHORT)
								.show();
					}
				});
				builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						Toast.makeText(getActivity(), "返回" + i, Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();

			}
		});
	}

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 1) { // 更新
				myadapter.notifyDataSetChanged();
			}
			if (msg.what == 2) { // 显示绿字
				TV_LOAD = (TextView) tempview.findViewById(R.id.demomanager_information_load);
				CB = (CheckBox) tempview.findViewById(R.id.checkBox_chosen);
				TV_LOAD.setText("");
				CB.setTextColor(Color.GREEN);
				manager_Total_data.setText(demoData.size() + "");
			}
			if (msg.what == 3) {
				// myadapter.notifyDataSetChanged();
				Log.d("list", list.getChildCount() + "");
				// Log.d("count-----",list.getChildCount()+"");
				tempview = list.getChildAt(list.getChildCount() - 1);
				TV_LOAD = (TextView) tempview.findViewById(R.id.demomanager_information_load);
				CB = (CheckBox) tempview.findViewById(R.id.checkBox_chosen);
				TV_LOAD.setText("加载中...");
				CB.setTextColor(Color.BLUE);
			}
		}
	};

}
