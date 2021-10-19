package com.yingke.demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yingke.demo.permission.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox mDesktopShow;
    private int mFloatNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View mShow = findViewById(R.id.tv_show);
        View mClose = findViewById(R.id.tv_close);
        mDesktopShow = findViewById(R.id.cb_desktop_show);

        PermissionUtils permissionUtils = PermissionUtils.newInstance();

        boolean hasPermission = permissionUtils.checkPermission(this);
        if (!hasPermission) {
            permissionUtils.applyPermission(this);
        }


        mDesktopShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<FloatViewController> viewControllerList = FloatX.get().getViewControllerList();
                if (viewControllerList != null) {
                    for (int i = 0; i < viewControllerList.size(); i++) {
                        FloatViewController viewController = viewControllerList.get(i);
                        FloatConfig floatBuilder = viewController.getFloatBuilder();
                        floatBuilder.setDesktopShow(isChecked);
                    }
                }

            }
        });

        findViewById(R.id.tv_open_show_float_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NeedShowActivity.launchActivity(MainActivity.this);
                    }
                });

        findViewById(R.id.tv_open_hint_float_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HintFloatActivity.launchActivity(MainActivity.this);
                    }
                });

        findViewById(R.id.tv_close_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CloseFloatActivity.launchActivity(MainActivity.this);
                    }
                });

        findViewById(R.id.tv_start_test)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TestMoveActivity.launchActivity(MainActivity.this);
                    }
                });
        mShow.setOnClickListener(this);
        mClose.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_show:

                List<Class<?>> notDisplayActivities = new ArrayList<>();
                notDisplayActivities.add(HintFloatActivity.class);

                List<Class<?>> closeActivities = new ArrayList<>();
                closeActivities.add(CloseFloatActivity.class);
                show("测试 " + mFloatNum, notDisplayActivities, closeActivities);
                mFloatNum++;
                break;
            case R.id.tv_close:
                List<FloatViewController> list = FloatX.get().getViewControllerList();
                if (list == null || list.size() <= 0) {
                    FloatToastUtils.show("当前还没有创建浮窗");
                    return;
                }
                CharSequence[] items = new CharSequence[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    items[i] = list.get(i).getFloatBuilder().getTag();
                }
                if (items.length == 1) {
                    String flag = (String) items[0];
                    FloatX.get().close(flag);
                    FloatViewController floatViewController = FloatX.get().getFloat(flag);
                    if (floatViewController == null) {
                        FloatToastUtils.show("删除 " + flag + " 成功！");
                    } else {
                        FloatToastUtils.show("删除 " + flag + " 失败。检查一下报错信息吧");

                    }
                    mFloatNum = 0;
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("选择对应的浮窗，进行删除")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CharSequence item = items[which];
                                String flag = item.toString();
                                FloatX.get().close(flag);
                                FloatViewController floatViewController = FloatX.get().getFloat(flag);
                                if (floatViewController == null) {
                                    FloatToastUtils.show("删除 " + flag + " 成功！");
                                } else {
                                    FloatToastUtils.show("删除 " + flag + " 失败。检查一下报错信息吧");
                                }
                                List<FloatViewController> floatList = FloatX.get().getViewControllerList();
                                if (floatList == null || floatList.size() == 0) {
                                    mFloatNum = 0;
                                }
                            }
                        }).show();
                break;

            default:
                break;
        }

    }

    private void show(String flag, List<Class<?>> notDisplayActivities, List<Class<?>> mCloseActivities) {
        View floatView = View.inflate(this.getApplicationContext(), R.layout.layout_float_view, null);
        floatView.setBackgroundColor(ContextCompat.getColor(this, R.color.black_333));
        TextView tvText = floatView.findViewById(R.id.tv_text);
        tvText.setText(flag);

        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatToastUtils.show("点击 " + System.currentTimeMillis());
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
                .setRawY(y)
                // 不需要展示的页面(进入这些页面不需要展示，但是退出后需要继续展示)
                .setNotDisplayActivities(notDisplayActivities)
                // 需要关闭的页面(一旦进入这些页面，就彻底销毁悬浮窗了。退出也不会展示，只有再次创建才可以)
                .setCloseActivities(mCloseActivities)
                // 【默认为不展示】是否需要在桌面也显示浮窗
                .setDesktopShow(mDesktopShow.isChecked());

        FloatX.get()
                .addFloat(flag, floatConfig)
                .show(flag);
    }

}