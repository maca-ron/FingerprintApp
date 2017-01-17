package com.example.bio.fingerprintapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFingerPrint(View view) {
        Log.i("", "debug_aaa");
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected() || fingerprintManager.hasEnrolledFingerprints()) {//指紋を取るハードウェアがあり、かつ、指紋が登録されていることをチェック。
            Log.i("", "debug_xxx");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("", "permission");
                return;
            }
            fingerprintManager.authenticate(null, null, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    Log.e("", "error " + errorCode + " " + errString);
                }

                @Override
                public void onAuthenticationFailed() {
                    Log.e("", "failed");
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    intent.putExtra("response", "");
                    MainActivity.this.startActivity(intent);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    Log.i("", "auth success");
                    try {
                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        intent.putExtra("response", "success");
                        MainActivity.this.startActivity(intent);
                    } catch (Exception ignored) {
                    }
                }
            }, new Handler());
        }
        Log.i("", "debug_zzz");
    }
}
