package tw.edu.ncu.nos.ncuwlpp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Davy on 3/22/14.
 */
public class Account {
    private String username = "";
    private String password = "";
    private String domain = "";
    private SharedPreferences sharedPreferences = null;
    private boolean ncu_csie_user = false;

    public Account(Context context) {
        this.sharedPreferences = context.getSharedPreferences("tw.davy.ncuwlpp.Connection", 0);
        this.loadPreferences();
    }

    public void loadPreferences() {
        setUsername(sharedPreferences.getString("username", ""));
        setPassword(sharedPreferences.getString("password", ""));
        setDomain(sharedPreferences.getString("domain", MainActivity.DOMAIN_LIST[0]));
    }

    public void savePreferences() {
        this.sharedPreferences.edit()
                .putString("username", getUsername())
                .putString("password", getPassword())
                .putString("domain", getDomain())
                .commit();
    }

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public Account setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDomain() {
        return this.domain;
    }

    public boolean isVaild() {
        return !this.domain.equals("");
    }
}
