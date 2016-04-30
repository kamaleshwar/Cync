package com.restws.cync.kamal.cync;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by kamal on 27-04-2016.
 */
public class ContactLookUpBackgroundTask
        extends AsyncTask<Void, Void, Void> {

    private String mContactName;
    private String term;
    private String endpoint;
    private Context mContext;


    public ContactLookUpBackgroundTask(Context paramContext, String name, String term, String endpoint) {
        this.mContext = paramContext;
        this.endpoint = endpoint;
        this.term = term;
        this.mContactName = name;
    }

    @Override
    protected Void doInBackground(Void... params) {
        searchQuery();
        return null;
    }

    private void searchQuery() {
        new CyncWebServiceClient(mContactName, term, endpoint, mContext).searchContacts();
    }

}
