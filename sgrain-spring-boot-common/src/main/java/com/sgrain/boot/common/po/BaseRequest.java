package com.sgrain.boot.common.po;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Description: 基类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class BaseRequest implements Serializable {
    /**
     * 系统日志
     */
    private SystemInfo systemInfo;

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public static class SystemInfo{
        /**
         * 事务编号
         */
        private String tansactionId = UUID.randomUUID().toString();
        /**
         * 客户端IP
         */
        private String clientIp;
        /**
         * 服务端IP
         */
        private String serverIp;
        /**
         * 用户ID
         */
        private String accountId;
        /**
         * 平台H5 APP PC
         */
        private String platorm;

        public String getTansactionId() {
            return tansactionId;
        }

        public void setTansactionId(String tansactionId) {
            this.tansactionId = tansactionId;
        }

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getPlatorm() {
            return platorm;
        }

        public void setPlatorm(String platorm) {
            this.platorm = platorm;
        }
    }


}
