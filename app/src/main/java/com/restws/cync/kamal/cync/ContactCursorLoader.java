package com.restws.cync.kamal.cync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.content.AsyncTaskLoader;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

/**
 * Created by kamal on 25-04-2016.
 */
public class ContactCursorLoader extends AsyncTaskLoader<Cursor> {
    public ContactCursorLoader(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }

    @Override
    public Cursor loadInBackground() {

        final SQLiteQueryBuilder sRegisteredContacts = new SQLiteQueryBuilder();

        sRegisteredContacts.setTables(CyncDBContract.ServerContactsEntry.TABLE_NAME + " LEFT JOIN " +
                CyncDBContract.ContactsEntry.TABLE_NAME + " ON " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + CyncDBContract.ContactsEntry.COLUMN_NUMBER);

        String sortOrder = CyncDBContract.ContactsEntry.COLUMN_NAME + " ASC";

        CyncDBHelper dbHelper = new CyncDBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor registeredCursor = sRegisteredContacts.query(db,
                new String[] {
                        CyncDBContract.ServerContactsEntry.TABLE_NAME + "." +
                        CyncDBContract.ServerContactsEntry._ID,
                        CyncDBContract.ContactsEntry.COLUMN_NAME,
                        CyncDBContract.ContactsEntry.COLUMN_NUMBER},
                        null,
                        null,
                        null,
                        null,
                        sortOrder);

        return registeredCursor;

    }
}
