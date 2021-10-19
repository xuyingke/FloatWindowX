package com.yingke.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HintFloatActivity extends AppCompatActivity {
    public static void launchActivity(@NonNull Context context) {
        Intent intent = new Intent(context, HintFloatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint_float);
    }
}