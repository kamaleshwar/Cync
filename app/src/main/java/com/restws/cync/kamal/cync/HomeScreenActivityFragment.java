package com.restws.cync.kamal.cync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.restws.cync.kamal.cync.data.CyncDBContract;
import com.restws.cync.kamal.cync.data.CyncDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeScreenActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.listview_Contacts)
    ListView mContactsListView;

    private ContactAdapter mContactAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int CONTACT_LOADER = 81000;
    private static final SQLiteQueryBuilder sRegisteredContacts = new SQLiteQueryBuilder();
    private final String BroadCastIntentId = "com.restws.cync.kamal.cync.fragmentupdater";
    private static final String USER_DECLINE = "reject";

    static boolean waitForResponse = true;
    static Context staticContext;
    static final int COL_CONTACT_NAME = 1;
    static final int COL_CONTACT_NUMBER = 2;
    static Activity mActivity;

    static {
        sRegisteredContacts.setTables(CyncDBContract.ServerContactsEntry.TABLE_NAME + " LEFT JOIN " +
                CyncDBContract.ContactsEntry.TABLE_NAME + " ON " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + CyncDBContract.ContactsEntry.COLUMN_NUMBER);
    }


    public LoaderManager.LoaderCallbacks<Cursor> loaderContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        HomeScreenActivityFragment.staticContext = getContext();
        HomeScreenActivityFragment.mActivity = getActivity();
        loaderContext = this;
        getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
        getActivity().registerReceiver(FragmentReceiver, new IntentFilter(BroadCastIntentId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(FragmentReceiver);
    }

    private final BroadcastReceiver FragmentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().getSupportLoaderManager().restartLoader(CONTACT_LOADER, null, loaderContext);
        }
    };

    public static void updatePhoneBook(String name, String number) {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        staticContext.startActivity(intent);
    }

    public static String updateContact(String old, String updated) {
        CyncDBHelper dbHelper = new CyncDBHelper(staticContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor contactNameCursor = db.rawQuery("select " + CyncDBContract.ContactsEntry.COLUMN_NAME + " FROM " +
                CyncDBContract.ContactsEntry.TABLE_NAME + " WHERE " + CyncDBContract.ContactsEntry.COLUMN_NUMBER + " = " +
                old, null);
        int columnNameIndex = contactNameCursor.getColumnIndex(CyncDBContract.ContactsEntry.COLUMN_NAME);
        String contactName = contactNameCursor.moveToFirst() ? contactNameCursor.getString(columnNameIndex) : "";

        Cursor serverContactUpdateCursor = db.rawQuery("delete from " + CyncDBContract.ServerContactsEntry.TABLE_NAME + " WHERE " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + old, null);

        contactNameCursor.close();
        serverContactUpdateCursor.moveToFirst();
        serverContactUpdateCursor.close();
        db.close();
        dbHelper.close();
        return contactName;
    }


    public static String trySearch(String term, boolean opnionTaken) {
        final String searchTerm = term.split(":")[0];
        final String contactName = term.split(":")[1];

        if (!opnionTaken) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertUser(searchTerm, contactName);
                }
            });
        } else {
            if (ContactSyncWebService.taken) {
                return searchContacts(contactName, searchTerm);
            } else {
                return USER_DECLINE;
            }
        }
        return USER_DECLINE;
    }

    public static String searchContacts(String name, String term) {
        CyncDBHelper dbHelper = new CyncDBHelper(staticContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JSONArray contactsArray = new JSONArray();
        String contactsStringObject = "";

        Cursor contactNameCursor = db.rawQuery("select " + CyncDBContract.ContactsEntry.COLUMN_NAME + " , " +
                CyncDBContract.ContactsEntry.COLUMN_NUMBER + " FROM " +
                CyncDBContract.ContactsEntry.TABLE_NAME + " WHERE " +
                CyncDBContract.ContactsEntry.COLUMN_NAME + " LIKE " +
                "'%" + term + "%'", null);

        int columnNameIndex = contactNameCursor.getColumnIndex(CyncDBContract.ContactsEntry.COLUMN_NAME);
        int columnNumberIndex = contactNameCursor.getColumnIndex(CyncDBContract.ContactsEntry.COLUMN_NUMBER);

        try {

            while (contactNameCursor.moveToNext()) {
                JSONObject contact = new JSONObject();
                String contactName = contactNameCursor.getString(columnNameIndex);
                String contactNumber = contactNameCursor.getString(columnNumberIndex);
                contact.put(contactName, contactNumber);
                contactsArray.put(contact);
            }
            contactsStringObject = contactsArray.toString();

        } catch (JSONException e) {
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        } finally {
            contactNameCursor.close();
            db.close();
            dbHelper.close();
        }

        return contactsStringObject;
    }

    public static void alertUser(final String term, final String name) {
        new AlertDialog.Builder(staticContext)
                .setTitle("Contact lookup Request")
                .setMessage("Allow " + name + " to search your contacts for keyword " + term + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HomeScreenActivityFragment.waitForResponse = false;
                        ContactSyncWebService.taken = true;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HomeScreenActivityFragment.waitForResponse = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_screen, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String sortOrder = CyncDBContract.ContactsEntry.COLUMN_NAME + " ASC";
        CyncDBHelper dbHelper = new CyncDBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final Cursor cur = sRegisteredContacts.query(db,
                new String[]
                        {CyncDBContract.ServerContactsEntry.TABLE_NAME + "." +
                                CyncDBContract.ServerContactsEntry._ID,
                                CyncDBContract.ContactsEntry.COLUMN_NAME,
                                CyncDBContract.ContactsEntry.COLUMN_NUMBER,
                                CyncDBContract.ServerContactsEntry.COLUMN_IP},
                null,
                null,
                null,
                null,
                sortOrder);

        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        mContactsListView = ButterKnife.findById(rootView, R.id.listview_Contacts);

        mContactAdapter = new ContactAdapter(getActivity(), cur, 0);
        mContactsListView.setAdapter(mContactAdapter);
        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            final String contactNameKey = "contactName_Key";
            final String contactNumberKey = "contactNumber_Key";
            final String contactIpKey = "ContactIP_Key";

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                String contactName = cursor.getString(cursor.
                        getColumnIndex(CyncDBContract.ContactsEntry.COLUMN_NAME));
                String contactNumber = cursor.getString(cursor.
                        getColumnIndex(CyncDBContract.ContactsEntry.COLUMN_NUMBER));
                String contactIP = cursor.getString(cursor.
                        getColumnIndex(CyncDBContract.ServerContactsEntry.COLUMN_IP));

                Intent detailIntent = new Intent(getContext(), DetailsActivity.class);
                detailIntent.putExtra(contactNameKey, contactName);
                detailIntent.putExtra(contactNumberKey, contactNumber);
                detailIntent.putExtra(contactIpKey, contactIP);
                startActivity(detailIntent);
                mPosition = position;
                cursor.close();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        cur.close();
        db.close();
        dbHelper.close();
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ContactCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mPosition != ListView.INVALID_POSITION) {
            mContactsListView.smoothScrollToPosition(mPosition);
        }
        mContactAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
