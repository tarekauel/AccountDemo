package de.orangecode.accountdemo.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DemoAccountAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        DemoAccountAuthenticator accountAuthenticator = new DemoAccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }
}


