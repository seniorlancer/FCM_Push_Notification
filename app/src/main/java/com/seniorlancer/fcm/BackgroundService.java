package com.seniorlancer.fcm;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public boolean isRestricted() {
        return super.isRestricted();
    }
}
