package tw.edu.ncu.nos.ncuwlpp.wirelessloginer;

import android.content.Context;

import java.io.InputStream;

import tw.edu.ncu.nos.ncuwlpp.R;
import tw.edu.ncu.nos.ncuwlpp.WirelessLoginer;

/**
 * Created by Davy on 3/22/14.
 */
public class NCUWLLoginer extends WirelessLoginer {
    protected String getLoginURL() { return "https://securelogin.arubanetworks.com/auth/index.html/u"; }
    protected String getLoginSucceedURL() { return null; }
    protected String getLoginFailedURL() { return "https://securelogin.arubanetworks.com/upload/custom/default/index.htm?errmsg=Authentication failed"; }
    protected InputStream getSSLCAStream() { return context.getResources().openRawResource(R.raw.securelogin_arubanetworks_com); }
    protected String getLoginSucceedMessage() { return "User NCUWLLoginerAuthenticated"; }
    protected String getLoginFailedMessage() { return "Authentication failed"; }

    public NCUWLLoginer(Context context) {
        super(context);
    }
}
