package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceErrorCodeLogBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MaintenanceErrorCodeLogFragment extends BaseBindingDialogFragment<FragmentMaintenanceErrorCodeLogBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();
        initErrorLog();

    }

    List<ErrorLogBean> errorLogBeanList;
    ErrorLogAdapter errorLogAdapter;

    private void initErrorLog() {

        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);

        errorLogAdapter = new ErrorLogAdapter(requireActivity());
        recyclerView.setAdapter(errorLogAdapter);

        errorLogAdapter.setOnItemClickListener(new ErrorLogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ErrorMsgEntity errorMsgEntity) {

                SpiritDbManager.getInstance(getApp()).deleteErrorMsg(errorMsgEntity.getUid(),
                        new DatabaseCallback<ErrorMsgEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();
                                Log.d("UPLOAD_ERROR_LOG", "刪除errorLog:" + errorMsgEntity.getErrorMessage());
                                getErrorLogData();
                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                Log.d("UPLOAD_ERROR_LOG", "刪除log資料失敗");
                            }
                        });
            }
        });

//        errorLogBeanList = getErrorLogData();
//        errorLogAdapter.setData2View(errorLogBeanList);


        getErrorLogData();
    }

    private void getErrorLogData() {
        SpiritDbManager.getInstance(getApp()).getErrorMsgList(new DatabaseCallback<ErrorMsgEntity>() {
                    @Override
                    public void onDataLoadedList(@NotNull List<? extends ErrorMsgEntity> errorMsgEntityList) {
                        super.onDataLoadedList(errorMsgEntityList);
                        errorLogAdapter.setData2View((List<ErrorMsgEntity>) errorMsgEntityList);
                    }
                });
    }

//    private List<ErrorLogBean> getErrorLogData() {
//        List<ErrorLogBean> list = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            list.add(new ErrorLogBean("02/06/2021", "05:20:43 AM GMT+7", "E" + i, "{\"message\": \"[reviews] Product.fuga -> is marked as @external but is not used by a @requires, @key, or @provides directive.\", \"extensions\": {\"code\": \"EXTERNAL_UNUSED\"}}"));
//        }
//
//
//        return list;
//    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        SpiritDbManager.getInstance(getApp()).clear();
    }
}