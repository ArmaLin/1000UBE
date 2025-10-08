package com.dyaco.spirit_commercial.egym;

import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;

import java.util.Comparator;

public class EgymTrainerSort implements Comparator<EgymTrainingPlans.TrainerDTO> {
    public int compare(EgymTrainingPlans.TrainerDTO a, EgymTrainingPlans.TrainerDTO b) {
     //   return (int) (a.getStartTimestamp() - b.getStartTimestamp()); //時間數字小的在前面
        return (int) (b.getStartTimestamp() - a.getStartTimestamp()); //時間數字大的在前面 (比較新)
    }
}
