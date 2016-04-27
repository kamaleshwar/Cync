package com.restws.cync.kamal.cync.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by kamal on 06-04-2016.
 */
public class AccountAuthenticationService extends Service {

    AccountAuthenticator mAccountAuth;

    @Override
    public void onCreate() {
        mAccountAuth = new AccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAccountAuth.getIBinder();
    }
}
