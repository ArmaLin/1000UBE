//package com.dyaco.spirit_commercial.dashboard_media;
//
//
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_YOUTUBE_FULL_SCREEN;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewStub;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebViewClient;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import androidx.lifecycle.Observer;
//
//import com.dyaco.spirit_commercial.MainActivity;
//import com.dyaco.spirit_commercial.R;
//import com.dyaco.spirit_commercial.databinding.WindowYoutubeBinding;
//import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
//import com.dyaco.spirit_commercial.support.intdef.GENERAL;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.Objects;
//
//import im.delight.android.webview.AdvancedWebView;
//
//public class YouTubeWindow2 extends BasePopupWindow<WindowYoutubeBinding> implements AdvancedWebView.Listener {
//    private MainActivity mainActivity;
//    private final View youBack;
//    private ProgressBar progressBar;
//    int vWidth, vHeight;
//
//    public YouTubeWindow2(Context context) {
//        super(context, 0, 0, 0, GENERAL.ALPHA, false, true, true, true);
//
//        mainActivity = (MainActivity) context;
//
//        vWidth = getWidth();
//        vHeight = getHeight();
//
//        youBack = getBinding().youBack;
//        progressBar = getBinding().progress;
//
//        initWebView();
//
//        LiveEventBus.get(CLOSE_YOUTUBE_FULL_SCREEN, boolean.class).observeForever(observer1);
//
////        LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA, Boolean.class).observe(getViewLifecycleOwner(), s -> {
////            mbMedia.callOnClick();
////        });
//    }
//
//    Observer<Boolean> observer1 = this::onChanged;
//
//    private WebSettings webSettings;
//    private MyChrome myChrome;
//    private AdvancedWebView advancedWebView;
//
//    @SuppressLint("SetJavaScriptEnabled")
//    private void initWebView() {
//        Log.d("DWWWWDWDWDWDW", "@@@@@initWebView:@@@@@ ");
//        Looper.myQueue().addIdleHandler(() -> {
//
//
//            ViewStub viewStub = getBinding().viewStub;
//
//            advancedWebView = viewStub.inflate().findViewById(R.id.advancedWebView);
//
//            advancedWebView.setListener(mainActivity, this);
//            advancedWebView.setWebViewClient(new WebViewClient());
//
//            //   String newUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50";
//
//            myChrome = new MyChrome();
//            advancedWebView.setWebChromeClient(myChrome);
//            advancedWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//
//            webSettings = advancedWebView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//            //  webSettings.setAllowFileAccess(true);
//            webSettings.setAppCacheEnabled(true);
//            webSettings.setDomStorageEnabled(true);
//            webSettings.setLoadsImagesAutomatically(false);
//            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//            webSettings.setUseWideViewPort(true);
//            webSettings.setSaveFormData(true);
//            webSettings.setSavePassword(true);
//
//            advancedWebView.loadUrl("https://www.youtube.com/");
//
//            youBack.setVisibility(View.GONE);
//            return false;
//        });
//    }
//
//
//    @Override
//    public void onPageStarted(String url, Bitmap favicon) {
//        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
//        Log.d("WEB_VIEW", "onPageStarted: " + url);
//    }
//
//    @Override
//    public void onPageFinished(String url) {
//        Log.d("WEB_VIEW", "onPageFinished: " + url);
//
//        if (webSettings != null) {
//            if (!webSettings.getLoadsImagesAutomatically()) {
//                webSettings.setLoadsImagesAutomatically(true);
//            }
//        }
//
//        if (progressBar != null)
//            progressBar.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void onPageError(int errorCode, String description, String failingUrl) {
//        Log.d("WEB_VIEW", "onPageError: ");
//
//        if (progressBar != null)
//            progressBar.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
//        Log.d("WEB_VIEW", "onDownloadRequested: ");
//    }
//
//    @Override
//    public void onExternalPageRequest(String url) {
//        Log.d("WEB_VIEW", "onExternalPageRequest: ");
//    }
//
//    private void onChanged(Boolean s) {
//        Log.d("PPPEPEPEP", ": CLooooLLLLL");
//        try {
//            myChrome.onHideCustomView();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private class MyChrome extends WebChromeClient {
//
//        private View mCustomView;
//        private WebChromeClient.CustomViewCallback mCustomViewCallback;
//        protected FrameLayout mFullscreenContainer;
//        private int mOriginalOrientation;
//        private int mOriginalSystemUiVisibility;
//
//        MyChrome() {
//        }
//
//        public Bitmap getDefaultVideoPoster() {
//            if (mCustomView == null) {
//                return null;
//            }
//            return BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), 2130837573);
//        }
//
//        @Override
//        public void onHideCustomView() {
//            update(vWidth, vHeight);
//            ((FrameLayout) mainActivity.getWindow().getDecorView()).removeView(this.mCustomView);
//            this.mCustomView = null;
//            mainActivity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
//            mainActivity.setRequestedOrientation(this.mOriginalOrientation);
//            this.mCustomViewCallback.onCustomViewHidden();
//            this.mCustomViewCallback = null;
//        }
//
//        @Override
//        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
//
//
//            if (this.mCustomView != null) {
//                onHideCustomView();
//                return;
//            }
//            this.mCustomView = paramView;
//            this.mOriginalSystemUiVisibility = mainActivity.getWindow().getDecorView().getSystemUiVisibility();
//            this.mOriginalOrientation = mainActivity.getRequestedOrientation();
//            this.mCustomViewCallback = paramCustomViewCallback;
//
//
////            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
////            layoutParams.width = -1;
////            layoutParams.height = -1;
////            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
////            ((FrameLayout) mainActivity.getWindow().getDecorView()).addView(this.mCustomView, layoutParams);
//
//            ((FrameLayout) mainActivity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
//            mainActivity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//
//            update(0, 0);
//
//            Log.d("PPPEPEPEP", "onShowCustomView: ");
//            //    mainActivity.showMediaMenu(true);
//        }
//    }
//
//
//    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi", "SoonBlockedPrivateApi"})
//    private void initHookWebView() {
//        // 如果是非系統程序則按正常程式走
////        if (Process.myUid() != Process.SYSTEM_UID) {
////            return;
////        }
//        Log.d("WEB_VIEW", "initHookWebView11111: " + ApplicationInfo.FLAG_SYSTEM);
//        if (ApplicationInfo.FLAG_SYSTEM != 1) {
//            return;
//        }
//        Log.d("WEB_VIEW", "initHookWebView2222: " + ApplicationInfo.FLAG_SYSTEM);
//
//        int sdkInt = Build.VERSION.SDK_INT;
//        try {
//            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
//            Field field = factoryClass.getDeclaredField("sProviderInstance");
//            field.setAccessible(true);
//            Object sProviderInstance = field.get(null);
//            if (sProviderInstance != null) {
//                Log.d("WEB_VIEW1", "sProviderInstance isn't null");
//                return;
//            }
//
//            Method getProviderClassMethod;
//            Log.d("WEB_VIEW0", "initHookWebView: " + sdkInt);
//            if (sdkInt > 22) {
//                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
//            } else if (sdkInt == 22) {
//                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
//            } else {
//                Log.d("WEB_VIEW2", "Don't need to Hook WebView");
//                return;
//            }
//            getProviderClassMethod.setAccessible(true);
//            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
//            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
//            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
//            delegateConstructor.setAccessible(true);
//            if (sdkInt < 26) {//低於Android O版本
//                Constructor<?> providerConstructor = Objects.requireNonNull(factoryProviderClass).getConstructor(delegateClass);
//                providerConstructor.setAccessible(true);
//                sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
//            } else {
//                //Reflective access to CHROMIUM_WEBVIEW_FACTORY_METHOD will throw an exception when targeting API 31 and above
//                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
//                chromiumMethodName.setAccessible(true);
//                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
//                if (chromiumMethodNameStr == null) {
//                    chromiumMethodNameStr = "create";
//                }
//                Method staticFactory = Objects.requireNonNull(factoryProviderClass).getMethod(chromiumMethodNameStr, delegateClass);
//                sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
//            }
//
//            if (sProviderInstance != null) {
//                field.set("sProviderInstance", sProviderInstance);
//                Log.e("WEB_VIEW3", "Hook success!");
//            } else {
//                Log.e("WEB_VIEW4", "Hook failed!");
//            }
//        } catch (Exception e) {
//            Log.e("WEB_VIEW5", e.getLocalizedMessage());
//        }
//    }
//
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        LiveEventBus.get(CLOSE_YOUTUBE_FULL_SCREEN, boolean.class).removeObserver(observer1);
//        if (advancedWebView != null) advancedWebView.onDestroy();
//        mainActivity = null;
//    }
//}
