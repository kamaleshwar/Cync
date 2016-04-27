package com.restws.cync.kamal.cync.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.restws.cync.kamal.cync.FetchLocalContactList;
import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kamal on 26-04-2016.
 */
public class CyncIpUpdatesService extends Service {

    public CyncIpUpdatesService() {
        super();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(3000);
                        FetchLocalContactList localContactList = new FetchLocalContactList(getBaseContext());
                        localContactList.execute();
                    }
                } catch (InterruptedException e) {
                }

            }

        }).start();

        return START_STICKY;
    }

            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }

            private List<ContentValues> fetchContactNumbers() {

                String contactNumberCol = ContactsContract.CommonDataKinds.Phone.NUMBER;
                String contactIdCol = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                String contactNameCol = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

                Cursor contactsCursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
                    if (!duplicateCheck.contains(contactID)) {
                        duplicateCheck.add(contactID);
//                String reverseNumber = new StringBuilder(contactNumber.replaceAll("[+()-]", "").replace(" ", "")).reverse().toString();
                        ContentValues value = new ContentValues();
                        value.put(CyncDBContract.ContactsEntry.COLUMN_NAME, contactName);
                        value.put(CyncDBContract.ContactsEntry.COLUMN_NUMBER, contactNumber.replaceAll("[+()-]", "").replace(" ", ""));
                        value.put(CyncDBContract.ContactsEntry.COLUMN_CONTACT_ID, contactID);
                        contentValuesList.add(value);
                    }
                }
                contactsCursor.close();
                return contentValuesList;
            }

            void storeLocalContacts(List<ContentValues> contentValuesList) {

                CyncDBHelper dbHelper = new CyncDBHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                long insertResult = 0;
                try {
                    for (ContentValues value : contentValuesList) {
                        insertResult = db.insertWithOnConflict(CyncDBContract.ContactsEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    if (insertResult > 0) {
                        Intent intent = new Intent("com.restws.cync.kamal.cync.fragmentupdater");
                        getBaseContext().sendBroadcast(intent);
                    }
                    db.endTransaction();
                }
            }


        }
