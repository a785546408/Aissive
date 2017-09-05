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

	// ������������صĿؼ����߹���ע��

	private boolean adapterFlag = false;// ���ڱ�ʾ�Ƿ��Ѿ�������һ��adapter
	BluetoothAdapter bluetoothAdapter ;
	List<BluetoothDevice> scanresult = new ArrayList<BluetoothDevice>();
	private volatile boolean discoveryFinished;

	SimpleAdapter sim_adapter;

	private Handler _handler = new Handler();

	private Runnable _discoveryWorkder = new Runnable() {

		@Override
		public void run() {
			/* ��ʼ���� */
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

	private BroadcastReceiver foundReceiver = new BroadcastReceiver() {// ע��㲥������
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BroadCast", "ע��㲥");
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// ��ȡ���ҵ��������豸
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
			Log.d("BroadcastReceiver", "ȡ��ע��㲥������");

			/* ж��ע��Ľ����� */
			getActivity().unregisterReceiver(foundReceiver);
			getActivity().unregisterReceiver(this);
			discoveryFinished = true;
		}
	};

	/* ����������ر���ע����� */

	private List<HashMap<String, Object>> deviceNameList = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> deviceNameList_cpy = new ArrayList<HashMap<String, Object>>();// ���Ʊ����豸�б�
	private TextView tv_device; // �豸�ı���
	private TextView tv_testsetting; // �������õı���
	private TextView tv_demomanager; // ��������ı���
	private TextView manager_Total_data; // �������������ĸ���
	private TextView manager_FileName_data; // ���������ļ���
	private TextView select_button; // ���������ѡ��ť
	private TextView tv_adddemo; // ����������������ӵı�ʶ
	private TextView TV_LOAD; // ��ʾ������...����
	private TextView testsetting_num; // ��ʾ������
	private TextView testsetting_speed_num; // ��ʾ�ٶ�
	private TextView dialog_info; // ��ҳ����ʾ����ѡ��Ϣ
	private TextView dialog_select; // �򿪽�����ʾ�õ�
	private CheckBox CB; // list �� checkbox
	
	private TextView device_button;				//�����豸ɨ��İ�ť
	private TextView demomanager_button;		//��������򿪵İ�ť

	
	private LinearLayout device_layout; // �豸�Ĳ���������ǂ��і|��
	private LinearLayout layout_device; // �豸�Ĳ���
	private LinearLayout select_layout; // ��������title�Ĳ���
	private LinearLayout layout_testsetting; // �������õĲ���
	private LinearLayout layout_demomanager; // ��������Ĳ���
	private LinearLayout layout_Total; // ���������������Ĳ���
	private LinearLayout layout_Speed; // ���������ٶȵĲ���
	private LinearLayout layout_zuhe; // ��������������ϵĲ���
	private LinearLayout add_demo_layout; // ���������layout
	private Boolean isdevice = false; // ����豸�Ƿ�չ��
	private Boolean manager = false; // ������������Ƿ�չ��
	private Boolean setting = false; // ��ǲ��������Ƿ�չ��
	private ImageView im_triangle1; // �豸��title���ļ�ͷ
	private ImageView im_triangle2; // ��������title���ļ�ͷ
	private ImageView im_triangle3; // �������ã�title���ļ�ͷ
	private AlertDialog adddialog; // ��������������ʾ��dialog
	private View tempview; // ������ʱ�洢list item��view��
	private Spinner sp_dialog; // ��ҳ���spinner
	private Spinner sp1; // ������spinner
	private Spinner sp2;
	private Spinner sp3;
	private CheckBox cb1; // ������checkbox
	private CheckBox cb2;
	private CheckBox cb3;
	private CheckBox checkBox_chosen; // list item���checkbox
	SQLiteHelper sqhelper;
	SQLiteDatabase db;
	Cursor cursor;
	private TxtHelper th = new TxtHelper(); // txt������
	// static String path = "/data/data/com.example.newsubway/demoFolder/"; //
	// ģ������·��
	static String path = "/sdcard/demoFolder/"; // �ֻ�·��

	private ArrayList<String> demoData = new ArrayList<String>(); // demo��list
	private ArrayList<Boolean> status = new ArrayList<Boolean>();
	private ArrayList<String> filename = new ArrayList<String>();

	private MyListAdapter myadapter; // �Զ����adapter ������������
	private ArrayAdapter<String> adapter_dialog; // dialog��� spinner��adapter
	private ArrayAdapter<String> adapter1; // spinner��������
	private ArrayAdapter<String> adapter2;
	private ArrayAdapter<String> adapter3;
	private String[] chosenDemo = new String[3]; // ������¼����б�ѡ�е�demo����
	private int[] flag = new int[] { 1, 0, 0 }; // ������¼����spinner�м�����ѡ��

	private ListView dv_listView; // �豸listview
	private ListView list; // ��ҳ���list
	private ListView device_list; // ��һ����ɨ�赽�������豸��list
	private String demoname = ""; // ������¼���ļ�ʱѡ����ļ���
	int select_Tag = 1; // ������¼�򿪡����ء�����

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("!---------------------", "������");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_first, container, false);

		Log.e("--------------------------------", "��һ������view");

		

		// ���ù㲥��Ϣ����
		IntentFilter found = new IntentFilter();
		
		
		found.addAction(BluetoothDevice.ACTION_FOUND);
		found.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		found.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		found.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(foundReceiver, found);

		IntentFilter filter = new IntentFilter();
		getActivity().registerReceiver(discoveryReceiver, filter);

		// ע��������ط������

		init(v);
		th.makeRootDirectory(path); // ���û�н����ļ��� �Ƚ�һ��
		th.makeFilePath(path, "demo.txt"); // ���û�н����ļ� �Ƚ�һ��demo������ʾ
		initDemoList();				//��ʼ��һ��demo������ʾ
		th.writeArrayToTxt(demoData, path,"demo.txt");//��ʼ��һ��demo������ʾ
		showhide(); // ������ʾһ��Ŀ¼
		scanBluetoothDevices(v); // ����ɨ�������豸�������
		SelectDemoManagerFunction(); // ѡ����������Ĺ���
		setDemoSettingLongClick(); // �������õĳ������Ӧ
		addnewdomo(); // ����µ�demo

		return v;
	}

	public void initDemoList(){
		demoData.add("��ͨ��������ͨ��");
		demoData.add("��ͨ������������ͨ��");
	}
	
	//��ʼ������textview��ť��λ��
	/*public void initButton(View v){
		TextView mTextView = new TextView(v.getContext());
		mTextView.setPadding(left, top, right, bottom);// ͨ���Զ���������������Ŀؼ�
	}*/
	
	// ��ʼ��
	private void init(View v) {
		// TODO Auto-generated method stub
		
		device_button = (TextView) v.findViewById(R.id.device_button);				//�����豸ɨ��İ�ť
		demomanager_button = (TextView) v.findViewById(R.id.demomanager_button);		//��������򿪵İ�ť
		
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

	// ��ʼ�����ݿ�
	private void setDataBase() {
		sqhelper = SQLiteHelper.getInstance(getActivity().getApplicationContext());
		sqhelper.getWritableDatabase();
	}

	// ��һ����������չ���ͺ�£������
	private void showhide() {
		// TODO Auto-generated method stub
		// ������������layout����
		add_demo_layout.setVisibility(View.GONE);

		// �豸����������
		tv_device.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("tv_device", "����ˡ��豸�������֣�");
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
						Log.d("tv_device", "����˼�ͷ");
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

		// �����������������
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
		
		// �������õ���������
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

	/* ������ع��ܿ�ʼ */

	// ���ɨ�������豸����
	private void scanBluetoothDevices(View v) {
		device_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ��������ע��
				Log.e("����", "clicked!");
				
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bluetoothAdapter == null) {// ����豸��֧���������򵯳���ʾ
					Toast.makeText(getActivity(), "���豸���ʴ��������ܣ�", Toast.LENGTH_SHORT).show();
				}

				if (!bluetoothAdapter.isEnabled()) {// ����豸�������ر��ˣ��������
					bluetoothAdapter.enable();
					Log.e("����", "enabled!");
				} else {
					Toast.makeText(getActivity(), "�����ѿ�����", Toast.LENGTH_SHORT).show();
				}

				// ��������豸�б�
				scanresult.clear();
				deviceNameList.clear();

				// ��ʾһ����������
				Dialog.indeterminateInternal(v.getContext(), _handler, "ɨ��������,���Ե�...", _discoveryWorkder,
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
		// �ж��Ƿ�ɨ���ͬһ�豸

		for (HashMap<String, Object> hashmap : deviceNameList) {
			String exist_device_name = hashmap.get("deviceName").toString();
			// String exist_device_mac =
			// hashmap.get("deviceAddress").toString();
			if (exist_device_name.equals(deviceName)) {
				Log.e(deviceName + "��ɨ�赽��", deviceName + "��ɨ�赽��");
				return true;
			}

		}
		Log.e("δɨ�赽��", "δɨ�赽��");
		return false;
	}

	// SetBlueToothList ���������б��������
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

	// �õ�������Ϣ
	private void getBlueToothInfomation() {
		deviceNameList.clear();
		HashMap<String, Object> item = new HashMap<String, Object>();
		for (BluetoothDevice device : scanresult) {
			Log.e("deviceName", device.getName());
			/* SimpleAdapter���� */

			if (isScaned("�豸ID��  " + device.getName()))// ���Ŀ���豸���Ѵ��ڣ���
				continue;

			item.put("deviceName", "�豸ID��  " + device.getName());// ��ȡ�豸��
			item.put("deviceConnState", ConnectState(device.getBondState()));// ��ȡ�豸����״̬
			item.put("deviceAddress", device.getAddress());//
			// ��ȡ�豸mac��ַ���ں���У���Ƿ�Ϊͬһ�豸
			// item.put("device", device);
			deviceNameList.add(item);
		}
		setBlueToothList();
	}

	private void showDevice() {

		if (adapterFlag) {
			// ����Ѿ�����adapter����ˢ������
			Log.e("Adapter_Flag", "true");

			HashMap<String, Object> item = new HashMap<String, Object>();
			for (BluetoothDevice device : scanresult) {
				Log.e("deviceName", device.getName());

				if (isScaned("�豸ID��  " + device.getName()))// ���Ŀ���豸���Ѵ��ڣ���
					continue;

				item.put("deviceName", "�豸ID��  " + device.getName());// ��ȡ�豸��
				item.put("deviceConnState", ConnectState(device.getBondState()));// ��ȡ�豸����״̬
				item.put("deviceAddress", device.getAddress());// ��ȡ�豸mac��ַ���ں���У���Ƿ�Ϊͬһ�豸
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
			return "δ����";

		case 12:
			return "������";
		default:
			return "δ֪����״̬";
		}

	}
	/* δʵ�ַ��� */

	/*
	 * // �����Է��� public void Paried(int position) { HashMap<String, Object> map
	 * = deviceNameList.get(position); BluetoothDevice device = null; int
	 * connectState = -1;// ��������״̬���� device = (BluetoothDevice)
	 * map.get("device"); if (device != null) { connectState =
	 * device.getBondState();
	 * 
	 * switch (connectState) { // δ��� case BluetoothDevice.BOND_NONE: // ��� try
	 * { Method createBondMethod =
	 * BluetoothDevice.class.getMethod("createBond");
	 * createBondMethod.invoke(device); bluetoothAdapter.cancelDiscovery();
	 * bluetoothAdapter.startDiscovery(); } catch (Exception e) {
	 * e.printStackTrace(); } break; // ����� case BluetoothDevice.BOND_BONDED:
	 * try { // ���� connect(device); } catch (IOException e) {
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
	 * uuid = getUUID();// ��ñ���UUID BluetoothSocket socket =
	 * device.createRfcommSocketToServiceRecord(uuid); socket.connect();
	 * //����Ӧ���������ӷ��� Toast.makeText(getApplicationContext(), "�����豸",
	 * Toast.LENGTH_SHORT).show(); }
	 * 
	 * public void clearList() { deviceNameList.clear(); }
	 * 
	 * public List<HashMap<String, Object>> getResultList() { if
	 * (deviceNameList.size() != 0) { return this.deviceNameList; } else {
	 * return null; } }
	 * 
	 * public void cancleScan() { bluetoothAdapter.cancelDiscovery();
	 * Toast.makeText(getApplicationContext(), "��ֹͣ��",
	 * Toast.LENGTH_SHORT).show(); }
	 */
	/* ������ع��ܽ��� */

	// ѡ����������Ĺ���
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
					builder.setTitle("�������ļ�");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							selectFile(v);

						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.create().show();
					break;
				}
				case 2: {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("����ѡ�е���������");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.create().show();
					break;
				}
				case 3: {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("���浱ǰ��������");
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							reserveData();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.create().show();
					break;
				}
				}

			}
		});
	}

	// ѡ���ļ�
	private void selectFile(View v) {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// File path = Environment.getExternalStorageDirectory();// ���SD��·��
			File Folderpath = new File(path);
			File[] files = Folderpath.listFiles();// ��ȡ
			getFileName(files);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		// ͨ��LayoutInflater������һ��xml�Ĳ����ļ���Ϊһ��View����
		View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner, null);

		// ���������Լ�����Ĳ����ļ���Ϊ�������Content

		sp_dialog = (Spinner) contentView.findViewById(R.id.spinner_dialog);
		dialog_info = (TextView) contentView.findViewById(R.id.dialog_info);
		dialog_select = (TextView) contentView.findViewById(R.id.dialog_select);
		dialog_info.setText("��ѡ��������");
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
		builder.setTitle("��ѡ������");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setView(contentView);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity().getApplicationContext(), "ȷ��:", Toast.LENGTH_SHORT).show();
				manager_FileName_data.setText(dialog_info.getText().toString().trim());
				demoname = dialog_info.getText().toString().trim();
				openfileShowonListView(path + dialog_info.getText().toString().trim() + ".txt");
				setSpinner();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.create().show();

	}

	// ���ļ�
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

	// ����������txt
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

	// �õ��ļ����е�txt����
	private void getFileName(File[] files) {
		if (files != null) {// ���ж�Ŀ¼�Ƿ�Ϊ�գ�����ᱨ��ָ��
			filename.clear();
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "�����ļ�Ŀ¼��������1" + file.getName().toString() + file.getPath().toString());
					getFileName(file.listFiles());
					Log.i("zeng", "�����ļ�Ŀ¼��������2" + file.getName().toString() + file.getPath().toString());
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".txt")) {
						// HashMap map = new HashMap();
						String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
						Log.i("zeng", "�ļ���txt��   " + s);
						// map.put("Name", fileName.substring(0,
						// fileName.lastIndexOf(".")));
						filename.add(s);
					}
				}
			}
		}
	}

	// ��ʾmenu
	private void showPopupMenu(View view) {
		// View��ǰPopupMenu��ʾ�����View��λ��
		PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
		// menu����
		popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
		// menu��item����¼�
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) // �õ��������item��itemId
				{
				case R.id.action_open: {

					/*
					 * ���ݿⷽ�� String path =
					 * getApplicationContext().getDatabasePath
					 * ("Demo2").getPath( ); //getAbsolutePath(); File file =
					 * new File(path); openFile(file);
					 */
					// String path =
					// "/data/data/com.example.newsubway/demoFolder/";

					// File file = new File(path+"demo.txt");

					// select_button.setText("��");
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.open));
					select_Tag = 1;

					break;
				}
				case R.id.action_download: {
					// select_button.setText("����");
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.download));
					select_Tag = 2;
					break;
				}
				case R.id.action_reserve: {

					/*
					 * ���ݿⷽ�� sqhelper.clearAllData(); for(int
					 * i=0;i<list.getChildCount();i++)
					 * sqhelper.insert(i+1,itemData.get(i));
					 */
					select_Tag = 3;
					select_layout.setBackgroundDrawable(
							getActivity().getApplicationContext().getResources().getDrawable(R.drawable.reserve));
					// select_button.setText("����");

					break;
				}
				}
				Toast.makeText(getActivity().getBaseContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		// PopupMenu�ر��¼�
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				Toast.makeText(getActivity().getBaseContext(), "�ر�PopupMenu", Toast.LENGTH_SHORT).show();
			}
		});
		popupMenu.show();
	}

	// ���ݿⷽ����������
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
		 * itemData.add("��ͨ���˷�����ͨ��"); itemData.add("��ͨ��������ͨ��");
		 * itemData.add("��ͨ����ǰ��������ͨ��");
		 */
		for (int i = 0; i < demoData.size(); i++)
			status.add(i, false);

	}

	// �������õĳ������Ӧ
	private void setDemoSettingLongClick() {
		layout_Total.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(getActivity());
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("������������");
				builder.setView(et);
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "ȷ��:" + et.getText().toString(), Toast.LENGTH_SHORT).show();
						testsetting_num.setText(et.getText().toString());

					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
				builder.setTitle("�������ٶ�");
				builder.setView(et);
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "ȷ��:" + et.getText().toString(), Toast.LENGTH_SHORT).show();
						testsetting_speed_num.setText(et.getText().toString() + " �˴�/����");
					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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

	// ��ʾdemosetting��3��ѡ���spinner
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
				Toast.makeText(getActivity(), "��ѡ����" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
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
					cb1.setChecked(true);// ѡ��
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
				Toast.makeText(getActivity(), "��ѡ����" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getActivity(), "��ѡ����" + demoData.get(arg2), Toast.LENGTH_SHORT).show();
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
				builder.setTitle("�����Ϣ");
				String information = "";
				for (int j = 0; j < 3; j++) {
					if (flag[j] == 1)
						information += (j + 1) + "��" + chosenDemo[j] + " ";
				}
				builder.setMessage("��ѡ�����Ϊ ��" + information);
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "ȷ��", Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "ȡ��", Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	// ��ʾListview������
	private void setListView() {
		// ��ʼ��������
		myadapter = new MyListAdapter(demoData, status, getActivity());
		// ��Ӳ�����ʾ
		list.setAdapter(myadapter);

		if (!demoname.equals(""))
			add_demo_layout.setVisibility(View.VISIBLE);

		// ��ӵ���¼�
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Item������¼�", Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("������Ϣ");
				builder.setMessage("�������" + (arg2 + 1) + ":" + demoData.get(arg2) + "����");
				builder.setPositiveButton("����¼��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						TV_LOAD = (TextView) arg1.findViewById(R.id.demomanager_information_load);
						CB = (CheckBox) arg1.findViewById(R.id.checkBox_chosen);
						TV_LOAD.setText("������...");
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
				builder.setNegativeButton("ɾ������", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String delete = demoData.get(arg2).toString();
						demoData.remove(arg2);
						status.remove(arg2);
						manager_Total_data.setText(demoData.size() + "");
						myadapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "�ɹ�ɾ��" + delete, Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();
				return true;
			}
		});

	}

	// ����µ�����
	private void addnewdomo() {
		tv_adddemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// ͨ��LayoutInflater������һ��xml�Ĳ����ļ���Ϊһ��View����
				View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_alert_dialog, null);
				// ���������Լ�����Ĳ����ļ���Ϊ�������Content
				final TextView tname = (TextView) view.findViewById(R.id.tvname);
				final EditText name = (EditText) view.findViewById(R.id.etname);

				builder.setTitle("��������");
				builder.setMessage("������������������");
				// builder.setView(new EditText(parentContext));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setView(view);
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss(); // �ر�dialog
						String saying = name.getText().toString().trim();
						// ����������б�
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
				builder.setNegativeButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						Toast.makeText(getActivity(), "����" + i, Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();

			}
		});
	}

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 1) { // ����
				myadapter.notifyDataSetChanged();
			}
			if (msg.what == 2) { // ��ʾ����
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
				TV_LOAD.setText("������...");
				CB.setTextColor(Color.BLUE);
			}
		}
	};

}
