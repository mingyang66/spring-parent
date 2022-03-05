package com.emily.infrastructure.datasource.thread;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.datasource.DataSourceProperties;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;

/**
 * @Description :  数据源守护线程
 * @Author :  Emily
 * @CreateDate :  Created in 2022/2/19 3:53 下午
 */
public class DataSourceDaemonThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceDaemonThread.class);

    private DataSourceProperties properties;

    public DataSourceDaemonThread(String name, DataSourceProperties properties) {
        super(name);
        this.setDaemon(true);
        this.properties = properties;
    }

    @Override
    public void run() {
        while (true) {
            properties.getAllDataSource().values().stream().forEach(source -> {
               // logger.info(JSONUtils.toJSONString(source.getPoolingConnectionInfo()));
                //logger.info("activeCount:{}", source.getActiveCount());
            });
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
