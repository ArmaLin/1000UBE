package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.UPDATE_FIELD_AVATARID;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.databinding.WindowMemberProfileAvatarEditorBinding;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.model.repository.MemberAvatarRepo;
import com.dyaco.spirit_commercial.model.repository.RepoCallback;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.GridViewSpaceItemDecoration;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.List;

public class MemberProfileAvatarEditorWindow extends BasePopupWindow<WindowMemberProfileAvatarEditorBinding> {
    private Context mContext;
    private View v_background;


    public MemberProfileAvatarEditorWindow(Context context) {
        super(context, 300, 1024, 0, GENERAL.TRANSLATION_Y, false, true, true, true);


        mContext = context;

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        initRecyclerView();

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        new RxTimer().timer(500, number -> getAvatarData());
    }

    AvatarAdapter avatarAdapter;

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 9);

        RecyclerView recyclerView = getBinding().recyclerview;
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridViewSpaceItemDecoration(7, 32, mContext));
        avatarAdapter = new AvatarAdapter(mContext, gridLayoutManager);
        recyclerView.setAdapter(avatarAdapter);

        avatarAdapter.setOnItemClickListener(bean -> {

            //Web Api 更新 AVATAR
            String avatarId = bean.getAvatarTag();
            new CallWebApi(mContext).updateUserInfo(UPDATE_FIELD_AVATARID, String.valueOf(avatarId),
                    data -> {
                        userProfileViewModel.setAvatarId(avatarId);
                        returnValue(new MsgEvent(avatarId));
                        dismiss();
                    });

           // saveData(bean.getAvatarTag());
        });
    }

    private void getAvatarData() {

        new MemberAvatarRepo().getData(0, new RepoCallback<AvatarBean>() {
            @Override
            public void onSuccess(List<AvatarBean> dataList) {
                if (avatarAdapter != null) avatarAdapter.setData2View(dataList);

                getBinding().placeholderView.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String error) {
                Log.d("ERROR", "onFail: " + error);
            }
        });

    }

    private void initView() {

//        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
//        for (int i = 0; i < getBinding().rgSelectItem.getChildCount(); i++) {
//            View o = getBinding().rgSelectItem.getChildAt(i);
//            if (o instanceof RadioButton) {
//                listOfRadioButtons.add((RadioButton) o);
//                if (listOfRadioButtons.get(i).getTag() != null) {
//                    if (Integer.parseInt((String) listOfRadioButtons.get(i).getTag()) == userProfileEntity.getAvatarTag()) {
//                        listOfRadioButtons.get(i).setChecked(true);
//                    } else {
//
//                    }
//                }
//            }
//        }
//
//

//        // ((MainActivity)mContext).userSettingBean.getAvatarIcon()
//        getBinding().rgSelectItem.setOnCheckedChangeListener((group, checkedId) -> {
//            ArrayList<RadioButton> listOfRadioButtons1 = new ArrayList<>();
//            for (int i = 0; i < getBinding().rgSelectItem.getChildCount(); i++) {
//                View o = getBinding().rgSelectItem.getChildAt(i);
//                if (o instanceof RadioButton) {
//                    listOfRadioButtons1.add((RadioButton) o);
//                    if (listOfRadioButtons1.get(i).getId() == checkedId) {
//                        if (listOfRadioButtons1.get(i).getTag() != null) {
//                            saveData(Integer.parseInt((String) listOfRadioButtons1.get(i).getTag()));
//                            //  ((MainActivity) mContext).userSettingBean.setAvatarIconTag(Integer.parseInt((String) listOfRadioButtons1.get(i).getTag()));
//                        }
//                    }
//                }
//            }
//        });
    }

    private void saveData(String avatarTag) {
        userProfileViewModel.setAvatarId(avatarTag);

        returnValue(new MsgEvent(avatarTag));
        dismiss();

//        SpiritDbManager.getInstance(getApp()).
//                updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
//                    @Override
//                    public void onUpdated() {
//                        super.onUpdated();
//                        returnValue(new MsgEvent(avatarTag));
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onError(String err) {
//                        super.onError(err);
//
//                        Toast.makeText(getApp(), "Failure:" + err, Toast.LENGTH_LONG).show();
//                    }
//                });
    }
}
