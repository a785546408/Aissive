package com.tjs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lyz.subway.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyListAdapter extends BaseAdapter {
	private ArrayList<String> demoData = new ArrayList<String>();
	private Context parentContext;
	private ArrayList<Boolean> status = new ArrayList<Boolean>();
	private int num = 0;

	public MyListAdapter(ArrayList<String> demoData, ArrayList<Boolean> status, Context view) {
		this.demoData = demoData;
		this.parentContext = view;
		this.status = status;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return demoData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 获取布局文件
		if (view == null) {
			view = (RelativeLayout) RelativeLayout.inflate(parentContext, R.layout.list_item_demomanager_information,
					null);
		}
		// 获取控件
		TextView name = (TextView) view.findViewById(R.id.demomanager_information_load);
		final CheckBox ck = (CheckBox) view.findViewById(R.id.checkBox_chosen);
		Log.e("1111", status.get(position) + "");
		ck.setChecked(status.get(position));
		if (demoData != null) {
			ck.setText("样本"+( position+1)+": "+demoData.get(position));
			ck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					status.add(position, !status.get(position));

					if (num < 3) {
						if (isChecked) {
							num++;
						} else {
							num--;
						}
					} else {
						Toast.makeText(parentContext, "您选了超过三个的demo", Toast.LENGTH_SHORT).show();
						if (isChecked) {
							ck.setChecked(false);
						} else {
							num--;
						}
					}

					Toast.makeText(parentContext, num + " ", Toast.LENGTH_SHORT).show();
				}
			});

		}
		return view;
	}

}
