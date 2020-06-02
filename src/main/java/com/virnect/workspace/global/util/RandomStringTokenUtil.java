package com.virnect.workspace.global.util;

import com.virnect.workspace.global.constant.UUIDType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class RandomStringTokenUtil {
    public static String generate(UUIDType type, int digit) {
        switch (type) {
            case UUID_WITH_SEQUENCE: {
                return RandomStringUtils.randomAlphanumeric(13);
            }
            case PIN_NUMBER: {
                Random random = new Random(System.currentTimeMillis());

                int range = (int)Math.pow(10,6);
                int trim = (int)Math.pow(10, 5);
                int result = random.nextInt(range)+trim;

                if(result>range){
                    result = result - trim;
                }

                return String.valueOf(result);
            }
            case INVITE_CODE: {
                return RandomStringUtils.randomAlphanumeric(digit).toUpperCase();
            }
        }
        return UUID.randomUUID().toString();
    }
}
