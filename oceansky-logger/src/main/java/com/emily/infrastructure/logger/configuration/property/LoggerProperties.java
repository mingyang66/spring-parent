package com.emily.infrastructure.logger.configuration.property;


import com.emily.infrastructure.logger.common.CompressionMode;
import com.emily.infrastructure.logger.configuration.type.LevelType;
import com.emily.infrastructure.logger.configuration.type.RollingPolicyType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emily
 * @description: 日志配置属性
 * @create: 2020/08/08
 */
public class LoggerProperties {
    /**
     * 是否开启日志组件，默认：true
     */
    private boolean enabled = true;
    /**
     * 是否开启debug模式，默认：false
     */
    private boolean debug = false;
    /**
     * 发生异常打印异常堆栈时是否将包信息追加到每行末尾，默认：true
     */
    private boolean packagingData = true;
    /**
     * 全局过滤器日志标记控制
     */
    private Marker marker = new Marker();
    /**
     * 基础根日志
     */
    private Root root = new Root();
    /**
     * 分组记录日志
     */
    private Group group = new Group();
    /**
     * 按模块记录日志
     */
    private Module module = new Module();
    /**
     * appender配置
     */
    private Appender appender = new Appender();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isPackagingData() {
        return packagingData;
    }

    public void setPackagingData(boolean packagingData) {
        this.packagingData = packagingData;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Appender getAppender() {
        return appender;
    }

    public void setAppender(Appender appender) {
        this.appender = appender;
    }

    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    /**
     * 基础日志
     */
    public static class Root {
        /**
         * 是否将日志信息输出到控制台，默认：true
         */
        private boolean console = true;
        /**
         * 基础日志文件路径，相对
         */
        private String filePath = "base";
        /**
         * 日志级别，OFF > ERROR > WARN > INFO > DEBUG >TRACE > ALL, 默认：DEBUG
         */
        private LevelType level = LevelType.INFO;
        /**
         * 记录文件格式-不带颜色
         */
        private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n";
        /**
         * 打印控制台格式-带颜色
         * 可以打印当前类名格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%p (%file:%line\\)] : %msg%n
         * 通用日志输出格式：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
         */
        private String consolePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %cn --- [%18.18thread] %cyan(%-36.36logger{36}:%-4.4line) : %msg %n";

        public LevelType getLevel() {
            return level;
        }

        public void setLevel(LevelType level) {
            this.level = level;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getConsolePattern() {
            return consolePattern;
        }

        public void setConsolePattern(String consolePattern) {
            this.consolePattern = consolePattern;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public boolean isConsole() {
            return console;
        }

        public void setConsole(boolean console) {
            this.console = console;
        }
    }

    public static class Group {
        /**
         * 是否将模块日志信息输出到控制台，默认false
         */
        private boolean console = false;
        /**
         * 模块输出的日志级别，ERROR > WARN > INFO > DEBUG >TRACE, 默认：DEBUG
         */
        private LevelType level = LevelType.INFO;
        /**
         * 模块日志输出格式，默认：%msg%n
         */
        private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n";

        public LevelType getLevel() {
            return level;
        }

        public void setLevel(LevelType level) {
            this.level = level;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public boolean isConsole() {
            return console;
        }

        public void setConsole(boolean console) {
            this.console = console;
        }
    }

    public static class Module {
        /**
         * 是否将模块日志信息输出到控制台，默认：false
         */
        private boolean console = false;
        /**
         * 模块输出的日志级别，ERROR > WARN > INFO > DEBUG >TRACE, 默认：DEBUG
         */
        private LevelType level = LevelType.INFO;
        /**
         * 模块日志输出格式，默认：%msg%n
         */
        private String pattern = "%msg%n";

        public LevelType getLevel() {
            return level;
        }

        public void setLevel(LevelType level) {
            this.level = level;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public boolean isConsole() {
            return console;
        }

        public void setConsole(boolean console) {
            this.console = console;
        }
    }

    public static class Async {
        /**
         * 是否启用日志异步记录Appender
         */
        private boolean enabled;
        /**
         * 队列的最大容量，默认为 256
         */
        private int queueSize = 256;
        /**
         * 默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
         */
        private int discardingThreshold;
        /**
         * 根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
         * 当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
         * 使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
         * 默认是 1000毫秒
         */
        private int maxFlushTime = 1000;
        /**
         * 在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
         */
        private boolean neverBlock;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }

        public int getDiscardingThreshold() {
            return discardingThreshold;
        }

        public void setDiscardingThreshold(int discardingThreshold) {
            this.discardingThreshold = discardingThreshold;
        }

        public int getMaxFlushTime() {
            return maxFlushTime;
        }

        public void setMaxFlushTime(int maxFlushTime) {
            this.maxFlushTime = maxFlushTime;
        }

        public boolean isNeverBlock() {
            return neverBlock;
        }

        public void setNeverBlock(boolean neverBlock) {
            this.neverBlock = neverBlock;
        }
    }

    /**
     * 文件归档策略
     */
    public static class RollingPolicy {
        /**
         * 是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
         * 默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
         */
        private RollingPolicyType type = RollingPolicyType.TIME_BASE;
        /**
         * 是否在应用程序启动时删除存档，默认：false
         * 是否在应用启动的时候删除历史日志。
         * 如果设置为真，将在启动应用程序时执行档案删除。默认情况下，此属性设置为 false。归档日志移除通常在滚动期间执行。
         * 但是，有些应用程序的存活时间可能不够长，无法触发滚动。因此，对于如此短命的应用程序，删除存档可能永远没有机会执行。
         * 通过将 cleanHistoryOnStart 设置为 true，将在启动 appender 时执行档案删除。
         */
        private boolean cleanHistoryOnStart = false;
        /**
         * 设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
         */
        private int maxHistory = 7;
        /**
         * 单个日志文件最大文件大小 KB、MB、GB，默认500MB
         */
        private String maxFileSize = "500MB";
        /**
         * 控制所有归档文件总大小 KB、MB、GB，默认5GB
         */
        private String totalSizeCap = "5GB";
        /**
         * 压缩模式，默认：zip
         * .gz  1/5  10KB压缩后2KB
         * .zip  2/11 11KB压缩后2KB
         */
        private CompressionMode compressionMode = CompressionMode.ZIP;

        public CompressionMode getCompressionMode() {
            return compressionMode;
        }

        public void setCompressionMode(CompressionMode compressionMode) {
            this.compressionMode = compressionMode;
        }

        public RollingPolicyType getType() {
            return type;
        }

        public void setType(RollingPolicyType type) {
            this.type = type;
        }

        public boolean isCleanHistoryOnStart() {
            return cleanHistoryOnStart;
        }

        public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
            this.cleanHistoryOnStart = cleanHistoryOnStart;
        }

        public int getMaxHistory() {
            return maxHistory;
        }

        public void setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
        }

        public String getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(String maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String getTotalSizeCap() {
            return totalSizeCap;
        }

        public void setTotalSizeCap(String totalSizeCap) {
            this.totalSizeCap = totalSizeCap;
        }
    }

    public static class Appender {
        /**
         * 日志文件存放路径，默认是:./logs
         */
        private String path = "./logs";
        /**
         * 如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
         */
        private boolean append = true;
        /**
         * 如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
         */
        private boolean prudent = false;
        /**
         * 设置是否将输出流刷新，确保日志信息不丢失，默认：true
         */
        private boolean immediateFlush = true;
        /**
         * 文件归档策略
         */
        private RollingPolicy rollingPolicy = new RollingPolicy();
        /**
         * 异步日志配置
         */
        private Async async = new Async();

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isAppend() {
            return append;
        }

        public void setAppend(boolean append) {
            this.append = append;
        }

        public boolean isPrudent() {
            return prudent;
        }

        public void setPrudent(boolean prudent) {
            this.prudent = prudent;
        }

        public boolean isImmediateFlush() {
            return immediateFlush;
        }

        public void setImmediateFlush(boolean immediateFlush) {
            this.immediateFlush = immediateFlush;
        }

        public RollingPolicy getRollingPolicy() {
            return rollingPolicy;
        }

        public void setRollingPolicy(RollingPolicy rollingPolicy) {
            this.rollingPolicy = rollingPolicy;
        }

        public Async getAsync() {
            return async;
        }

        public void setAsync(Async async) {
            this.async = async;
        }
    }

    /**
     * 标记全局过滤器控制
     */
    public static class Marker {
        /**
         * 接受指定标记的日志记录到文件中
         */
        private List<String> acceptMarker = new ArrayList<>();
        /**
         * 拒绝标记的日志记录到文件中
         */
        private List<String> denyMarker = new ArrayList<>();

        public List<String> getAcceptMarker() {
            return acceptMarker;
        }

        public void setAcceptMarker(List<String> acceptMarker) {
            this.acceptMarker = acceptMarker;
        }

        public List<String> getDenyMarker() {
            return denyMarker;
        }

        public void setDenyMarker(List<String> denyMarker) {
            this.denyMarker = denyMarker;
        }
    }
}
