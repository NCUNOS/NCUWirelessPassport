package tw.edu.ncu.nos.ncuwlpp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Davy on 3/18/14.
 */
public class LoginHelper {
    final static Logger loggerWifiStatus = Logger.getLogger("tw.davy.ncuwlpp.WifiStatus");
    final static Logger loggerConnection = Logger.getLogger("tw.davy.ncuwlpp.Connection");
    final static String NOTIFICATION_CONNECTION = "tw.davy.ncuwlpp.Connection";
    public static void login(final Context context)
    {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Resources resources = context.getResources();
        Notification notification;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED)
        {
            loggerWifiStatus.log(Level.INFO, "Disconnected");
            notificationManager.cancel(NOTIFICATION_CONNECTION, 0);
        }
        else
        {
            WifiInfo wifiInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            if (wifiInfo == null)
                return;
            String ssid = wifiInfo.getSSID();
            if (ssid == null) {
                ssid = "";
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            loggerWifiStatus.log(Level.INFO, "Connected to " + ssid);

            final List<String> wirelesses = Arrays.asList(resources.getStringArray(R.array.wirelesses));
            int index = wirelesses.indexOf(ssid);
            final WirelessLoginer wirelessLoginer;
            WirelessLoginer tryLoginer = null;
            if (index >= 0) {
                try {
                    tryLoginer = (WirelessLoginer) Class
                            .forName(wirelesses.get(index + 1))
                            .getConstructor(Context.class)
                            .newInstance(context);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            wirelessLoginer = tryLoginer;
            final String wirelessSSID = ssid;
            if (wirelessLoginer != null) {
                loggerConnection.log(Level.INFO, "Connected");
                notification = new NotificationBuilder(context, wirelessSSID)
                        .setContentText(resources.getString(R.string.login_checking))
                        .setProgress(0, 0, true)
                        .build();
                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);

                new Thread() {
                    @Override
                    public void run() {
                        Notification notification;
                        if (wirelessLoginer.isLogined()) {
                            loggerConnection.log(Level.INFO, "Already Logged in");
                            notification = new NotificationBuilder(context, wirelessSSID)
                                    .setContentText(resources.getString(R.string.login_logged))
                                    .build();
                            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                            notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);
                        }
                        else if (!wirelessLoginer.isSupport()) {
                            Intent notifyIntent = new Intent(context, MainActivity.class);
                            notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent appIntent = PendingIntent.getActivity(context,
                                    0, notifyIntent, 0);
                            notification = new NotificationBuilder(context, wirelessSSID)
                                    .setContentText(String.format(resources.getString(R.string.login_unsupported_in), wirelessSSID))
                                    .setContentIntent(appIntent)
                                    .build();
                            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                            notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);
                        }
                        else {
                            loggerConnection.log(Level.INFO, "Login Processing");
                            notification = new NotificationBuilder(context, wirelessSSID)
                                    .setContentText(resources.getString(R.string.login_logging))
                                    .setProgress(0, 0, true)
                                    .build();
                            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                            notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);


                            if (wirelessLoginer.login())
                            {
                                loggerConnection.log(Level.INFO, "Login Succeed");
                                notification = new NotificationBuilder(context, wirelessSSID)
                                        .setContentText(resources.getString(R.string.login_logged))
                                        .build();
                                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                                notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);
                            }
                            else
                            {
                                loggerConnection.log(Level.INFO, "Login Failed");
                                Intent notifyIntent = new Intent(context, MainActivity.class);
                                notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent appIntent = PendingIntent.getActivity(context,
                                        0, notifyIntent, 0);
                                notification = new NotificationBuilder(context, wirelessSSID)
                                        .setContentText(resources.getString(R.string.login_failed))
                                        .setContentIntent(appIntent)
                                        .build();
                                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                                notificationManager.notify(NOTIFICATION_CONNECTION, 0, notification);
                            }
                        }
                    }
                }.start();
            }
        }
    }

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
