package tw.edu.ncu.nos.ncuwlpp.wirelessloginer;

import android.content.Context;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import tw.edu.ncu.nos.ncuwlpp.Account;
import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class NTULoginer extends WirelessLoginer {
    protected String getLoginURL() { return "https://wl122.cc.ntu.edu.tw/auth/loginnw.html"; }
    protected String getLoginSucceedURL() { return "http://www.ntu.edu.tw/"; }
    protected String getLoginFailedURL() { return null; }
    protected InputStream getSSLCAStream() { return context.getResources().openRawResource(R.raw.wireless_ntu_edu_tw); }
    protected InputStream getSSLCA2Stream() { return context.getResources().openRawResource(R.raw.wl122_cc_ntu_edu_tw); }
    protected String getLoginSucceedMessage() { return "認識台大"; }
    protected String getLoginFailedMessage() { return "ntu_peap請用計中帳號登入"; }

    @Override
    public boolean isSupport() {
        Account account = new Account(context);
        return !account.getDomain().equals("iTaiwan");
    }

    public NTULoginer(Context context) {
        super(context);
    }

    @Override
    protected HttpsURLConnection getLoginConnectionSSL()
    {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getSSLCAStream(), ca2Input = getSSLCA2Stream();
            Certificate ca, ca2;
            try {
                ca = cf.generateCertificate(caInput);
                ca2 = cf.generateCertificate(ca2Input);
            } finally {
                caInput.close();
                ca2Input.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            keyStore.setCertificateEntry("ca", ca2);

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
    /*
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
    */
}
