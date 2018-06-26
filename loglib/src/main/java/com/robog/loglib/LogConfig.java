package com.robog.loglib;

/**
 * Created by yuxingdong on 2018/6/25.
 */
public final class LogConfig {

    private final boolean mDebug;
    private final String mDeviceInfo;
    private final int mDbThreshold;
    private final int mCacheSize;
    private final int mLogMode;
    private final int mServiceCheckInterval;
    private final int mDbCheckInterval;

    public LogConfig(Builder builder) {
        mDebug = builder.debug;
        mDeviceInfo = builder.deviceInfo;
        mDbThreshold = builder.dbThreshold;
        mCacheSize = builder.cacheSize;
        mLogMode = builder.logMode;
        mServiceCheckInterval = builder.serviceCheckInterval;
        mDbCheckInterval = builder.dbCheckInterval;
    }

    public boolean debug() {
        return mDebug;
    }

    public String deviceInfo() {
        return mDeviceInfo;
    }

    public int dbThreshold() {
        return mDbThreshold;
    }

    public int cacheSize() {
        return mCacheSize;
    }

    public int logMode() {
        return mLogMode;
    }

    public int serviceCheckInterval() {
        return mServiceCheckInterval;
    }

    public int dbCheckInterval() {
        return mDbCheckInterval;
    }

    public static final class Builder {

        /**
         * 以下数据默认值都是瞎定的
         */
        private static final int DB_THRESHOLD = 1000;
        private static final int MAX_CACHE_SIZE = 50;
        private static final int SERVICE_CHECK_INTERVAL = 60 * 60 * 1000;
        private static final int DB_CHECK_INTERVAL = 60 * 60 * 1000;

        boolean debug;
        String deviceInfo;
        int dbThreshold = DB_THRESHOLD;
        int cacheSize = MAX_CACHE_SIZE;
        int logMode = LogMode.DATABASE;
        int serviceCheckInterval = SERVICE_CHECK_INTERVAL;
        int dbCheckInterval = DB_CHECK_INTERVAL;

        /**
         * 是否调试模式
         *
         * @param debug
         * @return
         */
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * 设备标识
         *
         * @param deviceInfo
         * @return
         */
        public Builder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        /**
         * 数据库容量阈值
         *
         * @param dbThreshold
         * @return
         */
        public Builder dbThreshold(int dbThreshold) {
            this.dbThreshold = dbThreshold;
            return this;
        }

        /**
         * 缓存大小
         *
         * @param cacheSize
         * @return
         */
        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        /**
         * Log存储模式
         *
         * @param logMode
         * @return
         */
        public Builder logMode(@LogMode.Check int logMode) {
            this.logMode = logMode;
            return this;
        }

        /**
         * 向服务器发送数据周期
         *
         * @param serviceCheckInterval
         * @return
         */
        public Builder serviceCheckInterval(int serviceCheckInterval) {
            this.serviceCheckInterval = serviceCheckInterval;
            return this;
        }

        /**
         * 数据库检查周期
         *
         * @param dbCheckInterval
         * @return
         */
        public Builder dbCheckInterval(int dbCheckInterval) {
            this.dbCheckInterval = dbCheckInterval;
            return this;
        }

        public LogConfig build() {
            return new LogConfig(this);
        }
    }
}
