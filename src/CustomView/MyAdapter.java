package CustomView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.lyz.subway.R;

import android.R.string;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.YuvImage;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private String[] str;

	private static final int YTPE_A = 0;
	private static final int YTPE_B = 1;
	private static final int YPTE_MAX_COUNT = YTPE_B + 1;

	public MyAdapter(Context context, ArrayList<Map<String, String>> arrayList, List<Map<String, String>> map) {
		this.context = context;
		this.str = str;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		int type = 0;
		if (str[position] == "a") {
			type = YTPE_A;
		} else if (str[position] == "b") {
			type = YTPE_B;
		}
		return type;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return YPTE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return str[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View coverView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (coverView == null) {
			holder = new ViewHolder();
			switch (getItemViewType(position)) {

			case YTPE_A:
				coverView = View.inflate(context, R.layout.item1, null);
				holder.tx1 = (TextView) coverView.findViewById(R.id.in);
				holder.tx2 = (TextView) coverView.findViewById(R.id.out);
				holder.tx3 = (TextView) coverView.findViewById(R.id.people);
				holder.tx4 = (TextView) coverView.findViewById(R.id.SVT);
				holder.tx5 = (TextView) coverView.findViewById(R.id.SJT);
				break;
			case YTPE_B:
				coverView = View.inflate(context, R.layout.item2, null);
				holder.pr1 = (ProgressBar) coverView
						.findViewById(R.id.progressBar1);
				holder.pr2 = (ProgressBar) coverView
						.findViewById(R.id.progressBar2);
				break;
			}
			coverView.setTag(holder);
		} else {
			holder = (ViewHolder) coverView.getTag();
			switch (getItemViewType(position)) {
			case YTPE_A:
				holder.tx1.setText("....");
				break;

			case YTPE_B:
				holder.pr1.setTag("...");
				holder.pr2.setTag("..");
				break;
			}
		}
		return coverView;
	}

	class ViewHolder {
		private TextView tx1, tx2, tx3, tx4, tx5;
		private ProgressBar pr1, pr2;
	}
}
