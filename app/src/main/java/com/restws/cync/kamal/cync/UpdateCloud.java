package com.restws.cync.kamal.cync;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by kamal on 21-04-2016.
 */
public class UpdateCloud extends AsyncTask<Object, Void, Boolean> {

    String old;
    String updated;
    private final String oldNumKey = "old_phone";
    private final String newNumKey = "new_phone";
    private final String cloudEndpoint = "MyContacts";

    UpdateCloud(String old, String updated) {
        this.old = old;
        this.updated = updated;
    }


    @Override
    protected Boolean doInBackground(Object... params) {
        updateCloud();
        return true;
    }

    public void updateCloud() {
        String signUp = SignUpActivity.BASEURL + cloudEndpoint;
        HttpClient client = new DefaultHttpClient();

        HttpPut httpClientPut = new HttpPut(signUp);

        String jsonString = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate(oldNumKey, old);
            jsonObject.accumulate(newNumKey, updated);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonString = jsonObject.toString();

        StringEntity sEntity = null;
        try {
            sEntity = new StringEntity(jsonString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpClientPut.setEntity(sEntity);
        httpClientPut.setHeader("Accept", "application/json");
        httpClientPut.setHeader("Content-type", "application/json");

        try {
            client.execute(httpClientPut);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
