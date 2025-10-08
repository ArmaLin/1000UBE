package com.dyaco.spirit_commercial.product_flavor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dyaco.spirit_commercial.MainActivity;

public class InstalledReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String action = intent.getAction();

		Log.d("更新", "action: " + action);

		if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			Intent launchIntent = new Intent(context, MainActivity.class);
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
			Log.d("更新", "start: 重新啟動");
		}
	}

}
