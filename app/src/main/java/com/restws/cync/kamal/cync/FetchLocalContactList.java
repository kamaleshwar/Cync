package com.restws.cync.kamal.cync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.restws.cync.kamal.cync.data.CyncDBContract.ContactsEntry;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kamal on 15-04-2016.
 */
public class FetchLocalContactList extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private final String dbNotifyIntent = "com.restws.cync.kamal.cync.fragmentupdater";
    private final String IP_UPDATE_URL = "https://contactapi-developer-edition.na22.force.com/services/apexrest/Logins";

    public FetchLocalContactList(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        updateIP();
        List<ContentValues> contentValuesList = fetchContactNumbers();
        storeLocalContacts(contentValuesList);
        ContactsLatentInfo latentInfo = new ContactsLatentInfo(mContext, contentValuesList);
        latentInfo.getContactsIP();
        return null;
    }

    private void updateIP() {
        HttpClient client = null;
        BufferedReader reader = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String number_Key = "m_number";
        String updatedNumber = prefs.getString(number_Key, "xxx");
        try {

            client = new DefaultHttpClient();

            HttpPut httpPut = new HttpPut(IP_UPDATE_URL);
            String jsonString;

            JSONObject jsonObject = new JSONObject();
            String latestIP = getLocalIp();
            jsonObject.put("phone", updatedNumber);
            jsonObject.put("url", latestIP);

            jsonString = jsonObject.toString();

            StringEntity sEntity = new StringEntity(jsonString);
            httpPut.setEntity(sEntity);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            client.execute(httpPut);

        } catch (JSONException | IOException e) {
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(e.getLocalizedMessage(), e.getMessage(), e);
                }
            }
        }

        return;

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
            Log.e(ex.getLocalizedMessage(), ex.getMessage(), ex);
        }
        return "";
    }

    private List<ContentValues> fetchContactNumbers() {

        String contactNumberCol = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String contactIdCol = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String contactNameCol = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor contactsCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC ");

        ArrayList<ContentValues> contentValuesList = new ArrayList<>();

        Set<String> duplicateCheck = new HashSet<>();

        while (contactsCursor.moveToNext()) {
            String contactNumber = contactsCursor.getString(contactsCursor.getColumnIndex(contactNumberCol));
            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(contactNameCol));
            String contactID = contactsCursor.getString(contactsCursor.getColumnIndex(contactIdCol));
            int dummyId = 0;
            if (!duplicateCheck.contains(contactNumber)) {
                duplicateCheck.add(contactNumber);
                ContentValues value = new ContentValues();
                value.put(ContactsEntry.COLUMN_ID, dummyId);
                value.put(ContactsEntry.COLUMN_NAME, contactName);
                value.put(ContactsEntry.COLUMN_NUMBER, contactNumber.replaceAll("[+()-]", "").replace(" ", ""));
                value.put(ContactsEntry.COLUMN_CONTACT_ID, contactID);
                contentValuesList.add(value);
            }
        }
        contactsCursor.close();
        return contentValuesList;
    }


    void storeLocalContacts(List<ContentValues> contentValuesList) {

        CyncDBHelper dbHelper = new CyncDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        long insertResult = 0;
        try {
            for (ContentValues value : contentValuesList) {
                insertResult = db.insertWithOnConflict(ContactsEntry.TABLE_NAME,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
        } finally {
            if (insertResult > 0) {
                Intent intent = new Intent(dbNotifyIntent);
                mContext.sendBroadcast(intent);
            }
            db.endTransaction();
            db.close();
            dbHelper.close();
        }
    }
}
