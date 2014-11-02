package tw.edu.ncu.nos.ncuwlpp.wirelessloginer;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import tw.edu.ncu.nos.ncuwlpp.Account;
import tw.edu.ncu.nos.ncuwlpp.LoginHelper;
import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class LITLoginer extends WirelessLoginer {
    protected String loginHost = "192.168.200.254";
    protected String getLoginURL() { return "https://" + loginHost + "/authUser.php"; }
    protected String getLoginSucceedMessage() { return "submitForm(\"logoff\")"; }
    protected InputStream getSSLCAStream() { return context.getResources().openRawResource(R.raw.ncu_lit); }

    public LITLoginer(Context context) {
        super(context);
    }

    @Override
    public boolean isLogined() {
        HttpURLConnection urlConnection = null;
        boolean logined = false;
        int tests = 0;
        while (tests < 3) {
            try {
                URL testLoginUrl = new URL(getTestURL());
                urlConnection = (HttpURLConnection) testLoginUrl.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.getInputStream();
                if (urlConnection.getResponseCode() != 307) {
                    logined = true;
                }
                tests = 3; // done
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            tests++; // try again
        }
        return logined;
    }

    public boolean login() {
        HttpURLConnection urlConnection = null;
        boolean logined = false;
        try {
            URL testLoginUrl = new URL(getTestURL());
            urlConnection = (HttpURLConnection) testLoginUrl.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.getInputStream(); // for start connecting
            String token = "";
            URL redirectionUrl = new URL(urlConnection.getHeaderField("Location"));
            String[] queries = redirectionUrl.getQuery().split("&");
            for (int i = 0; i < queries.length; ++i) {
                if (queries[i].startsWith("token=")) {
                    token = queries[i].substring(6);
                    break;
                }
            }
            loginHost = redirectionUrl.getHost();

            List<NameValuePair> params = getLoginParams();
            params.add(new BasicNameValuePair("hToken", token));

            urlConnection = getLoginConnectionSSL();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(LoginHelper.getQuery(params));
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

        params.add(new BasicNameValuePair("uName", account.getUsername() + account.getDomain()));
        params.add(new BasicNameValuePair("uPwd", account.getPassword()));
        params.add(new BasicNameValuePair("wlan", "1"));
        params.add(new BasicNameValuePair("cp_type", "2"));
        params.add(new BasicNameValuePair("hFromPage", "login.php"));
        params.add(new BasicNameValuePair("hURL", "127.0.0.1"));
        params.add(new BasicNameValuePair("dest", ""));
        params.add(new BasicNameValuePair("useIndex", "1"));
        return params;
    }

    @Override
    protected HttpsURLConnection getLoginConnectionSSL()
    {
        HttpsURLConnection urlConnection = super.getLoginConnectionSSL();
        urlConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return urlConnection;
    }

}
