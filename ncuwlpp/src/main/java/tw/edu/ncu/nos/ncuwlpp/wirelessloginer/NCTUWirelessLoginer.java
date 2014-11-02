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
public class NCTUWirelessLoginer extends NCTUGuestLoginer {
    @Override
    public boolean isSupport() {
        Account account = new Account(context);
        return account.getDomain().endsWith("nctu.edu.tw");
    }

    public NCTUWirelessLoginer(Context context) {
        super(context);
    }
}
