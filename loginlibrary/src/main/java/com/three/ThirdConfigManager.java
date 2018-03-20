package com.three;


/**
 *
 * @author onetouch
 * @date 2017/11/21
 */

public class ThirdConfigManager {
    private static boolean isInit = false;
    public static ConfigAppKey CONFIG;
    public static void init(ConfigAppKey config) {
        isInit = true;
        CONFIG = config;
    }
}
