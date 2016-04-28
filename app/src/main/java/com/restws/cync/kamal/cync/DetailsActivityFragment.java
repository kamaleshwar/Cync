package com.restws.cync.kamal.cync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    static Context mContext;
    @Bind(R.id.query_item_contact_name)
    TextView mQueryContactName;
    @Bind(R.id.query_item_contact_number)
    TextView getmQueryContactNumber;

    public DetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailsActivityFragment.mContext = getActivity().getApplicationContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    public static void showQueryOutput(String resultString) {
        try {
            JSONArray contactsArray = new JSONArray(resultString);

            for (int i = 0; i < contactsArray.length(); i++) {
                Log.v("checkhere", contactsArray.getJSONObject(i).toString());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
