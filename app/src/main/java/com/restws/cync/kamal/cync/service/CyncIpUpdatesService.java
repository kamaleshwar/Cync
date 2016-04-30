package com.restws.cync.kamal.cync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.restws.cync.kamal.cync.FetchLocalContactList;

/**
 * Created by kamal on 29-04-2016.
 */
public class CyncIpUpdatesService extends Service {

    public CyncIpUpdatesService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(3000);
                        FetchLocalContactList localContactList = new FetchLocalContactList(getBaseContext());
                        localContactList.execute();
                    }
                } catch (InterruptedException e) {
                }
            }

        }).start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
