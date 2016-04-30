package com.restws.cync.kamal.cync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by kamal on 16-04-2016.
 */
public class LoginChainActivities extends AsyncTask<Object, Void, Boolean> {

    Context context;

    LoginChainActivities(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        String firstName = (String) params[0];
        String lastName = (String) params[1];
        String email = (String) params[2];
        String password = (String) params[3];
        String number = (String) params[4];
        String localIp = (String) params[5];

        try {
            signUpUser(firstName, lastName, email, password, number, localIp);
            createRegistry();
            populateRegistry(email, "OK");
            insertCurrentContact(number);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void createRegistry() {
        CyncDBHelper dbHelper = new CyncDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        db.close();
        dbHelper.close();
    }

    private void populateRegistry(String email, String status) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(CyncDBContract.RegistrationEntry.COLUMN_ID, 0);
        insertValues.put(CyncDBContract.RegistrationEntry.COLUMN_EMAIL, email);
        insertValues.put(CyncDBContract.RegistrationEntry.COLUMN_REGISTER_STATUS, status);
        CyncDBHelper dbHelper = new CyncDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onInsert(db, CyncDBContract.RegistrationEntry.TABLE_NAME, insertValues);
        db.close();
        dbHelper.close();
    }

    private void insertCurrentContact(String number) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(CyncDBContract.CurrentContactEntry.COLUMN_ID, 0);
        insertValues.put(CyncDBContract.CurrentContactEntry.COLUMN_CURRENT_CONTACT, number);
        CyncDBHelper dbHelper = new CyncDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onInsert(db, CyncDBContract.CurrentContactEntry.TABLE_NAME, insertValues);
        db.close();
        dbHelper.close();
    }



    // reference : http://hmkcode.com/android-send-json-data-to-server/
    private boolean signUpUser(String firstName, String lastName, String email, String password,
                               String number, String ipAdressUrl) throws JSONException, IOException {

        String signUp = SignUpActivity.BASEURL + "Logins";
        HttpClient client = new DefaultHttpClient();

        HttpPost httpClientPost = new HttpPost(signUp);

        String jsonString = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("fname", firstName);
        jsonObject.accumulate("lname", lastName);
        jsonObject.accumulate("email", email);
        jsonObject.accumulate("password", password);
        jsonObject.accumulate("phone", number);
        jsonObject.accumulate("url", ipAdressUrl);

        jsonString = jsonObject.toString();

        StringEntity sEntity = new StringEntity(jsonString);

        httpClientPost.setEntity(sEntity);
        httpClientPost.setHeader("Accept", "application/json");
        httpClientPost.setHeader("Content-type", "application/json");

        client.execute(httpClientPost);

        return true;
    }
}

