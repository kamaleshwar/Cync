package com.restws.cync.kamal.cync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kamal on 16-04-2016.
 */
public class ContactsLatentInfo {

    Context mContext;
    List<ContentValues> mLocalContactList;
    final String CONTACTS_COMM_SERVER_BASE_URL =
            "https://contactapi-developer-edition.na22.force.com/services/apexrest/MyContacts";
    final String dbNotifierIntent = "com.restws.cync.kamal.cync.fragmentupdater";

    public ContactsLatentInfo(Context context, List<ContentValues> localContactList) {
        this.mContext = context;
        this.mLocalContactList = localContactList;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public void getContactsIP() {
        HttpClient client = null;
        BufferedReader reader = null;
        InputStream inputStream = null;

        try {

            client = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(CONTACTS_COMM_SERVER_BASE_URL);
            String jsonString;
            String result;


            JSONArray numArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            for (ContentValues contact : this.mLocalContactList) {
                String contactNumber = contact.get(CyncDBContract.ContactsEntry.COLUMN_NUMBER).toString();
                JSONObject subJsonObj = new JSONObject();
                subJsonObj.put("phone", contactNumber);
                numArray.put(subJsonObj);
            }

            // preparing json to send

            jsonObject.accumulate("phones", numArray);
            jsonString = jsonObject.toString();

            StringEntity sEntity = new StringEntity(jsonString);
            httpPost.setEntity(sEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = client.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Error";
            }

            List<ContentValues> contactContentList = storeAndProcessOutput(result);
            persistServerContacts(contactContentList);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null && reader != null) {
                try {
                    inputStream.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }

    private void persistServerContacts(List<ContentValues> paramList)
    {
        SQLiteDatabase localSQLiteDatabase = new CyncDBHelper(this.mContext).getWritableDatabase();
        localSQLiteDatabase.beginTransaction();
        long insertResult = 0;
        try
        {
            Iterator localIterator = paramList.iterator();
            while (localIterator.hasNext()) {
                insertResult = localSQLiteDatabase.insertWithOnConflict(CyncDBContract.ServerContactsEntry.TABLE_NAME,
                        null, (ContentValues)localIterator.next(), SQLiteDatabase.CONFLICT_IGNORE);
            }
            localSQLiteDatabase.setTransactionSuccessful();
        }
        finally
        {
            if(insertResult > 0){
                Intent intent = new Intent(dbNotifierIntent);
                mContext.sendBroadcast(intent);
            }
            localSQLiteDatabase.endTransaction();
            localSQLiteDatabase.close();
        }
    }

    private List<ContentValues> storeAndProcessOutput(String paramString)
    {
        List<ContentValues> localArrayList = new ArrayList();
        try
        {
            JSONObject localJSONObject = new JSONObject((String)new JSONTokener(paramString).nextValue());
            Iterator localIterator = localJSONObject.keys();

                while (localIterator.hasNext())
                {
                    ContentValues localContentValues = new ContentValues();
                    String contactNumber = (String)localIterator.next();
                    String contactIp = localJSONObject.getString(contactNumber);
                    localContentValues.put("contact_number", contactNumber);
                    localContentValues.put("IP", contactIp);
                    localArrayList.add(localContentValues);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        return localArrayList;
    }

}
