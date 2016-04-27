package com.restws.cync.kamal.cync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

public class BackgroundWebServerRequest
        extends AsyncTask<Void, Void, Void>
{
    private static final SQLiteQueryBuilder sRegisteredContacts = new SQLiteQueryBuilder();
    private Context mContext;
    private String updatedNumber = null;
    private String oldContact;

    static
    {
        sRegisteredContacts.setTables(CyncDBContract.ServerContactsEntry.TABLE_NAME+" LEFT JOIN " +
                CyncDBContract.ContactsEntry.TABLE_NAME +" ON "+
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER +" = "+ CyncDBContract.ContactsEntry.COLUMN_NUMBER);
    }

    BackgroundWebServerRequest(String oldContact, String paramString, Context paramContext)
    {
        this.mContext = paramContext;
        this.updatedNumber = paramString;
        this.oldContact = oldContact;
    }

    private void sendUpdates()
    {
        SQLiteDatabase localSQLiteDatabase = new CyncDBHelper(this.mContext).getReadableDatabase();
        Cursor localCursor = sRegisteredContacts.query(localSQLiteDatabase, new String[] { "IP" }, null, null, null, null, null);
        while (localCursor.moveToNext())
        {
            String IP = localCursor.getString(localCursor.getColumnIndex(CyncDBContract.ServerContactsEntry.COLUMN_IP));
            new CyncWebServiceClient(mContext, oldContact, updatedNumber, IP).init();
        }

        localCursor.close();
        localSQLiteDatabase.close();
    }

    protected Void doInBackground(Void... paramVarArgs)
    {
        sendUpdates();
        return null;
    }
}
