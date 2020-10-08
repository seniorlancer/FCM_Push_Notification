package com.seniorlancer.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRetrieveToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRetrieveToken = findViewById(R.id.btn_retrieve_toke);
        btnRetrieveToken.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retrieve_toke:
                retrieveFirebaseToken();
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
                    Log.e("FCM", "Get to get token");
                    return;
                }

                String token = task.getResult().getToken();
                Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
            }
        });
    }
}