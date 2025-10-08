package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_EZ_CAST;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_MIRA_CAST;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.dashboard_media.TvControllerWindow;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceTvSearchUsBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.fec.hdmiin.HdmiIn;

import es.dmoral.toasty.Toasty;

/**
 * 顯示影像主視窗 SCAN
 */
public class MaintenanceTvSearchUsWindow extends BasePopupWindow<FragmentMaintenanceTvSearchUsBinding> {
    private final Context mContext;

    public PopupWindow popupWindow;

    private RxTimer scanCheckTimer;

    public MaintenanceTvSearchUsWindow(Context context) {
        super(context, 500, 0, 0, GENERAL.FADE, false, false, false, false);
        mContext = context;


        //遙控器
        popupWindow = new TvControllerWindow(mContext,false);
        popupWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);





        getBinding().btnDone.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });


        new RxTimer().timer(1000, number -> {
            initHdmi();
        });


    }



    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        //等設定國家完成
        try {
            ((MainActivity) mContext).showLoading(true);
            new RxTimer().timer(1000, number ->
                    ((MainActivity) mContext).showLoading(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        popupWindow.dismiss();
        closeHdmi();
        super.dismiss();

    }


    MaintenanceTvMenuWindow maintenanceTvMenuWindow;

    public void showMaintenanceTvMenu(boolean isShow) {

        if (isShow) {
            if (maintenanceTvMenuWindow != null && maintenanceTvMenuWindow.isShowing()) return;
            maintenanceTvMenuWindow = new MaintenanceTvMenuWindow(mContext);
            maintenanceTvMenuWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.START | Gravity.TOP, 50, 80);
            maintenanceTvMenuWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    maintenanceTvMenuWindow = null;
                }
            });

        } else {
            if (maintenanceTvMenuWindow != null) {
                maintenanceTvMenuWindow.dismiss();
            }
        }
    }



    //初始化時會卡
    private boolean isOpen = false;

    public void initHdmi() {
        if (isOpen) return;
        try {
            isOpen = true;
            MainActivity.isReAssignView = true;
            HdmiIn hdmiIn = ((MainActivity) mContext).hdmiIn;
            //如果videoFullViewContainer2 一開始為 INVISIBLE ， AssignView不會往下跑
            hdmiIn.AssignView(getBinding().videoFullViewContainer2);

            getBinding().videoFullViewContainer2.setVisibility(View.INVISIBLE);

//            new RxTimer().timer(3000, number -> {
//                if (hdmiIn.SwitchHdmiSource(PORT_MIRA_CAST)) {
//                    hdmiIn.resumeVideo();
//                    new RxTimer().timer(4000, x -> {
//                        if (getBinding() != null) {
//                            ((MainActivity) mContext).showLoading(false);
//                            getBinding().videoFullViewContainer2.setVisibility(View.VISIBLE);
//                        }
//                    });
//                } else {
//                    Toasty.warning(mContext, "Please Try Again.", Toasty.LENGTH_SHORT).show();
//                }
//
//            });

            new RxTimer().timer(3000, number -> {

                boolean isTvOn = hdmiIn.CheckHdmiSource(PORT_MIRA_CAST);
             //   Log.d("SSSSSTTTTTTT", "initHdmiUS: " + isTvOn);
                if (isTvOn) {
                    if (hdmiIn.SwitchHdmiSource(PORT_MIRA_CAST)) {
                        hdmiIn.resumeVideo();
                        new RxTimer().timer(4000, x -> {
                            if (getBinding() != null) {
                                ((MainActivity) mContext).showLoading(false);
                                getBinding().videoFullViewContainer2.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Toasty.warning(mContext, "Please Try Again.", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    if (hdmiIn.SwitchHdmiSource(PORT_EZ_CAST)) {
                        hdmiIn.resumeVideo(); //不加這個會死當，要重新上電
                        new RxTimer().timer(4000, x -> {
                            if (getBinding() != null) {
                                ((MainActivity) mContext).showLoading(false);
                                getBinding().videoFullViewContainer2.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Toasty.warning(mContext, "Please Try Again.", Toasty.LENGTH_SHORT).show();
                    }
                }

            });


        } catch (Exception e) {
            Log.d("VVVCCCVVV", "initHdmi: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }




    public void closeHdmi() {

        if (((MainActivity) mContext).hdmiIn != null) {
            // Session has been closed; further changes are illegal.
            try {
                ((MainActivity) mContext).hdmiIn.pauseVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (getBinding() != null) {
            try {
                getBinding().videoFullViewContainer2.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
