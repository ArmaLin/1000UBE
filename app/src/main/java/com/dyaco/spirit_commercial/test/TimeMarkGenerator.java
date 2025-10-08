package com.dyaco.spirit_commercial.test;

import java.util.Arrays;

public class TimeMarkGenerator {
    public static void main(String[] args) {
        // 總秒數 1263 秒
        int totalSeconds = 1263;
        // 輸出陣列長度，例如 5 個時間點
        int segments = 5;

        String[] timeMarks = generateTimeMarks(totalSeconds, segments);
        System.out.println("時間標記：" + Arrays.toString(timeMarks));
    }

    /**
     * 根據總秒數與指定的段數平均分配時間標記
     * 第一個固定為 "0:00"，最後一個為總秒數轉換的時間
     *
     * @param totalSeconds 總秒數
     * @param segments     陣列的長度（時間點數量）
     * @return 時間標記的字串陣列
     */
    public static String[] generateTimeMarks(int totalSeconds, int segments) {
        String[] result = new String[segments];
        // 平均間隔 = 總秒數除以 (segments - 1)
        double step = (double) totalSeconds / (segments - 1);

        for (int i = 0; i < segments; i++) {
            // 每個位置的秒數以四捨五入取整
            int seconds = (int) Math.round(i * step);
            result[i] = formatSeconds(seconds);
        }
        return result;
    }

    /**
     * 將秒數格式化為 "m:ss" 的格式
     *
     * @param seconds 要格式化的秒數
     * @return 格式化後的時間字串
     */
    private static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        int sec = seconds % 60;
        return minutes + ":" + (sec < 10 ? "0" : "") + sec;
    }
}
