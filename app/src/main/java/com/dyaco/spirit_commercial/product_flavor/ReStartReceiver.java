package com.dyaco.spirit_commercial.product_flavor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dyaco.spirit_commercial.MainActivity;

public class ReStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String action = intent.getAction();
		Log.d("IIIIIIEIEE", "action: " + action);

		if (action.equals("com.dyaco.spirit_commercial.RESTART")) {
			Intent launchIntent = new Intent(context, MainActivity.class);
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
			Log.d("IIIIIIEIEE", "start: 重新啟動");
		}
	}

}
