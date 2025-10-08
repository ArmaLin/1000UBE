package com.dyaco.spirit_commercial.model.webapi.bean;

import java.util.Comparator;

public class GymSort implements Comparator<GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO> {
    public int compare(GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO a, GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO b) {
        return a.getRanking() - b.getRanking();
    }
}