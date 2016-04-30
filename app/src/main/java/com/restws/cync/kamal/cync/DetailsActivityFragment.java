package com.restws.cync.kamal.cync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<String>> {

    static Context mContext;
    static ArrayList<String> queryList;
    static QueryContactAdapter arrayAdapter;
    static ListView mQueryListView;
    static LoaderManager dummyActivity;

    private static String USER_DECLINE = "reject";
    private static final String SELECTED_KEY = "selected_position";
    private int mPosition = ListView.INVALID_POSITION;
    private static final int CONTACT_QUERY_LOADER = 81001;
    public static LoaderManager.LoaderCallbacks<List<String>> loaderContext;
    public static boolean userDenied = false;


    public DetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailsActivityFragment.mContext = getActivity();
        loaderContext = this;
        dummyActivity = getActivity().getSupportLoaderManager();
        queryList = new ArrayList<>();
        arrayAdapter = new QueryContactAdapter(mContext, R.layout.query_list_view_elem_details, new ArrayList());
        userDenied = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        getActivity().getSupportLoaderManager().initLoader(CONTACT_QUERY_LOADER, null, this);
        mQueryListView = (ListView) rootView.findViewById(R.id.query_list_view);
        mQueryListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        return rootView;
    }

    public static void showQueryOutput(String resultString) {

        if (resultString.equals(USER_DECLINE)) {
            queryList.clear();
            userDenied = true;
            populateListView();
            return;
        }

        try {
            JSONArray contactsArray = new JSONArray(resultString);
            queryList = new ArrayList<>();
            for (int i = 0; i < contactsArray.length(); i++) {
                JSONObject contact = contactsArray.getJSONObject(i);
                Iterator keys = contact.keys();
                String name = keys.next().toString();
                String number = contact.getString(name);
                String res = name + ":" + number;
                queryList.add(res);
            }
            populateListView();
        } catch (JSONException e1) {
            Log.e(e1.getLocalizedMessage(), e1.getMessage(), e1);
        }
    }

    private static void populateListView() {
        dummyActivity.restartLoader(CONTACT_QUERY_LOADER, null, loaderContext);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new ContactQueryStringLoader(getActivity(), queryList);
    }


    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        if (mPosition != ListView.INVALID_POSITION) {
            mQueryListView.smoothScrollToPosition(mPosition);
        }
        if (data.isEmpty() && userDenied) {
            Toast.makeText(getActivity(), "User Denied", Toast.LENGTH_SHORT).show();
        } else {

            arrayAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        arrayAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    public static class QueryContactAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private List<String> mData;

        public QueryContactAdapter(Context context, int resource, List<String> data) {
            super(context, resource, data);
            this.mContext = context;
            this.mData = data;
        }

        public static class ViewHolder {
            public TextView contactName;
            public TextView contactNumber;
            public Button query_call;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder v_holder;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.query_list_view_elem_details, parent, false);

                v_holder = new ViewHolder();
                v_holder.contactName = (TextView) convertView.findViewById(R.id.q_contact_name_text_view);
                v_holder.contactNumber = (TextView) convertView.findViewById(R.id.q_contact_number_text_view);
                v_holder.query_call = (Button) convertView.findViewById(R.id.query_list_item_call);

                convertView.setTag(v_holder);
            } else {
                v_holder = (ViewHolder) convertView.getTag();
            }
            String contact = mData.get(position);
            String name = contact.split(":")[0];
            String number = contact.split(":")[1];
            v_holder.contactName.setText(name);
            v_holder.contactNumber.setText(number);

            v_holder.query_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = v_holder.contactNumber.getText().toString();
                    String absNumber = phoneNumber.substring((phoneNumber.length() - 10), phoneNumber.length());
                    String callUri = "tel:" + absNumber.trim();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.setData(Uri.parse(callUri));
                    mContext.getApplicationContext().startActivity(callIntent);
                }
            });

            return convertView;
        }

        @Override
        public void clear() {
            mData.clear();
        }
    }
}

