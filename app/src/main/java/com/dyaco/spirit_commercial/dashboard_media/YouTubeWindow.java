package com.dyaco.spirit_commercial.dashboard_media;


import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_MAINTENANCE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_YOUTUBE_FULL_SCREEN;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_WEBVIEW_BACK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow2;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;

public class YouTubeWindow extends BasePopupWindow2 implements AdvancedWebView.Listener {
    //WebView開啟時，點Gboard鍵盤設定會觸發Resume，導致menu被關閉
    public static boolean isWebViewOn;
    private MainActivity mainActivity;
  //  private final View youBack;
    private ProgressBar progressBar;
    int vWidth, vHeight;
    String webUrl;

    public YouTubeWindow(Context context,String webUrl) {
        super(context, 0, 0, 0, GENERAL.FADE, false, false);



        // ★ 攔截 PopupWindow 的觸控事件，避免走到預設的 onTouchEvent → dismiss()
        setTouchInterceptor((v, event) -> {
            // 這裡你可以視需求縮小條件，先粗暴一點全吃掉
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                Log.d("WEB_VIEW", "ignore ACTION_OUTSIDE for YouTubeWindow");
                return true; // 事件消費掉，不往下傳 → 不會觸發 dismiss()
            }
            return false;
        });


        this.webUrl = webUrl;
        initHookWebView();
        isWebViewOn = true;
        Log.d("YYYYYYOOOOOO", "#################YouTubeWindow: ");

        View view = LayoutInflater.from(context).inflate(R.layout.window_youtube, (ViewGroup) parentView);
        setContentView(view);
        mainActivity = (MainActivity) context;

        vWidth = getWidth();
        vHeight = getHeight();

       // youBack = view.findViewById(R.id.youBack);
        initWebView(view);

        LiveEventBus.get(CLOSE_YOUTUBE_FULL_SCREEN, boolean.class).observeForever(observer1);
        LiveEventBus.get(ON_WEBVIEW_BACK, boolean.class).observeForever(observer2);

//        LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA, Boolean.class).observe(getViewLifecycleOwner(), s -> {
//            mbMedia.callOnClick();
//        });

        setFocusable(true);//WebView的鍵盤

        if (mainActivity.appStatusViewModel.currentStatus.get() == STATUS_MAINTENANCE) {
            Log.d("WEB_VIEW", "YouTubeWindow:  OVERLAY");
            setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }

    }

    Observer<Boolean> observer1 = this::onChanged;
    Observer<Boolean> observer2 = this::onBack;

    private WebSettings webSettings;
    private MyChrome myChrome;
    private AdvancedWebView advancedWebView;

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(View view) {

        Looper.myQueue().addIdleHandler(() -> {

            ViewStub viewStub = view.findViewById(R.id.viewStub);
            progressBar = view.findViewById(R.id.progress);
            advancedWebView = viewStub.inflate().findViewById(R.id.advancedWebView);

            advancedWebView.setListener(mainActivity, this);
            advancedWebView.setWebViewClient(new WebViewClient());

            //  m_wvBrowser.setScrollbarFadingEnabled(false);
            //   String newUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50";

            myChrome = new MyChrome();
            advancedWebView.setWebChromeClient(myChrome);
            advancedWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            webSettings = advancedWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            //  webSettings.setAllowFileAccess(true);
//            webSettings.setAppCacheEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
         //   webSettings.setSaveFormData(true);
           // webSettings.setSavePassword(true);

//            if (type == 0) {
//                advancedWebView.loadUrl("https://www.youtube.com/");
//            } else {
//                advancedWebView.loadUrl("https://tv.youtube.com/");
//            }

            advancedWebView.loadUrl(webUrl);


            return false;
        });
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {

        if (webSettings != null) {
            if (!webSettings.getLoadsImagesAutomatically()) {
                webSettings.setLoadsImagesAutomatically(true);
            }
        }

        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.d("WEB_VIEW", "onPageError: ");

        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
        Log.d("WEB_VIEW", "onExternalPageRequest: ");
    }

    private void onChanged(Boolean s) {
        try {
            myChrome.onHideCustomView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onBack(Boolean s) {
        try {
            if (advancedWebView.canGoBack()) {

                if (isFullScreenOn) {
                    if (myChrome != null) myChrome.onHideCustomView();
                }

                advancedWebView.goBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFullScreenOn;
    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public void onHideCustomView() {
            // ★ PopupWindow 已經沒在顯示就不要亂動
            if (!YouTubeWindow.this.isShowing()) {
                Log.w("WEB_VIEW", "onHideCustomView() called but popup not showing, ignore");
                return;
            }
            // ★ mainActivity 或 mCustomView 為 null 就不要做後面動作
            if (mainActivity == null || mCustomView == null) {
                Log.w("WEB_VIEW", "onHideCustomView() mainActivity or mCustomView is null, ignore");
                return;
            }

            // ★ 離開全螢幕時，把 PopupWindow 的觸控打開
            YouTubeWindow.this.setTouchable(true);

            isFullScreenOn = false;

            // popup 視窗大小恢復
            update(vWidth, vHeight);

            ((FrameLayout) mainActivity.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;

            mainActivity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(this.mOriginalSystemUiVisibility);

            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }
        }




        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {

            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }

            isFullScreenOn = true;

            // ★ 全螢幕播放時，讓 PopupWindow 不吃任何觸控事件
            YouTubeWindow.this.setTouchable(false);

            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = mainActivity.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = mainActivity.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;

            ((FrameLayout) mainActivity.getWindow().getDecorView())
                    .addView(this.mCustomView, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));

            mainActivity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            // 把 PopupWindow 本身縮到 0x0（你原本就有）
            update(0, 0);
        }

    }


    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi", "SoonBlockedPrivateApi"})
    private void initHookWebView() {
        // 如果是非系統程序則按正常程式走
//        if (Process.myUid() != Process.SYSTEM_UID) {
//            return;
//        }
        if (ApplicationInfo.FLAG_SYSTEM != 1) {
            return;
        }

        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低於Android O版本
                Constructor<?> providerConstructor = Objects.requireNonNull(factoryProviderClass).getConstructor(delegateClass);
                providerConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
            } else {
                //Reflective access to CHROMIUM_WEBVIEW_FACTORY_METHOD will throw an exception when targeting API 31 and above
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = Objects.requireNonNull(factoryProviderClass).getMethod(chromiumMethodNameStr, delegateClass);
                sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
            }

            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
            } else {
                Log.e("WEB_VIEW4", "Hook failed!");
            }
        } catch (Exception e) {
            Log.e("WEB_VIEW5", e.getLocalizedMessage());
        }
    }


    @Override
    public void dismiss() {
        Log.d("WEB_VIEW", "dismiss() called (Ask Gemini)",
                new Exception("YouTubeWindow dismiss stack"));

        // 全螢幕時，不讓 PopupWindow 直接把整個視窗收掉
        if (isFullScreenOn) {
            Log.d("WEB_VIEW", "ignore dismiss from PopupDecorView while fullscreen");
            return;
        }

        LiveEventBus.get(CLOSE_YOUTUBE_FULL_SCREEN, boolean.class)
                .removeObserver(observer1);
        LiveEventBus.get(ON_WEBVIEW_BACK, boolean.class)
                .removeObserver(observer2);

        isWebViewOn = false;

        // 先不要設為 null，避免 WebView 晚一點才 callback onHideCustomView() 時 NPE
        // mainActivity = null;

        super.dismiss();

        Log.d("WEB_VIEW", "dismiss done");
    }




}
