package com.restws.cync.kamal.cync.Sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kamal on 06-04-2016.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 30*2* 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 12*2;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    private Set<String> fetchContactNumbers() {

        String numId = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor contactNamesCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC ");

        Set<String> numberSet = new HashSet<>();

        while (contactNamesCursor.moveToNext()) {
            String number = contactNamesCursor.getString(contactNamesCursor.getColumnIndex(numId));
            numberSet.add(number);
        }

        return numberSet;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpClient client = null;
        BufferedReader reader = null;
        String ContactsJsonStr = null;
        String outputFormat = "json";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myNumber = prefs.getString("myNumber", "");
        InputStream inputStream = null;

        try {
            final String CONTACTS_COMM_SERVER_BASE_URL =
                    "https://contactapi-developer-edition.na22.force.com/services/apexrest/MyContacts";

            client = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(CONTACTS_COMM_SERVER_BASE_URL);

            String jsonString = "";
            String result;

            ArrayList<String> contactNumbers = new ArrayList<>();

            // preparing json to send
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("phones", fetchContactNumbers());

            jsonString = jsonObject.toString();
            StringEntity sEntity = new StringEntity(jsonString);
            httpPost.setEntity(sEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = client.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Error";

            Log.v("checkThis", result.toString());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;

    }
}
