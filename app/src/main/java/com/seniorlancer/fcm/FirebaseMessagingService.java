package com.seniorlancer.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final String TAG = "FCM_LOG";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleMessage(remoteMessage);
    }



    private void handleMessage(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Log.i(TAG, "Message from: " + from);

        if(remoteMessage.getData().size() > 0) {
            if(true) {
                Map<String, String> info_data = remoteMessage.getData();
                String to = info_data.get("to");
                String notification = info_data.get("notification");
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getData());
            } else {
                Log.i(TAG, "abcedf");
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
