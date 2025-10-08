package com.dyaco.spirit_commercial.support;

public class RpmUtil {

    private static final int RESET_VALUE = 65536;
    private static int previous = RESET_VALUE;
    private static int count = 0;

    /**
     * Reset when workout started
     */
    public static void reset() {
        previous = RESET_VALUE;
        count = 0;
    }

    /**
     * Get Stride count for cross trainer
     * @param rpmCount value from callback onMcuControl -> rpmCounter value
     * @return
     */
    public static int getStrideCount(int rpmCount) {
        if (previous == RESET_VALUE) {
            previous = rpmCount;
        }

        int delta = rpmCount - previous;

        previous = rpmCount;

        if (delta < 0) {
            delta += RESET_VALUE;
        }

        return count += delta;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i += 3) {
            int rpmCounter = i + 65520;

            if (rpmCounter > 65535) {
                rpmCounter -= RESET_VALUE;
            }

            int count = getStrideCount(rpmCounter);

            String result = String.format("rpm counter: %5d, count: %d", rpmCounter, count);

            System.out.println(result);
        }
    }
}
