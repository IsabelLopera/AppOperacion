package com.example.appoperaciones.Clases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MyPackageReplacedReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageReplacedReceiver";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            if(mAuth.getCurrentUser() != null){
                mAuth.signOut();
            }
            Log.i(TAG,"SE REINSTALO LA APP");
        }

    }
}