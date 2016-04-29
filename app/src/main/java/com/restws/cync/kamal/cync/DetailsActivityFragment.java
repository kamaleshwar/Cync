package com.restws.cync.kamal.cync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    static ArrayAdapter adp;
    static ListView mQueryListView;
    static LoaderManager dummyActivity;

    private int mPosition = ListView.INVALID_POSITION;
    private static final int CONTACT_QUERY_LOADER = 81001;
    public static LoaderManager.LoaderCallbacks<List<String>> loaderContext;

    public DetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailsActivityFragment.mContext = getActivity().getApplicationContext();
        loaderContext = this;
        dummyActivity = getActivity().getSupportLoaderManager();
        queryList = new ArrayList<>();
        adp = new ArrayAdapter(mContext, R.layout.query_list_view_elem_details, new ArrayList());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        getActivity().getSupportLoaderManager().initLoader(CONTACT_QUERY_LOADER, null, this);
        mQueryListView = (ListView) rootView.findViewById(R.id.query_list_view);
        mQueryListView.setAdapter(adp);
        adp.notifyDataSetChanged();
        return rootView;
    }

    public static void showQueryOutput(String resultString) {
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
        adp.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        adp.clear();
    }

}
