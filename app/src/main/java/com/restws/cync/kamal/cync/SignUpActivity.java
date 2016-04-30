package com.restws.cync.kamal.cync;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;
import com.restws.cync.kamal.cync.service.CyncBackgroundService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements HomeScreenActivityFragment.Callback {

    @Bind(R.id.area_code_input)
    TextView mAreaCode;
    @Bind(R.id.mobile_number_input)
    TextView mPhoneNumber;
    @Bind(R.id.password_input)
    TextView mPassword;
    @Bind(R.id.confirm_password_input)
    TextView mConfirmPassword;
    @Bind(R.id.Email_address_input)
    TextView mEmail;
    @Bind(R.id.signup_button)
    Button mSignUpNutton;
    @Bind(R.id.first_name_input)
    TextView mFirstName;
    @Bind(R.id.last_name_input)
    TextView mLastName;

    public static final String BASEURL = "https://contactapi-developer-edition.na22.force.com/services/apexrest/";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkUserAlreadyLoggedIn()) {
            RequestContactsPermission();
        } else {
            setContentView(R.layout.activity_sign_up);
            ButterKnife.bind(this);
        }
    }

    private Boolean checkUserAlreadyLoggedIn() {
        CyncDBHelper dbHelper = new CyncDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorLogin = db.query(CyncDBContract.RegistrationEntry.TABLE_NAME,
                new String[]{CyncDBContract.RegistrationEntry.COLUMN_REGISTER_STATUS},
                null, null, null, null, null);
        Boolean signedUp = cursorLogin.moveToFirst() ? true : false;
        cursorLogin.close();
        db.close();
        dbHelper.close();
        return signedUp;
    }

    @OnClick(R.id.signup_button)
    public void signUp() {
        String email = mEmail.getText().toString();
        String areaCode = mAreaCode.getText().toString();
        String number = areaCode + mPhoneNumber.getText().toString();
        String password = mPassword.getText().toString();
        String confirmedPassword = mConfirmPassword.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String localIp = getLocalIp();

        if (!password.equals(confirmedPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email address", Toast.LENGTH_SHORT).show();
        } else {
            LoginChainActivities task = new LoginChainActivities(getApplicationContext());
            task.execute(new String[]{firstName, lastName, email, password, number, localIp});
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("m_number", number);
            editor.apply();
            startActivity(new Intent(this, HomeScreenActivity.class));

        }
    }

    private void RequestContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            FetchLocalContactList localContactList = new FetchLocalContactList(getApplicationContext());
            localContactList.execute();
            startService(new Intent(this, CyncBackgroundService.class));
            startActivity(new Intent(this, HomeScreenActivity.class));
        }
    }

    // code taken from http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
    private String getLocalIp() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface eachInterface : interfaces) {
                List<InetAddress> addressList = Collections.list(eachInterface.getInetAddresses());
                for (InetAddress eachAddress : addressList) {
                    if (!eachAddress.isLoopbackAddress()) {
                        String sAddr = eachAddress.getHostAddress();
                        if (sAddr.indexOf(':') < 0)
                            return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @Override
    public void onItemSelected(Uri dateUri) {

    }
}



