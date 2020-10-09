package com.seniorlancer.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
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
    private final String TAG = "FCM_LOG";
    public static final int MY_BACKGROUND_JOB = 0;

    Button btnRetrieveToken;
    EditText textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRetrieveToken = findViewById(R.id.btn_retrieve_toke);
        textView = findViewById(R.id.txt_token);

        btnRetrieveToken.setOnClickListener(this);
//        scheduleJob(this);
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
                    Log.w(TAG, "Device doesn't have google play services");
                }
                break;
            default:
                break;
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
                Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
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
}