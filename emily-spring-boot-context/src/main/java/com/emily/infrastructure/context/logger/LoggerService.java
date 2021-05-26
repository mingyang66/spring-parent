package com.emily.infrastructure.context.logger;

import com.emily.infrastructure.common.base.BaseLogger;

public interface LoggerService {
    /**
     * @Description 记录响应信息
     * @Version 1.0
     */
    void traceResponse(BaseLogger baseLogger);
}
