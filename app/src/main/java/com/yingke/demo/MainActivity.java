package com.yingke.demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yingke.floatwindow.FloatConfig;
import com.yingke.floatwindow.FloatUtils;
import com.yingke.floatwindow.FloatViewController;
import com.yingke.floatwindow.FloatX;
import com.yingke.floatwindow.FloatXLog;
import com.yingke.floatwindow.SimpleTouchActionListener;
import com.yingke.floatwindow.TouchActionListener;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox mDesktopShow;
    private int mFloatNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View mShow = findViewById(R.id.tv_show);
        View mClose = findViewById(R.id.tv_close);
        View reqPer = findViewById(R.id.req_per);
        mDesktopShow = findViewById(R.id.cb_desktop_show);

        // 不要忽略这一步
        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return;
                }
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    return;
                }
                List<FloatViewController> viewControllerList = FloatX.get().getViewControllerList();
                if (FloatUtils.isEmpty(viewControllerList)) {
                    return;
                }
                for (int i = 0; i < viewControllerList.size(); i++) {
                    String tag = viewControllerList.get(i).getFloatBuilder().getTag();
                    FloatX.get().show(tag);
                }
            }
        });

        reqPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    intentActivityResultLauncher.launch(intent);
                }
            }
        });

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

        findViewById(R.id.tv_secondary_page_show_float)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SecondaryPageShowFloatActivity.launchActivity(MainActivity.this);
                    }
                });

        findViewById(R.id.tv_show_hint)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowHintActivity.launchActivity(MainActivity.this);
                    }
                });
    }



    private void show(String flag, List<Class<?>> notDisplayActivities, List<Class<?>> mCloseActivities) {
        // 业务中的浮窗 view 不要简单的 new 出来，尽可能自己放到一个单独的控制类中去。
        // flag 主要用于在多个浮窗时对单个唯一浮窗进行区别。建议自己创建一个常量类进行维护。
        FloatViewBinder floatViewBinder = new FloatViewBinder(this, flag);
        View floatView = floatViewBinder.getFloatView();
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatViewBinder.incNum();
            }
        });
        FloatConfig floatConfig = new FloatConfig(floatView, flag);

        // 配置项，可选
        {
            floatConfig
                    // 不需要展示的页面(进入这些页面不需要展示，但是退出后需要继续展示)
                    .setNotDisplayActivities(notDisplayActivities)
                    // 需要关闭的页面(一旦进入这些页面，就彻底销毁悬浮窗了。退出也不会展示，只有再次创建才可以)
                    .setCloseActivities(mCloseActivities)
                    // 【默认为不展示】是否需要在桌面也显示浮窗
                    .setDesktopShow(mDesktopShow.isChecked());

            // 松手时动画
            floatConfig.setTouchActionUpListener(new SimpleTouchActionListener() {
                @Override
                public void onTouchStart(@Nullable FloatConfig floatConfig, @NonNull MotionEvent event) {
                    FloatXLog.d("开始触摸");
                }

                @Override
                public void onTouchMove(@Nullable FloatConfig floatConfig, @NonNull MotionEvent event) {
                    FloatXLog.d("触摸中...");
                }

                @Override
                public boolean onTouchUp(@Nullable FloatViewController controller, MotionEvent event) {
                    /*
                        如果业务的松手时动画有自己的需求，就在这里返回 true。然后实现自己的动画就好。
                        下面是你自己实现动画可能需要的一些参数信息。

                        1. 当前浮窗位置：
                        int rawX = mViewController.getFloatBuilder().getRawX();
                        int rawY = mViewController.getFloatBuilder().getRawY();

                        2. controller.updateViewLocation(x,y); 改变 view 的位置
                     */

                    FloatXLog.d("松手啦~");
                    return false;
                }

                @Override
                public void onAnimatorEnd(@Nullable FloatConfig floatConfig) {
                    FloatXLog.d("松手后动画结束");
                }
            });
        }


        FloatX.get()
                .addFloat(floatConfig)
                .show(flag);
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
                show("浮窗 " + mFloatNum + " 号", notDisplayActivities, closeActivities);
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
}