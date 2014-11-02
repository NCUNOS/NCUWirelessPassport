package tw.edu.ncu.nos.ncuwlpp.wirelessloginer;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import tw.edu.ncu.nos.ncuwlpp.Account;
import tw.edu.ncu.nos.ncuwlpp.LoginHelper;
import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class NCUCELoginer extends WirelessLoginer {
    protected String getLoginURL() { return "http://192.115.152.254:8000/"; }
    protected String getLoginSucceedURL() { return "http://www.ce.ncu.edu.tw/ce/"; }
    protected String getLoginSucceedMessage() { return "http://www.ce.ncu.edu.tw"; }

    public NCUCELoginer(Context context) {
        super(context);
    }

    @Override
    public boolean isSupport() {
        Account account = new Account(context);
        return !account.getDomain().equals("iTaiwan");
    }

    public boolean login() {
        HttpURLConnection urlConnection = null;
        boolean logined = false;
        try {
            urlConnection = getLoginConnection();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(LoginHelper.getQuery(getLoginParams()));
            writer.flush();
            writer.close();
            os.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                total.append(line);
            }
            if (total.toString().contains(getLoginSucceedMessage())) {
                logined = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return logined;
    }

    @Override
    protected List<NameValuePair> getLoginParams()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Account account = new Account(context);

        params.add(new BasicNameValuePair("auth_user", account.getUsername() + account.getDomain()));
        params.add(new BasicNameValuePair("auth_pass", account.getPassword()));
        return params;
    }

}
