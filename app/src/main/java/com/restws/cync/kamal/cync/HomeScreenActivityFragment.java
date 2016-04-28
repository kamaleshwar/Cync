package com.restws.cync.kamal.cync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

    private static final int CONTACT_LOADER = 81215;

    static Context staticContext;

    private static final SQLiteQueryBuilder sRegisteredContacts = new SQLiteQueryBuilder();


    static final int COL_CONTACT_NAME = 1;
    static final int COL_CONTACT_NUMBER = 2;


    static {
        sRegisteredContacts.setTables(CyncDBContract.ServerContactsEntry.TABLE_NAME + " LEFT JOIN " +
                CyncDBContract.ContactsEntry.TABLE_NAME + " ON " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + CyncDBContract.ContactsEntry.COLUMN_NUMBER);
    }


    public LoaderManager.LoaderCallbacks<Cursor> loaderContext;

    public HomeScreenActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        HomeScreenActivityFragment.staticContext = getContext();
        loaderContext = this;
        getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
        getActivity().registerReceiver(FragmentReceiver, new IntentFilter("com.restws.cync.kamal.cync.fragmentupdater"));
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

        db.rawQuery("update " + CyncDBContract.ContactsEntry.TABLE_NAME + " SET " +
                CyncDBContract.ContactsEntry.COLUMN_NUMBER + " = " + updated + " WHERE " +
                CyncDBContract.ContactsEntry.COLUMN_NUMBER + " = " + old, null);

        db.rawQuery("update " + CyncDBContract.ServerContactsEntry.TABLE_NAME + " SET " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + updated + " WHERE " +
                CyncDBContract.ServerContactsEntry.COLUMN_NUMBER + " = " + old, null);

        db.rawQuery("update " + CyncDBContract.CurrentContactEntry.TABLE_NAME + " SET " +
                CyncDBContract.CurrentContactEntry.COLUMN_CURRENT_CONTACT + " = " + updated + " WHERE " +
                CyncDBContract.CurrentContactEntry.COLUMN_CURRENT_CONTACT + " = " + old, null);

        return contactName;
    }


    public static String searchContacts(String term) {
        CyncDBHelper dbHelper = new CyncDBHelper(staticContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JSONArray contactsArray = new JSONArray();
        JSONObject contactsObject = new JSONObject();
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
            Log.e("JSONException", e.getMessage(), e);
        }

        return contactsStringObject;
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);

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
                for (int i = 0; i < cursor.getColumnNames().length; i++) {
                    Log.v("columnnmaes", cursor.getColumnNames()[i].toString());
                }

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
            }
        });


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
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
