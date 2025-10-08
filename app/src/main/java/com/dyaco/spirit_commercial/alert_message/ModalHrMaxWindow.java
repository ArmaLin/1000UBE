package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowModalHrMaxBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

public class ModalHrMaxWindow extends BasePopupWindow<WindowModalHrMaxBinding> {

    public ModalHrMaxWindow(Context context, WorkoutViewModel w) {
        super(context, 300, 576, 0, GENERAL.TRANSLATION_Y,false,true,true,true);

        getBinding().btnIgnore.setOnClickListener(view -> dismiss());

        //w.selTargetHrBpm 200 > HrTargetHrMax 185 > 選定的目標心率超過您年齡預測的最大心率。
        getBinding().btnSetMaximum.setOnClickListener(view -> {
            //用年齡預測的最大心率(HrTargetHrMax) 取代 選定的目標心率(selTargetHrBpm)
          //  w.setHrTargetHrMax(w.selTargetHrBpm.get());
            w.selTargetHrBpm.set(w.getHrTargetHrMax());

            dismiss();
        });
    }

}
