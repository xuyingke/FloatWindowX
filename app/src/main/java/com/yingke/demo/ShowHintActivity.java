package com.yingke.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class ShowHintActivity extends AppCompatActivity {

    String flag = "ShowHint";

    public static void launchActivity(@NonNull Context context) {
        Intent intent = new Intent(context, ShowHintActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_hint);
        findViewById(R.id.tv_show)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FloatX.get().getFloat(flag) != null) {
                            FloatX.get().getFloat(flag).show();
                        }

                        View floatView = View.inflate(ShowHintActivity.this, R.layout.layout_float_view, null);
                        TextView tvText = floatView.findViewById(R.id.tv_text);
                        tvText.setText(flag);

                        floatView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int random = new Random().nextInt(100);
                                tvText.setText(flag + random);
                            }
                        });


                        int x = (int) (Utils.getWidth() * 0.1F);
                        int y = (int) (Utils.getHeight() * 0.1F);

                        FloatConfig floatConfig = new FloatConfig(floatView);
                        floatConfig.setTag(flag)
                                // 【必须】浮窗的宽
                                .setFloatViewWidth(Utils.dp2px(100))
                                // 【必须】浮窗的高
                                .setFloatViewHeight(Utils.dp2px(100))
                                // 【必须】相对屏幕的横坐标
                                .setRawX(x)
                                // 【必须】相对屏幕的纵坐标
                                .setRawY(y);


                        FloatX.get()
                                .addFloat(flag, floatConfig)
                                .show(flag);
                    }
                });

        findViewById(R.id.tv_hint)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FloatX.get().getFloat(flag)
                                .hidden();
                    }
                });
    }
}