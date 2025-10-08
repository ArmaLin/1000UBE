package com.dyaco.spirit_commercial.model.repository;

import static com.dyaco.spirit_commercial.App.getApp;

import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.RankEntity;

import java.util.List;

public class RankRepo implements IRepo<RankEntity> {
    @Override
    public void getData(int id, RepoCallback<RankEntity> repoCallback) {

        SpiritDbManager.getInstance(getApp()).getRankLit(new DatabaseCallback<RankEntity>() {
            @Override
            public void onDataLoadedList(List<RankEntity> rankEntityList) {
                super.onDataLoadedList(rankEntityList);
                repoCallback.onSuccess(rankEntityList);
            }
        });

//        List<RankingBean> list = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            list.add(new RankingBean("Member" + 1, 1, 10 + i, 10 + i, 10 + i, 10 + i, R.drawable.avatar_female_1_default,
//                    true));
//        }
//        listener.onSuccess(list);
    }
}
