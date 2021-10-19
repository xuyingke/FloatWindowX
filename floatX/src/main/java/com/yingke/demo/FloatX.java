package com.yingke.demo;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FloatX {

    private boolean mDebug;

    private ConcurrentHashMap<String, FloatViewController> mViewControllerList;

    private Application mContext;

    private static FloatX mInstance;

    private List<OnVisibilityListener> mVisibilityListeners = new ArrayList<>();

    public static FloatX get() {
        if (mInstance == null) {
            synchronized (FloatX.class) {
                if (mInstance == null) {
                    mInstance = new FloatX();
                }
            }
        }
        return mInstance;
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
        mViewControllerList = new ConcurrentHashMap<String, FloatViewController>(3);
    }

    private FloatX() {
        // empty
    }

    public FloatX addFloat(@NonNull String floatFlag, @NonNull FloatConfig floatConfig) {
        if (mViewControllerList == null) {
            return this;
        }
        FloatViewController viewController = mViewControllerList.get(floatFlag);
        if (viewController == null) {
            FloatViewController controller = new FloatViewController(mContext);
            controller.setFloatBuilder(floatConfig);
            mViewControllerList.put(floatFlag, controller);
        }
        return this;
    }

    public void show(@NonNull String floatFlag) {
        if (mViewControllerList == null) {
            // 需要调用 init
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


    /**
     * 有页面被唤醒了
     *
     * @param activity activity
     */
    public void onResumed(@NonNull Activity activity) {
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

    /**
     * 有 activity 要看不到了、退到后台了
     *
     * @param activity activity
     */
    public void onActivityStopped(@NonNull Activity activity) {
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

    public synchronized void addVisibilityListeners(OnVisibilityListener listener) {
        try {
            mVisibilityListeners.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeVisibilityListeners(OnVisibilityListener listener) {
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
