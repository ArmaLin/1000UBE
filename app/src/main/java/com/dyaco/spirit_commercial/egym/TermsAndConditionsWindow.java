package com.dyaco.spirit_commercial.egym;

import android.content.Context;
import android.util.Log;

import com.dyaco.spirit_commercial.databinding.WindowTermsAndConditionsBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class TermsAndConditionsWindow extends BasePopupWindow<WindowTermsAndConditionsBinding> {
    private Context mContext;

    public TermsAndConditionsWindow(Context context, String text) {
        super(context, 500, 0, 0, GENERAL.TRANSLATION_Y,false,true,true,true);
        mContext = context;
        initView();



        try {
            getBinding().tvTerms.setText(android.text.Html.fromHtml(text, android.text.Html.FROM_HTML_MODE_LEGACY));
        } catch (Exception e) {
            Log.d("EgymUtil", "Exception" + e.getMessage());
            e.printStackTrace();
        }

    }


    private void initView() {
        getBinding().btnConfirm.setOnClickListener(v -> {

//            EgymApiManager.acceptTermsAndConditions(EGYM_BEARER_AUTHORIZATION, "en_US", new ApiResponseListener<>() {
//
//                @Override
//                public void onSuccess(ResponseBody responseBody) {
//                    Log.d("EgymUtil", "✅ 條款接受成功！");
//                }
//
//                @Override
//                public void onFailure(@NonNull Throwable error, Integer code) {
//                    Log.e("EgymUtil", "❌ 條款接受失敗：" + error.getMessage());
//                }
//            });

            returnValue(new MsgEvent(true));
            dismiss();
        });
        getBinding().btnCancel.setOnClickListener(v -> {
            returnValue(new MsgEvent(false));
            dismiss();
        });


//        getBinding().scrollViewTerms.getViewTreeObserver().addOnScrollChangedListener(() -> {
//            ScrollView scrollView = getBinding().scrollViewTerms;
//            int scrollY = scrollView.getScrollY();
//            int contentHeight = scrollView.getChildAt(0).getMeasuredHeight();
//            int scrollViewHeight = scrollView.getHeight();
//
//            if ((scrollY + scrollViewHeight) >= (contentHeight - 10)) {
//                getBinding().btnConfirm.setEnabled(true); // 滑到底，啟用按鈕
//                getBinding().btnConfirm.setAlpha(1f); // 啟用時全不透明
//
//            }
//        });



//        getBinding().scrollViewTerms.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            // 還能向下捲
//            boolean canDown = getBinding().scrollViewTerms.canScrollVertically(1);
//            if (canDown && getBinding().bottomShadow.getVisibility() != View.VISIBLE) {
//                getBinding().bottomShadow.setVisibility(View.VISIBLE);
//            } else if (!canDown && getBinding().bottomShadow.getVisibility() != View.GONE) {
//                getBinding().bottomShadow.setVisibility(View.GONE);
//            }
//        });


//        getBinding().scrollViewTerms.setOnScrollChangeListener((v, scrollX, scrollY, oldX, oldY) -> {
//            boolean canDown = getBinding().scrollViewTerms.canScrollVertically(1);
//            getBinding().bottomShadow.setVisibility(canDown ? View.VISIBLE : View.GONE);
//        });

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
