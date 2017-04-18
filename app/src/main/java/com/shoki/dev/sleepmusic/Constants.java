package com.shoki.dev.sleepmusic;

import android.support.annotation.StringRes;

/**
 * Created by shoki on 2017. 4. 12..
 */

public class Constants {

    public static final String DEFAULT = "com.shoki.dev.sleepmusic";
    public interface ACTION {
        public static final String SERVICE_START_ACTION  = DEFAULT + ".start";
        public static final String SERVICE_STOP_ACTION = DEFAULT + ".stop";
        public static final String SERVICE_KILL_ACTION = DEFAULT + ".kill";
        public static final String SERVICE_RESTART_ACTION = DEFAULT + ".restart";
    }
}
