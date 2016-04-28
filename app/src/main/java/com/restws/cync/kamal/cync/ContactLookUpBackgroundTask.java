package com.restws.cync.kamal.cync;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by kamal on 27-04-2016.
 */
public class ContactLookUpBackgroundTask
        extends AsyncTask<Void, Void, Void> {

    private String term;
    private String endpoint;
    private Context mContext;

    public ContactLookUpBackgroundTask(Context paramContext, String term, String endpoint) {
        this.mContext = paramContext;
        this.endpoint = endpoint;
        this.term = term;
    }

    @Override
    protected Void doInBackground(Void... params) {
        searchQuery();
        return null;
    }

    private void searchQuery() {
        new CyncWebServiceClient(mContext, term, endpoint).searchContacts();
    }

}
