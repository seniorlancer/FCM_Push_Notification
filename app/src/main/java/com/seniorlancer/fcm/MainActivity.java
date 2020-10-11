package com.seniorlancer.fcm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static MainActivity runingActivity = null;

    private final String TAG = "FCM_LOG";
    public static final int MY_BACKGROUND_JOB = 0;
    public static final int RESULT_RESTRICTION_BACKGROUND = 100;

    Button btnRetrieveToken;
    EditText textView;
    EditText textMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRetrieveToken = findViewById(R.id.btn_retrieve_toke);
        textView = findViewById(R.id.txt_token);
        textMessage = findViewById(R.id.txt_message);

        textMessage.setText("");

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if(getIntent().getExtras().get("message") != null) {
                    String value = getIntent().getExtras().get("message").toString();
                    Log.d(TAG, "Key: " + key + " Value: " + value);
                    textMessage.setText(value);
                } else {
                    textMessage.setText("");
                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        btnRetrieveToken.setOnClickListener(this);
        checkBackgroundRestriction();
        runtimeEnableAutoInit();

//        scheduleJob(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        runingActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        runingActivity = null;
    }

    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    private void checkBackgroundRestriction() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            boolean check = activityManager.isBackgroundRestricted();
            if(!check) {
                setRestrictionBackground();
            }
            Log.d("TEST", "onCreate: activityManager.isBackgroundRestricted() = " + check);
        }
    }

    private void setRestrictionBackground() {
        AlertDialog.Builder alertRestriction = new AlertDialog.Builder(this);
        alertRestriction.setMessage(getResources().getString(R.string.set_background_restriction));
        alertRestriction.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(intent, RESULT_RESTRICTION_BACKGROUND);
            }
        });
        alertRestriction.setCancelable(false);
        AlertDialog alertDialog = alertRestriction.create();
        alertDialog.show();
    }

    public void runTimeEnableAuto() {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public static void scheduleJob(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo job = new JobInfo.Builder(
                    MY_BACKGROUND_JOB,
                    new ComponentName(context, MainActivity.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setRequiresCharging(true)
                    .build();
            js.schedule(job);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retrieve_toke:
                if(checkGooglePlayServices()) {
                    retrieveFirebaseToken();
                } else {
                    Toast.makeText(getApplicationContext(), "Device doesn't have google play services", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Device doesn't have google play services");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_RESTRICTION_BACKGROUND) {
            checkBackgroundRestriction();
        }
    }

    void retrieveFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()) {
                    Log.e(TAG, "Get to get token");
                    return;
                }

                String token = task.getResult().getToken();
                textView.setText(token);
            }
        });
    }

    private boolean checkGooglePlayServices() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error to be available to google api");
            return false;
        } else {
            Log.i(TAG, "Google play services updated");
            return true;
        }
    }

    public void setMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textMessage.setText(message);
            }
        });
    }
}