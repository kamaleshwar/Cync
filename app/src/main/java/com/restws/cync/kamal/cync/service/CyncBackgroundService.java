package com.restws.cync.kamal.cync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.restws.cync.kamal.cync.ContactSyncServer;

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Created by kamal on 18-04-2016.
 */
public class CyncBackgroundService extends Service{

    public CyncBackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        try {
            Component component = new Component();
            component.getServers().add(Protocol.HTTP, 8182);
            component.getDefaultHost().attach("/root",
                    new ContactSyncServer());
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

}
