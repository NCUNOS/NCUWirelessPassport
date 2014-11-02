package tw.edu.ncu.nos.ncuwlpp;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Davy on 3/22/14.
 */
public abstract class WirelessLoginer {
    protected String getTestURL() { return "http://www.google.com.tw/?" + Math.random(); }
    protected abstract String getLoginURL();
    protected String getLoginSucceedURL() { return null; }
    protected String getLoginFailedURL() { return null; }
    protected InputStream getSSLCAStream() { return null; }
    protected String getLoginSucceedMessage() { return ""; }
    protected String getLoginFailedMessage() { return ""; }

    protected Context context = null;

    public WirelessLoginer(Context context) {
        this.context = context;
    }

    public boolean isSupport() {
        return true;
    }

    public boolean isLogined() {
        HttpURLConnection urlConnection = null;
        boolean logined = false;
        int tests = 0;
        while (tests < 5) {
            try {
                URL testLoginUrl = new URL(getTestURL());
                urlConnection = (HttpURLConnection) testLoginUrl.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.getInputStream(); // for start connecting

                Logger.getLogger("tw.davy.ncuwlpp").log(Level.INFO, urlConnection.getURL().toString());
                if (testLoginUrl.getHost().equals(urlConnection.getURL().getHost())) {
                    logined = true;
                    break;
                }
                tests = 5; // done
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
        HttpsURLConnection urlConnection = null;
        boolean logined = false;
        try {
            urlConnection = getLoginConnectionSSL();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(LoginHelper.getQuery(getLoginParams()));
            writer.flush();
            writer.close();
            os.close();

            if (getLoginSucceedURL() != null && new URL(getLoginSucceedURL()).equals(urlConnection.getURL())) {
                logined = true;
            }
            else {
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

    protected List<NameValuePair> getLoginParams()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Account account = new Account(context);

        String domain = account.getDomain();
        if (domain.equals("iTaiwan")) {
            domain = "@itw";
        }
        String username = account.getUsername() + domain;
        String password = account.getPassword();
        params.add(new BasicNameValuePair("user", username));
        params.add(new BasicNameValuePair("password", password));
        return params;
    }

    protected HttpURLConnection getLoginConnection()
    {
        try {
            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL loginUrl = new URL(getLoginURL());
            HttpURLConnection urlConnection = (HttpURLConnection) loginUrl.openConnection();
            // set Timeout and method
            urlConnection.setReadTimeout(7000);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            return urlConnection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected HttpsURLConnection getLoginConnectionSSL()
    {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getSSLCAStream();
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL loginUrl = new URL(getLoginURL());
            HttpsURLConnection urlConnection = (HttpsURLConnection) loginUrl.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            // set Timeout and method
            urlConnection.setReadTimeout(7000);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            return urlConnection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
