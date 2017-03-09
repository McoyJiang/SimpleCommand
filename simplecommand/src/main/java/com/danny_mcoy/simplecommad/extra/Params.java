package com.danny_mcoy.simplecommad.extra;

/**
 * Created by Danny_姜新星 on 3/8/2017.
 */

public class Params {

    private static final String NAMESPACE = Params.class.getName();

    public static class Intent {

        private Intent() {
            // hidden
        }

        public final static String EXTRA_COMMAND = NAMESPACE.concat(".EXTRA_COMMAND");
        public final static String EXTRA_RECEIVER = NAMESPACE.concat(".EXTRA_RECEIVER");
    }

    public static class CommandMessage {

        private CommandMessage() {
            // hidden
        }

        public static final String EXTRA_BODY = NAMESPACE.concat(".EXTRA_BODY");
        public final static String EXTRA_CMD = NAMESPACE.concat(".EXTRA_CMD");
        public final static String EXTRA_STATUS = NAMESPACE.concat(".EXTRA_STATUS");
        public final static String EXTRA_STATUS_CODE = NAMESPACE.concat(".EXTRA_STATUS_CODE");
        public final static String EXTRA_PROGRESS_CODE = NAMESPACE.concat(".EXTRA_PROGRESS_CODE");
        public final static String EXTRA_MESSAGE = NAMESPACE.concat(".EXTRA_MESSAGE");
    }

    public static class Command {
        private Command() {
            // hidden
        }

        public static class StatusCode {
            private StatusCode() {
                // hidden
            }

            public final static int CODE_NEW_VERSION = 0;
            public final static int CODE_NO_NETWORK = 10;
            public final static int CODE_NO_FREE_SPACE = 20;
            public final static int CODE_NO_SUPPORTED_SYSTEM = 30;
            public final static int CODE_CELL_NETWORK_UPLOAD = 40;
            public final static int CODE_EXPIRED_TOKEN = 400;
            public final static int CODE_BAD_USERNAME_PASSWORD = 401;
        }
    }

    /**
     * 在Storage中使用到此类中的常量
     */
    public static class Persistent {
        private Persistent() {
            // hidden
        }

        public final static String PARAM_TOKEN = ".PARAM_TOKEN";
        public final static String PARAM_MEDIA_SYNC = ".PARAM_MEDIA_SYNC";
        public final static String PARAM_SCHOOL_CODE = ".PARAM_SCHOOL_CODE";
        public final static String PARAM_ACCOUNT = ".PARAM_ACCOUNT";
        public final static String PARAM_FIRST_LAUNCH = ".PARAM_FIRST_LAUNCH";
        public final static String PARAM_SHOW_WARNING = ".PARAM_SHOW_WARNING";
        public final static String PARAM_LOCATION =  ".PARAM_LOCATION";
        public final static String PARAM_SHOWCASE =  ".PARAM_SHOWCASE";
        public final static String PARAM_DEVICE_ID = ".PARAM_DEVICE_ID";

        public static class MediaMetadata {
            public final static String TAG = ".TAG";
            public final static String GROUP_ID = ".GROUP_ID";
            public final static String CREATOR_NAME = ".CREATOR_NAME";
        }

    }
}
