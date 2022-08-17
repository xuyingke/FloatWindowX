package com.yingke.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 核心类。
 * 多数方法不再处理多线程的情况。多线程的环境下注意调用安全。
 */
public class FloatX {

    private boolean mDebug;

    private ConcurrentHashMap<String, FloatViewController> mViewControllerList;

    private Application mContext;

    private List<OnVisibilityListener> mVisibilityListeners = new ArrayList<>();

    private static final class MInstanceHolder {
        static final FloatX mInstance = new FloatX();
    }

    public static FloatX get() {
        return MInstanceHolder.mInstance;
    }

    /**
     * 初始化方法是为了对悬浮窗 View 的容器、View 的控制器、LifecycleCallbacks 初始化
     * 如果不想在 Application 初始化，或者不想要另外注册 LifecycleCallbacks，就可以使用  initSimple
     * 进行最基础的初始化。但是这么做的话，关于悬浮窗在某个页面展示/隐藏、 App 后台不展示悬浮窗就需要你自己在自己的项目中的 LifecycleCallbacks
     * 对应时机通过 getFloat 方法获取悬浮窗控制器，来调用对应的方法。详细调用规则请看 FloatActivityLifecycleCallbacks
     */
    public void init(@NonNull Application context) {
        initSimple(context);
        ((Application) context.getApplicationContext())
                .registerActivityLifecycleCallbacks(new FloatActivityLifecycleCallbacks());
    }

    /**
     * 与 init 的区别是不再注册自己的 LifecycleCallbacks
     */
    public void initSimple(@NonNull Application context) {
        mContext = context;
        mViewControllerList = new ConcurrentHashMap<String, FloatViewController>();
    }

    /**
     * 提供给不想在 application 中做初始化代码的的强迫症
     * 需要在第一次使用浮窗前调用
     */
    public void tryInitRear(@NonNull Application context) {
        init(context);
    }

    /**
     * 和 tryInitRear 差别是 lifecycle 由外部管理
     */
    public void tryInitSimpleRear(@NonNull Application context) {
        initSimple(context);
    }

    private FloatX() {
        // empty
    }

    public FloatX addFloat(@NonNull FloatConfig floatConfig) {
        if (mViewControllerList == null) {
            return this;
        }
        checkConfig(floatConfig);
        FloatViewController viewController = mViewControllerList.get(floatConfig.getTag());
        if (viewController == null) {
            FloatViewController controller = new FloatViewController(mContext);
            controller.setFloatBuilder(floatConfig);
            mViewControllerList.put(floatConfig.getTag(), controller);
        }
        return this;
    }


    public void show(@NonNull String floatFlag) {
        if (mViewControllerList == null) {
            FloatXLog.w("得初始化啊，不初始化没法干活鸭~");
            return;
        }
        FloatViewController viewController = mViewControllerList.get(floatFlag);
        if (viewController != null) {
            viewController.show();
        }
    }

    public void close(@NonNull String floatFlag) {
        if (mViewControllerList == null) {
            // 需要调用 init
            return;
        }
        FloatViewController viewController = mViewControllerList.get(floatFlag);
        if (viewController != null) {
            viewController.close();
        }
        mViewControllerList.remove(floatFlag);
    }


    public void hidden(@NonNull String floatFlag) {
        if (mViewControllerList == null) {
            // 需要调用 init
            return;
        }
        FloatViewController viewController = mViewControllerList.get(floatFlag);
        if (viewController != null) {
            viewController.hidden();
        }
    }

    private void checkConfig(@NonNull FloatConfig floatConfig) {
        if (floatConfig.getFloatViewHeight() == 0 || floatConfig.getFloatViewWidth() == 0) {
            View floatView = floatConfig.getFloatView();
            floatView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            floatConfig.setFloatViewWidth(floatView.getMeasuredWidth());
            floatConfig.setFloatViewHeight(floatView.getMeasuredHeight());
        }
        if (floatConfig.getRawX() == 0) {
            // 默认右下角，屏幕高 70% 贴右边。
            int width = FloatUtils.getWidth(floatConfig.getFloatView().getContext());
            floatConfig.setRawX((int) (width - floatConfig.getFloatViewWidth()));
        }
        if (floatConfig.getRawY() == 0) {
            // 默认右下角，屏幕高 70% 贴右边。
            int height = FloatUtils.getHeight(floatConfig.getFloatView().getContext());
            floatConfig.setRawY((int) (height * 0.7F));
        }
    }


    public FloatViewController getFloat(@NonNull String floatFlag) {
        if (mViewControllerList == null) {
            return null;
        }
        return mViewControllerList.get(floatFlag);
    }

    @Nullable
    public List<FloatViewController> getViewControllerList() {
        if (mViewControllerList == null) {
            return null;
        }
        List<FloatViewController> list = new ArrayList<>();
        for (Map.Entry<String, FloatViewController> entry : mViewControllerList.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    public ConcurrentHashMap<String, FloatViewController> getViewControllerMap() {
        return mViewControllerList;
    }

    /**
     * 遍历当前的浮窗。回调 flag 和具体的浮窗实体
     *
     * @param callBack callBack
     */
    private void traverseFloatMap(@NonNull ViewControllerCallBack callBack) {
        synchronized (this) {
            ConcurrentHashMap<String, FloatViewController> viewControllerList = getViewControllerMap();
            Set<Map.Entry<String, FloatViewController>> entries = viewControllerList.entrySet();
            for (Map.Entry<String, FloatViewController> viewControllerEntry : entries) {
                String floatFlag = viewControllerEntry.getKey();
                FloatViewController viewController = viewControllerEntry.getValue();
                if (floatFlag == null || viewController == null) {
                    continue;
                }
                callBack.call(floatFlag, viewController);
            }
        }
    }

    public void setDebugEnabled(boolean mDebug) {
        this.mDebug = mDebug;
    }


    public boolean isDebugEnabled() {
        return mDebug;
    }

    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        traverseFloatMap(new ViewControllerCallBack() {
            @Override
            public void call(@NonNull String floatFlag, @NonNull FloatViewController viewController) {
                FloatConfig config = viewController.getFloatBuilder();
                if (config == null) {
                    return;
                }
                List<Class<?>> closeActivities = config.getCloseActivities();
                if (FloatUtils.isEmpty(closeActivities)) {
                    return;
                }
                try {
                    for (int i = 0; i < closeActivities.size(); i++) {
                        Class<?> aClass = closeActivities.get(i);
                        if (aClass == null) {
                            continue;
                        }
                        if (aClass.isInstance(activity)) {
                            viewController.close();
                            mViewControllerList.remove(floatFlag);
                            return;
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    public void onActivityResumed(int activityAmount, @NonNull Activity activity) {
        if (activityAmount == 1) {
            desktopBack(activity);
        } else {
            traverseFloatMap(new ViewControllerCallBack() {
                @Override
                public void call(@NonNull String floatFlag, @NonNull FloatViewController viewController) {
                    FloatConfig config = viewController.getFloatBuilder();
                    if (config == null) {
                        return;
                    }
                    boolean needHidden = config.isNeedHidden(activity);
                    if (needHidden) {
                        viewController.hidden();
                    }
                }
            });
        }
    }


    /**
     * 有 activity 要看不到了、退到后台了
     *
     * @param activity activity
     */
    public void onActivityStopped(int activityAmount, @NonNull Activity activity) {
        if (activityAmount == 0) {
            desktop();
        } else {
            traverseFloatMap(new ViewControllerCallBack() {
                @Override
                public void call(@NonNull String floatFlag, @NonNull FloatViewController viewController) {
                    FloatConfig floatBuilder = viewController.getFloatBuilder();
                    if (floatBuilder == null) {
                        return;
                    }
                    boolean needClose = floatBuilder.isNeedClose(activity);
                    boolean needHidden = floatBuilder.isNeedHidden(activity);
                    if (needClose) {
                        viewController.close();
                        mViewControllerList.remove(floatFlag);
                        return;
                    }

                    try {
                        for (int i = 0; i < mVisibilityListeners.size(); i++) {
                            OnVisibilityListener onVisibilityListener = mVisibilityListeners.get(i);
                            if (onVisibilityListener == null) {
                                continue;
                            }
                            boolean allowShow = onVisibilityListener.isShow(viewController);
                            if (!allowShow) {
                                needHidden = false;
                                break;
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                    if (needHidden) {
                        viewController.show();
                    }
                }
            });
        }
    }


    /**
     * 回到桌面
     */
    public void desktop() {
        traverseFloatMap(new ViewControllerCallBack() {
            @Override
            public void call(@NonNull String floatFlag, @NonNull FloatViewController viewController) {
                FloatConfig config = viewController.getFloatBuilder();
                if (config == null) {
                    return;
                }
                boolean desktopShow = config.isDesktopShow();
                if (!desktopShow) {
                    viewController.hidden();
                }
            }
        });
    }


    /**
     * 从桌面回来
     *
     * @param activity 回来后第一个看到的页面
     */
    public void desktopBack(Activity activity) {
        traverseFloatMap(new ViewControllerCallBack() {
            @Override
            public void call(@NonNull String floatFlag, @NonNull FloatViewController viewController) {
                FloatConfig config = viewController.getFloatBuilder();
                if (config == null) {
                    return;
                }
                boolean desktopShow = config.isDesktopShow();
                // 如果从桌面回来第一个展示的 activity 是需要隐藏的，那就不能展示出来浮窗
                boolean needHidden = config.isNeedHidden(activity);

                boolean show = false;
                if (!desktopShow && !needHidden) {
                    show = true;
                }
                try {
                    for (int i = 0; i < mVisibilityListeners.size(); i++) {
                        OnVisibilityListener onVisibilityListener = mVisibilityListeners.get(i);
                        if (onVisibilityListener == null) {
                            continue;
                        }
                        boolean allowShow = onVisibilityListener.isShow(viewController);
                        if (!allowShow) {
                            show = false;
                            break;
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }

                if (show) {
                    viewController.show();
                }
            }
        });
    }

    public void addVisibilityListeners(OnVisibilityListener listener) {
        try {
            mVisibilityListeners.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeVisibilityListeners(OnVisibilityListener listener) {
        try {
            mVisibilityListeners.remove(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnVisibilityListener {

        boolean isShow(FloatViewController viewController);
    }
}
