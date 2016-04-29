package com.restws.cync.kamal.cync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamal on 28-04-2016.
 */
public class ContactQueryStringLoader extends AsyncTaskLoader<List<String>> {

    List<String> mMap = new ArrayList<>();

    public ContactQueryStringLoader(Context context, List<String> map) {
        super(context);
        this.mMap = map;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public void deliverResult(List<String> data) {
        super.deliverResult(data);
    }

    @Override
    public List<String> loadInBackground() {
        return mMap;
    }
}
