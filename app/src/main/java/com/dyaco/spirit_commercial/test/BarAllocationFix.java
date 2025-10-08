package com.dyaco.spirit_commercial.test;

import java.util.*;

public class BarAllocationFix {
    public static void main(String[] args) {
        // 測試案例 1
        int totalBars1 = 20;
        int totalTime1 = 480;
        List<Integer> durationTimesList1 = Arrays.asList(120, 360);
        List<Integer> setsTimePosition1 = allocateBars(totalBars1, totalTime1, durationTimesList1);
        System.out.println("Final setsTimePosition (Case 1): " + setsTimePosition1);

        // 測試案例 2
        int totalBars2 = 20;
        int totalTime2 = 25680;
        List<Integer> durationTimesList2 = Arrays.asList(5940, 5940, 5940, 5940, 660, 660, 600);
        List<Integer> setsTimePosition2 = allocateBars(totalBars2, totalTime2, durationTimesList2);
        System.out.println("Final setsTimePosition (Case 2): " + setsTimePosition2);
    }

    public static List<Integer> allocateBars(int totalBars, int totalTime, List<Integer> durationTimesList) {
        int n = durationTimesList.size();
        int[] bars = new int[n];          // 每個 set 分配的 bar 數
        double[] exact = new double[n];     // 精確分配值（浮點數）
        double[] fraction = new double[n];  // 小數部分

        // 以最大餘數法計算，每個 set 的初始分配為 floor(exact)，但保證至少 1 個 bar
        int sumBars = 0;
        for (int i = 0; i < n; i++) {
            exact[i] = (double) durationTimesList.get(i) / totalTime * totalBars;
            int base = (int) Math.floor(exact[i]);
            if (base < 1) {
                bars[i] = 1;
                fraction[i] = exact[i] - 1;  // 可能為負值
            } else {
                bars[i] = base;
                fraction[i] = exact[i] - base;
            }
            sumBars += bars[i];
        }

        // 若總和不等於 totalBars，依差額補足或扣除（但每個 set 至少保留 1 個 bar）
        int diff = totalBars - sumBars;
        if (diff > 0) {
            // 補足多餘的 bar，優先給小數部分較大的 set
            for (int r = 0; r < diff; r++) {
                int maxIndex = -1;
                double maxFraction = -Double.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (fraction[i] > maxFraction) {
                        maxFraction = fraction[i];
                        maxIndex = i;
                    }
                }
                bars[maxIndex]++;
                // 將該項的 fraction 設為極小值，避免再次被補足
                fraction[maxIndex] = -Double.MAX_VALUE;
            }
        } else if (diff < 0) {
            // 若分配過多，從小數部分最小的 set 扣除，但不低於 1 個 bar
            for (int r = 0; r < -diff; r++) {
                int minIndex = -1;
                double minFraction = Double.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (bars[i] > 1 && fraction[i] < minFraction) {
                        minFraction = fraction[i];
                        minIndex = i;
                    }
                }
                if (minIndex != -1) {
                    bars[minIndex]--;
                    fraction[minIndex] = Double.MAX_VALUE;
                }
            }
        }

        // Debug：印出每個 set 分配的 bar 數
        System.out.println("Adjusted bars allocation: " + Arrays.toString(bars));

        // 根據 bars 分配計算 setsTimePosition：每個 set 的結束 bar index 為累計值 - 1
        List<Integer> setsTimePosition = new ArrayList<>();
        int cumulative = 0;
        for (int i = 0; i < n; i++) {
            cumulative += bars[i];
            int pos = cumulative - 1;
            // 確保不超過 totalBars - 1（例如 index 0~19）
            pos = Math.min(pos, totalBars - 1);
            setsTimePosition.add(pos);
        }

        // 再次檢查確保結果嚴格遞增（理論上每個 bars[i] >= 1 時已保證）
        for (int i = 1; i < setsTimePosition.size(); i++) {
            if (setsTimePosition.get(i) <= setsTimePosition.get(i - 1)) {
                setsTimePosition.set(i, setsTimePosition.get(i - 1) + 1);
                if (setsTimePosition.get(i) > totalBars - 1) {
                    setsTimePosition.set(i, totalBars - 1);
                }
            }
        }

        System.out.println("Final setsTimePosition: " + setsTimePosition);
        return setsTimePosition;
    }
}
