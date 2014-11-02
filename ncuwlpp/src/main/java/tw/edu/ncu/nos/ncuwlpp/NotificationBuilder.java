package tw.edu.ncu.nos.ncuwlpp;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Davy on 3/22/14.
 */
public class NotificationBuilder extends NotificationCompat.Builder {
    public NotificationBuilder(Context mContext, String ssid) {
        super(mContext);

        this.setSmallIcon(R.drawable.ic_stat_device_access_network_wifi);
        this.setContentTitle(String.format(mContext.getResources().getString(R.string.connected_to), ssid));
    }
}
