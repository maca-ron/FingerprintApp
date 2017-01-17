package com.example.bio.fingerprintapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        String response = getIntent().getStringExtra("response");
        TextView userText = (TextView) findViewById(R.id.user);
        if ("".equals(response)) {
            // 認証失敗
            userText.setText("failer");
        } else {
            // 認証成功
            userText.setText(response);
        }
    }

    public void onClickReLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
