package tw.edu.ncu.nos.ncuwlpp.wirelessloginer;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tw.edu.ncu.nos.ncuwlpp.Account;
import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class NCTUGuestLoginer extends WirelessLoginer {
    protected String getLoginURL() { return "https://securelogin.arubanetworks.com/auth/index.html"; }
    protected String getLoginSucceedURL() { return null; }
    protected String getLoginFailedURL() { return null; }
    protected InputStream getSSLCAStream() { return context.getResources().openRawResource(R.raw.securelogin_arubanetworks_com); }
    protected String getLoginSucceedMessage() { return "External Welcome Page"; }
    protected String getLoginFailedMessage() { return "Authentication failed"; }

    @Override
    public boolean isSupport() {
        Account account = new Account(context);
        return !(account.getDomain().equals("iTaiwan") || account.getDomain().endsWith("nctu.edu.tw"));
    }

    public NCTUGuestLoginer(Context context) {
        super(context);
    }

    @Override
    protected List<NameValuePair> getLoginParams()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Account account = new Account(context);

        params.add(new BasicNameValuePair("user", account.getUsername() + account.getDomain()));
        params.add(new BasicNameValuePair("password", account.getPassword()));
        params.add(new BasicNameValuePair("cmd", "authenticate"));
        return params;
    }
}
