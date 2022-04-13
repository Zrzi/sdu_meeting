package com.meeting.common.util;

import java.util.Random;

public class DigitUtil {

    public int code(int length) {
        if (length <= 0) {
            return 0;
        }
        Random random = new Random();
        int temp = random.nextInt(8) + 1;
        for (int i=0; i<length-1; ++i) {
            temp = temp * 10 + random.nextInt(9);
        }
        return temp;
    }

}
