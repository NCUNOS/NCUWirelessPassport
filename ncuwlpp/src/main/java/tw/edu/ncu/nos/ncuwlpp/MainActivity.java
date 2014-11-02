
package tw.edu.ncu.nos.ncuwlpp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private EditText usernameEditText;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinner;
    private EditText passwordEditText;
    private Account account;

    static public final String[] DOMAIN_LIST = {"@cc.ncu.edu.tw", "@ncu.edu.tw", "@alumni.ncu.edu.tw", "iTaiwan"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSlide();
        String version = "";
        try {
            version = " v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.setTitle(this.getTitle() + version);
        initSpinner();

        usernameEditText = (EditText) findViewById(R.id.et_username);
        passwordEditText = (EditText) findViewById(R.id.et_password);

        account = new Account(this);

        restorePrefs();
        LoginHelper.login(this);
    }

    public void initSlide() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void saveBthOnclick(View view) {
        savePrefs();
    }

    private void restorePrefs() {
        usernameEditText.setText(account.getUsername());
        passwordEditText.setText(account.getPassword());
        String domain = account.getDomain();
        int index = Arrays.asList(DOMAIN_LIST).indexOf(domain);
        if (index == -1) {
            customSpinner(domain);
            index = DOMAIN_LIST.length;
        }
        spinner.setSelection(index);
    }

    private void savePrefs() {
        account.setUsername(usernameEditText.getText().toString())
                .setPassword(passwordEditText.getText().toString())
                .setDomain(spinner.getSelectedItem().toString())
                .savePreferences();
        Toast.makeText(this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
        LoginHelper.login(this);
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner_domain);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(Arrays.asList(DOMAIN_LIST));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        customSpinner("");

        final Context mContext = this;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == arg0.getCount() - 1) {
                    TextView textView = new TextView(mContext);
                    textView.setText("@");
                    textView.setLayoutParams(
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setPadding(16,0,8,0);
                    final EditText editText = new EditText(mContext);
                    editText.setText("edu.tw");
                    editText.setLayoutParams(
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                    );
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setPadding(4,0,4,0);
                    linearLayout.addView(textView);
                    linearLayout.addView(editText);
                    new AlertDialog.Builder(mContext)
                            .setTitle(getResources().getString(R.string.other))
                            .setIcon(android.R.drawable.ic_menu_edit)
                            .setView(linearLayout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    customSpinner(editText.getText().toString());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    customSpinner("");
                                }
                            })
                            .show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void customSpinner(String custom) {
        int selection = spinner.getSelectedItemPosition();
        while (arrayAdapter.getCount() > DOMAIN_LIST.length) {
            arrayAdapter.remove(arrayAdapter.getItem(arrayAdapter.getCount() - 1));
        }
        if (custom.startsWith("@")) {
            custom = custom.substring(1);
        }
        if (!custom.equals("")) {
            arrayAdapter.add("@" + custom);
        }
        arrayAdapter.add(getResources().getString(R.string.other));
        if (selection >= arrayAdapter.getCount()) {
            spinner.setSelection(0);
        }
        else if (selection == arrayAdapter.getCount() - 1) {
            spinner.setSelection(selection - 1);
        }
    }
}