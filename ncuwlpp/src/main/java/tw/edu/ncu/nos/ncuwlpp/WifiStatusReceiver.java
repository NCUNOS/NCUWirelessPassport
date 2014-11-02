package tw.edu.ncu.nos.ncuwlpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Davy on 3/18/14.
 */
public class WifiStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LoginHelper.login(context);
    }
}