## 开箱即用的 Android 悬浮窗 FloatWindowX

<img src="images/floatWindowX.png" width="200px" height="auto">

#### 需要权限

##### demo 支持 sdk >= 23 的 Android 版本。如果需要支持小于 sdk 23 的设备，可以使用额外的权限库。

```java
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
<uses-permission android:name="android.permission.ACTION_MANAGE_OVERLAY_PERMISSION"/>
```

#### 请在 Application 初始化

```
  FloatX.get().init(App.get());
```

#### 最简单的使用

```java

// floatView 自己来构造就可。floatView 只是简单的 view ~
FloatConfig floatConfig=new FloatConfig(floatView);

        FloatX.get()
        .addFloat(floatConfig)
        // flag 针对多个浮窗时的识别符，必须唯一。可以自行创建常量类维护。
        .show(flag);
```

#### 高级一点

```java
// 不需要展示的页面(进入这些页面不需要展示，但是退出后需要继续展示)
.setNotDisplayActivities(notDisplayActivities)
// 需要关闭的页面(一旦进入这些页面，就彻底销毁悬浮窗了。退出也不会展示，只有再次创建才可以)
        .setCloseActivities(mCloseActivities)
// 【默认为不展示】是否需要在桌面也显示浮窗
        .setDesktopShow(mDesktopShow.isChecked());


// 松手时动画
        floatConfig.setTouchActionUpListener(new TouchActionUpListener(){
@Override
public boolean actionUp(@Nullable FloatViewController controller,float rawX,float rawY){
        // 如果业务的松手时动画有自己的需求，就在这里返回 true。然后实现自己的动画就好。
        // controller.updateViewLocation(x,y); 可以改变 view 的位置。其他的 api 能不调就别调
        return false;
        }
        });
```

#### 直接使用可以下载项目根目录下的 arr 文件夹中的 aar 包。

<br/><br/>

### 下面是以后要做的功能，如果有别的想法也可以在 issues 提出

- [ ] 支持没有权限时在单个页面展示浮窗
- 没有浮窗权限时还要展示，弊端很大的。多个页面层级每个都有浮窗的话，就会给用户闪烁的感受。但之所以加这个功能，还是因为有仅一个页面展示的需求。
- [ ] 项目改为 kotlin
- [ ] ......

## License

```
---
```
