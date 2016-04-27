package com.restws.cync.kamal.cync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.restws.cync.kamal.cync.data.CyncDBContract.ContactsEntry;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kamal on 15-04-2016.
 */
public class FetchLocalContactList extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private final String dbNotifyIntent = "com.restws.cync.kamal.cync.fragmentupdater";

    public FetchLocalContactList(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<ContentValues> contentValuesList = fetchContactNumbers();
        storeLocalContacts(contentValuesList);
        ContactsLatentInfo latentInfo = new ContactsLatentInfo(mContext, contentValuesList);
        latentInfo.getContactsIP();
        return null;
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
            if (!duplicateCheck.contains(contactID)) {
                duplicateCheck.add(contactID);
                ContentValues value = new ContentValues();
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
            if(insertResult > 0) {
                Intent intent = new Intent(dbNotifyIntent);
                mContext.sendBroadcast(intent);
            }
            db.endTransaction();
        }
    }
}
