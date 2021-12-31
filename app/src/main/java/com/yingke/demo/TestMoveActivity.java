package com.yingke.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingke.floatwindow.FloatViewController;
import com.yingke.floatwindow.FloatX;

import java.util.List;

public class TestMoveActivity extends AppCompatActivity {

    public static void launchActivity(@NonNull Context context) {
        Intent intent = new Intent(context, TestMoveActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_move);

        ((TextView) findViewById(R.id.tv_width)).setText(String.valueOf(Utils.getWidth()));
        ((TextView) findViewById(R.id.tv_height)).setText(String.valueOf(Utils.getHeight()));

        EditText editTextX = findViewById(R.id.et_x);
        EditText editTextY = findViewById(R.id.et_y);

        findViewById(R.id.btn_move)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            float x = Float.parseFloat(editTextX.getText().toString());
                            float y = Float.parseFloat(editTextY.getText().toString());

                            List<FloatViewController> viewControllerList = FloatX.get().getViewControllerList();
                            viewControllerList.get(0).updateViewLocation(x, y);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}