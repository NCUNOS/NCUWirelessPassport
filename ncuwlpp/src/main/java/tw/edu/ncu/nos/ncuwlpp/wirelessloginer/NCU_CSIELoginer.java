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
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import tw.edu.ncu.nos.ncuwlpp.Account;
import tw.edu.ncu.nos.ncuwlpp.LoginHelper;
import tw.edu.ncu.nos.ncuwlpp.MainActivity;
import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class NCU_CSIELoginer extends WirelessLoginer {
    @Override
    protected String getTestURL() { return "http://10.115.50.254"; }
    @Override
    protected String getLoginURL() { return "https://10.115.50.254"; }
    @Override
    protected InputStream getSSLCAStream() { return context.getResources().openRawResource(R.raw.pluto248_csie_ncu_edu_tw); }
    @Override
    protected String getLoginSucceedMessage() { return "<p id=\"success\">"; }
    @Override
    protected String getLoginFailedMessage() { return "<p id=\"failed\">"; }

    public NCU_CSIELoginer(Context context) {
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
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    total.append(line);
                }
                if (total.toString().contains(getLoginSucceedMessage())) {
                    logined = true;
                    tests = 3;
                }
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

    @Override
    protected List<NameValuePair> getLoginParams()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        /*
         *  <option value="0">資工系帳號</option>
         *  <option value="1">計中帳號</option>
         *  <option value="2">校際無線漫遊</option>
         */
        int loginType = 2;
        Account account = new Account(context);
        String domain = account.getDomain();
        if (Arrays.asList(MainActivity.DOMAIN_LIST).contains(domain)) {
            loginType = 1;
            if (isNCUCSIEUser(account.getUsername()) && false) {
                loginType = 0;
            }
        }
        if (domain.equals("iTaiwan")) {
            domain = "@itw";
            loginType = 2;
        }
        params.add(new BasicNameValuePair("login[type]", String.valueOf(loginType)));
        params.add(new BasicNameValuePair("login[username]", account.getUsername() + (loginType == 2 ? domain : "")));
        params.add(new BasicNameValuePair("login[password]", account.getPassword()));
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

    private boolean isNCUCSIEUser(String username) {
        int college, department;
        if (username.startsWith("9")) {
            college = Integer.parseInt(username.substring(2, 3));
            department = Integer.parseInt(username.substring(5, 6));
        }
        else if (username.startsWith("1")) {
            college = Integer.parseInt(username.substring(3, 4));
            department = Integer.parseInt(username.substring(5, 6));
        }
        else
            return false;
        return (college == 5 && department == 2);
    }
}
