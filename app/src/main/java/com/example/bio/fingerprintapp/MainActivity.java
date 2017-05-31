package com.example.bio.fingerprintapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.support.v4.app.ActivityCompat;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    Signature mSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFingerPrint(View view) {
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // 秘密鍵から署名オブジェクトを生成
        try {

            KeyStore mKeyStore;
            // 公開鍵を取得する
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            PrivateKey key = (PrivateKey) mKeyStore.getKey("FingerPrintKey", null);
            mSignature = Signature.getInstance("SHA256withECDSA");
            mSignature.initSign(key);
            CancellationSignal mCancellationSignal = new CancellationSignal();

            if (fingerprintManager.isHardwareDetected() || fingerprintManager.hasEnrolledFingerprints()) {//指紋を取るハードウェアがあり、かつ、指紋が登録されていることをチェック。
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
                Toast.makeText(this, "指紋認証してください。", Toast.LENGTH_LONG).show();
                fingerprintManager.authenticate(new FingerprintManager.CryptoObject(mSignature), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
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
                        FingerprintManager.CryptoObject object = result.getCryptoObject();
                        byte[] signBytes = null;
                        if(object != null){
                            try {
                                Signature signature = object.getSignature();
                                signature.update("finger_print_token".getBytes());
                                signBytes = signature.sign();
                                // サーバーサイドに送り、サーバーサイドで検証する
                            } catch (SignatureException e) {
                                // error handling
                            }
                        }
                        try {
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            intent.putExtra("response", new String(signBytes));
                            MainActivity.this.startActivity(intent);
                        } catch (Exception ignored) {
                        }
                    }
                }, new Handler());
            }
        } catch (KeyPermanentlyInvalidatedException e) {
            // error handling
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            // error handling
        }
    }

    public void onClickRegister(View view) {
        try {
            Intent intent = new Intent(MainActivity.this, FingerPrintActivity.class);
            MainActivity.this.startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
