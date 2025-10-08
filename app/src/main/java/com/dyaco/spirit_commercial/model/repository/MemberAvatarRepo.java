package com.dyaco.spirit_commercial.model.repository;

import com.dyaco.spirit_commercial.settings.AvatarBean;

import java.util.ArrayList;
import java.util.List;

public class MemberAvatarRepo implements IRepo<AvatarBean> {
    int memberCount = 34;

    @Override
    public void getData(int id, RepoCallback<AvatarBean> repoCallback) {
        List<AvatarBean> avatarBeanList = new ArrayList<>();
        avatarBeanList.add(new AvatarBean("avatar17"));
        avatarBeanList.add(new AvatarBean("avatar18"));
        avatarBeanList.add(new AvatarBean("avatar19"));
        avatarBeanList.add(new AvatarBean("avatar20"));
        avatarBeanList.add(new AvatarBean("avatar21"));
        avatarBeanList.add(new AvatarBean("avatar22"));
        avatarBeanList.add(new AvatarBean("avatar23"));
        avatarBeanList.add(new AvatarBean("avatar24"));
        avatarBeanList.add(new AvatarBean("avatar25"));
        avatarBeanList.add(new AvatarBean("avatar26"));
        avatarBeanList.add(new AvatarBean("avatar27"));
        avatarBeanList.add(new AvatarBean("avatar28"));
        avatarBeanList.add(new AvatarBean("avatar29"));
        avatarBeanList.add(new AvatarBean("avatar30"));
        avatarBeanList.add(new AvatarBean("avatar31"));
        avatarBeanList.add(new AvatarBean("avatar32"));
        avatarBeanList.add(new AvatarBean("avatar01"));
        avatarBeanList.add(new AvatarBean("avatar02"));
        avatarBeanList.add(new AvatarBean("avatar03"));
        avatarBeanList.add(new AvatarBean("avatar04"));
        avatarBeanList.add(new AvatarBean("avatar05"));
        avatarBeanList.add(new AvatarBean("avatar06"));
        avatarBeanList.add(new AvatarBean("avatar07"));
        avatarBeanList.add(new AvatarBean("avatar08"));
        avatarBeanList.add(new AvatarBean("avatar09"));
        avatarBeanList.add(new AvatarBean("avatar10"));
        avatarBeanList.add(new AvatarBean("avatar11"));
        avatarBeanList.add(new AvatarBean("avatar12"));
        avatarBeanList.add(new AvatarBean("avatar13"));
        avatarBeanList.add(new AvatarBean("avatar14"));
        avatarBeanList.add(new AvatarBean("avatar15"));
        avatarBeanList.add(new AvatarBean("avatar16"));
        avatarBeanList.add(new AvatarBean("avatar19"));
        avatarBeanList.add(new AvatarBean("default_avatar"));
        avatarBeanList.add(new AvatarBean("default_avatar2"));
        avatarBeanList.add(new AvatarBean("default_avatar3"));
//        for (int i = 0; i <= memberCount; i++) {
//            if (i == 0) {
//                avatarBeanList.add(new AvatarBean("avatar17"));
//                continue;
//            }
//            if (i == 16) {
//                avatarBeanList.add(new AvatarBean("avatar01"));
//                continue;
//            }
//
//            avatarBeanList.add(new AvatarBean(String.valueOf(i)));
//        }
        if (avatarBeanList.size() > 0) {
            repoCallback.onSuccess(avatarBeanList);
        } else {
            repoCallback.onFail("NO DATA");
        }
    }
}
