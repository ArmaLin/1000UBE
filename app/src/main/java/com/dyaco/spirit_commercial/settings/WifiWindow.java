package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.wifi.WifiControlUtils.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWifiBinding;
import com.dyaco.spirit_commercial.support.CommandUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.QRCodeUtil;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.wifi.AlertRemoveWifiWindow;
import com.dyaco.spirit_commercial.support.wifi.WifiControlUtils;
import com.dyaco.spirit_commercial.support.wifi.WifiControlUtils.WifiEntity;
import com.dyaco.spirit_commercial.support.wifi.WifiPasswordEditorWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

@SuppressWarnings("deprecation")
public class WifiWindow extends BasePopupWindow<WindowWifiBinding> {
    AlertRemoveWifiWindow alertCustomExitWindow;
    WifiPasswordEditorWindow popupWindow;
    private final MainActivity m;

    WifiRecyclerViewAdapter mAdapter;

    private final WifiControlUtils wifiControlUtils;

    // 新增：移除WiFi成功後，跳過 SupplicantState 檢查的旗標
    private boolean skipSupplicantStateCheck = false;

    public WifiWindow(Context context, boolean isFloat) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, isFloat, true);

        m = ((MainActivity) context);

        //能夠解決 90% 的 WiFi 掃描不完整問題 ? ?
        CommandUtil.executeAsRoot("svc wifi scanalwayson enable");
        CommandUtil.executeAsRoot("settings put global wifi_scan_always_enabled 1");

//        String scanResults = CommandUtil.executeAsRoot("wpa_cli -i wlan0 scan_results");
//        Log.d("###WIFI", "WifiWindow: " + scanResults);


        initView();

        wifiControlUtils = new WifiControlUtils(m);

        setBroadcastReceiver();

        initCheckbox();


        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new WifiRecyclerViewAdapter(mContext);
        recyclerView.setAdapter(mAdapter);


        // 添加下拉刷新監聽
        getBinding().swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "下拉刷新觸發，正在掃描 WiFi...");
            getBinding().progress.setVisibility(View.VISIBLE);
            wifiControlUtils.scanWifi();
            getBinding().swipeRefreshLayout.setRefreshing(false);
        });

        // 設定觸發距離，讓用戶拉得更低一點才會觸發
        getBinding().swipeRefreshLayout.setProgressViewOffset(true, 0, 300);//scale縮放，從0拉到200逐漸變大
        getBinding().swipeRefreshLayout.setDistanceToTriggerSync(400); // 設定觸發距離（單位：像素）

        mAdapter.setOnItemClickListener((iWifi, view1, view2, progress) -> {
            if (getBinding() == null) return;
            if (CheckDoubleClick.isFastClick()) return;

            if (!iWifi.isConnected()) {
                String password = "";
                if (iWifi.isEncrypt() && !iWifi.isSaved()) {
                    // 需要密碼且尚未儲存密碼
                    popupWindow = new WifiPasswordEditorWindow(mContext);
                    popupWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
                    popupWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                        @Override
                        public void onStartDismiss(MsgEvent value) {
                            if (value != null) {

                                hrConnectingAnimation(iWifi, view1, view2, progress);
                                Log.d(TAG, "#########密碼: " + value.getObj());
                                wifiControlUtils.connectWifi(iWifi, (String) value.getObj());
                            }
                        }

                        @Override
                        public void onDismiss() {
                        }
                    });
                } else {

                    //已儲存密碼
                    hrConnectingAnimation(iWifi, view1, view2, progress);

                    wifiControlUtils.connectWifi(iWifi, password);
                }
            }
        });

        mAdapter.setOnLongItemClickListener(bean -> {
            alertCustomExitWindow = new AlertRemoveWifiWindow(mContext, bean.name);
            alertCustomExitWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            alertCustomExitWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null && ((boolean) value.getObj())) {

                        //剛連線就remove會失敗, 要先執行Scan
                        wifiControlUtils.scanWifi();
                        if (wifiControlUtils.removeWifi(bean.name)) {
                            Log.d(TAG, "移除WIFI成功: ");
                            // 設定旗標，移除成功時跳過 SupplicantState 檢查
                            skipSupplicantStateCheck = true;
                            getBinding().progress.setVisibility(View.VISIBLE);
                            wifiControlUtils.scanWifi();
                        } else {
                            Log.d(TAG, "移除失敗: " + bean.name);
                        }
                    }
                }

                @Override
                public void onDismiss() {
                    alertCustomExitWindow = null;
                }
            });
        });

        getBinding().cbHR.setChecked(MainActivity.isCbWifiOn);
        boolean isCheck = getBinding().cbHR.isChecked();
        getBinding().recyclerView.setVisibility(isCheck ? View.VISIBLE : View.GONE);
    }

    private void setSharedWifiQrCode() {
        new RxTimer().timer(500, number -> {
            try {
                //      Bitmap qrCodeBitmap = QRCodeUtil.generateQRCodeWithLogo(m, wifiControlUtils.getSharedWifi(), R.drawable.icon_maintenance_wifi_linked);
                Bitmap qrCodeBitmap = QRCodeUtil.generateQRCodeWithText(m, wifiControlUtils.getSharedWifi(), wifiControlUtils.getCurrentSSID());
                Glide.with(getApp())
                        //    .load(new CommonUtils().createQRCode(wifiControlUtils.getSharedWifi(), 184, 8))
                        .load(qrCodeBitmap)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        //  .transition(DrawableTransitionOptions.withCrossFade(500))
                        .transition(GenericTransitionOptions.with(R.anim.scale_up))
                        .error(R.drawable.no_qrcode)
                        .into(getBinding().wifiImage);
            } catch (Exception e) {
                showException(e);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        wifiControlUtils.unregisterReceiver();

        if (alertCustomExitWindow != null && alertCustomExitWindow.isShowing()) {
            alertCustomExitWindow.dismiss();
            alertCustomExitWindow = null;
        }

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });

        getBinding().hrTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNetworkDialog();
            }
        });
    }

    private void setBroadcastReceiver() {
        wifiControlUtils.setWifiEventListener(new WifiControlUtils.WifiEventListener() {
            @Override
            public void onWifiStateChanged(int state) {
                handleWifiStateChanged(state);
            }

            @Override
            public void onNetworkStateChanged(NetworkInfo.DetailedState state) {
                handleNetworkStateChanged(state);
            }

            @Override
            public void onScanResultsAvailable(boolean isUpdated) {
                handleScanResults(isUpdated);
            }

            @Override
            public void onSupplicantStateChanged(int state) {
                handleSupplicantStateChanged(state);
            }
        });

        wifiControlUtils.registerReceiver();
    }

    private void handleWifiStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                Log.i(TAG, "WiFi is disabled.");
                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.GONE);
                }
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Log.i(TAG, "Disabling WiFi...");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.i(TAG, "WiFi is enabled.");
                if (wifiControlUtils != null && getBinding() != null) {
                    wifiControlUtils.scanWifi();
                    getBinding().progress.setVisibility(View.VISIBLE);
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, "Enabling WiFi...");
                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.VISIBLE);
                }
                break;
            default:
                Log.d(TAG, "Unknown WiFi state.");
                break;
        }
    }


    private void handleNetworkStateChanged(NetworkInfo.DetailedState state) {
        switch (state) {
            case IDLE:
                Log.i(TAG, "WiFi is idle.");
                break;
            case SCANNING:
                Log.i(TAG, "Scanning for WiFi networks...");
                break;
            case AUTHENTICATING:
                Log.i(TAG, "Authenticating...");
                break;
            case OBTAINING_IPADDR:
                Log.i(TAG, "Obtaining IP address...");
                break;
            case CONNECTED:
                if (mAdapter != null && wifiControlUtils != null) {
                    WifiInfo wifiInfo = wifiControlUtils.getWifiManager().getConnectionInfo();
                    Log.d(TAG, "WiFi connected: " + wifiInfo.getSSID());
                    List<WifiEntity> wifiEntityList = wifiControlUtils.modifyWifi();
                    Log.i(TAG, "Number of available WiFi networks: " + wifiEntityList.size());
                    mAdapter.setData2View(wifiEntityList);
                    if (getBinding() != null) {
                        getBinding().recyclerView.smoothScrollToPosition(0);
                        setSharedWifiQrCode();
                        getBinding().progress.setVisibility(View.GONE);
                    }
                }
                break;
            case CONNECTING:
                Log.i(TAG, "WiFi connecting...");
                break;
            case SUSPENDED:
                Log.i(TAG, "Connection suspended.");
                break;
            case DISCONNECTING:
                Log.i(TAG, "Disconnecting...");
                break;
            case FAILED:
                Log.i(TAG, "Connection failed.");
                break;
            case BLOCKED:
                Log.i(TAG, "WiFi is blocked.");
                break;
            case VERIFYING_POOR_LINK:
                Log.i(TAG, "Poor signal detected.");
                break;
            case CAPTIVE_PORTAL_CHECK:
                Log.i(TAG, "Captive portal check required.");
                break;
            default:
                break;
        }
    }

    /**
     * 掃描結果
     * @param isUpdated 是否有更新
     */
    private void handleScanResults(boolean isUpdated) {
        //  Log.e(TAG, "EXTRA_RESULTS_UPDATED: " + isUpdated);
        if (!isUpdated || wifiControlUtils == null || mAdapter == null) {
            return;
        }

        WifiInfo wifiInfo = wifiControlUtils.getWifiManager().getConnectionInfo();
        if (!skipSupplicantStateCheck) {
            if (wifiInfo != null && wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
                Log.i(TAG, "WiFi 連線中，暫不更新掃描結果");
                return;
            }
        } else {
            Log.i(TAG, "跳過 SupplicantState 檢查，因為移除Wifi成功");
        }

        List<WifiEntity> iWifiList = wifiControlUtils.modifyWifi();
        Log.i(TAG, "掃描結果更新，筆數：" + iWifiList.size());
        if (!iWifiList.isEmpty()) {
            mAdapter.setData2View(iWifiList);
            if (getBinding() != null) {
                getBinding().progress.setVisibility(View.GONE);
            }
        } else {
            Log.i(TAG, "掃描結果為空則重新掃描");
            new Handler(Looper.getMainLooper()).postDelayed(wifiControlUtils::scanWifi, 500);
//            wifiControlUtils.scanWifi();
        }
        // 處理完後重設旗標
        skipSupplicantStateCheck = false;
    }


    private void handleSupplicantStateChanged(int state) {
        if (state == WifiManager.ERROR_AUTHENTICATING) {
            Log.d(TAG, "密碼錯誤");
            Toasty.warning(getApp(), "Incorrect password", Toasty.LENGTH_SHORT).show();

            List<WifiEntity> iWifiList = wifiControlUtils.modifyWifi();
            Log.d(TAG, "掃描結果更新，筆數：" + iWifiList.size());
            if (!iWifiList.isEmpty()) {
                mAdapter.setData2View(iWifiList);
            }
        }
    }


    private void hrConnectingAnimation(WifiEntity wifiEntity, View clBase, View ivHrIcon, ProgressBar progressBar) {
        try {
            mAdapter.setClickable(false);
            wifiEntity.setConnecting(true);
            clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));
            progressBar.setVisibility(View.VISIBLE);
            ivHrIcon.setBackgroundResource(0);
        } catch (Exception e) {
            showException(e);
        }
    }

    private void initCheckbox() {
        getBinding().cbHR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getBinding().recyclerView.setVisibility(View.VISIBLE);
                if (mAdapter != null) {
                    mAdapter.setData2View(new ArrayList<>(0));
                }
                wifiControlUtils.openWifi();
                getBinding().progress.setVisibility(View.VISIBLE);
                MainActivity.isCbWifiOn = true;
            } else {
                if (wifiControlUtils != null) {
                    wifiControlUtils.closeWifi();
                }
                getBinding().recyclerView.setVisibility(View.INVISIBLE);
                getBinding().tvNoDevice.setVisibility(View.GONE);
                MainActivity.isCbWifiOn = false;
                if (mAdapter != null) {
                    mAdapter.setData2View(new ArrayList<>(0));
                }
            }
        });
    }


    private void showAddNetworkDialog() {
        // 1. 用 mContext inflate 自訂版面
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_add_network, null, false);
        EditText etSsid     = view.findViewById(R.id.et_ssid);
        Spinner spSecurity = view.findViewById(R.id.sp_security);
        EditText etPassword = view.findViewById(R.id.et_password);

        // 2. Spinner 選項
        final String[] securityTypes = {"WPA/WPA2 PSK", "WEP", "EAP", "None"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_spinner_item,
                securityTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSecurity.setAdapter(adapter);

        // 3. 根據選擇決定密碼欄是否啟用
        spSecurity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                String type = securityTypes[pos];
                boolean needsPwd = !"None".equals(type) && !"EAP".equals(type);
                etPassword.setEnabled(needsPwd);
                //    if (!needsPwd) etPassword.setText("");
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


        etSsid.setText("CoreStarCo");
        etPassword.setText("core2295star0357");
        // 4. 建 AlertDialog
        new AlertDialog.Builder(mContext)
                .setTitle("手動新增 Wi-Fi")
                .setView(view)
                .setPositiveButton("連線", (dlg, which) -> {
                    String ssid     = etSsid.getText().toString().trim();
                    String security = spSecurity.getSelectedItem().toString();
                    String pwd      = etPassword.getText().toString();

                    if (ssid.isEmpty()) {
                        Toasty.warning(mContext, "請輸入 SSID", Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if ("EAP".equals(security)) {
                        Toasty.info(mContext, "EAP 目前尚未支援", Toasty.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = wifiControlUtils.addWifiNetwork(ssid, pwd, security);
                    if (ok) {
                        Toasty.success(mContext, "已新增並嘗試連線", Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(mContext, "新增失敗，請檢查權限或 SSID 是否重複", Toasty.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

}
