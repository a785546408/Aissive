package com.zl.bluetooth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.util.Log;

public class Dialog {
	private static ProgressDialog createProgressDialog(Context context, String message) {

		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(false);
		dialog.setMessage(message);

		return dialog;
	}

	public static void indeterminateInternal(Context context, final Handler handler, String message,
			final Runnable runnable, OnDismissListener dismissListener, boolean cancelable) {

		final ProgressDialog dialog = createProgressDialog(context, message);
		dialog.setCancelable(cancelable);

		if (dismissListener != null) {
			dialog.setOnDismissListener(dismissListener);
		}

		dialog.show();

		new Thread() {

			@Override
			public void run() {
				runnable.run();
			};
		}.start();

		
		//一个临时的方法，仅让进度条框显示10秒钟，蓝后消失
		new Thread() {
			public void run() {
				int i = 0;
				try {
					while (true) {
						Log.d("dialog_time", i + "");
						Thread.sleep(1000);//
						i++;

						if (i == 10)
							break;
					}
					dialog.dismiss();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			};

		}.start();
	}
}
