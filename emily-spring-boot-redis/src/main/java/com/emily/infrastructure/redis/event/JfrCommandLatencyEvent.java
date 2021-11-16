package com.emily.infrastructure.redis.event;

import io.lettuce.core.event.metrics.CommandLatencyEvent;
import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.StackTrace;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/11/09
 */
@Deprecated
@Category({ "Lettuce", "Command Events" })
@Label("Command Latency Trigger")
@StackTrace(false)
public class JfrCommandLatencyEvent extends Event {
    private final int size;

    public JfrCommandLatencyEvent(CommandLatencyEvent commandLatencyEvent) {
        this.size = commandLatencyEvent.getLatencies().size();
        commandLatencyEvent.getLatencies().forEach((commandLatencyId, commandMetrics) -> {
            JfrCommandLatency jfrCommandLatency = new JfrCommandLatency(commandLatencyId, commandMetrics);
            jfrCommandLatency.commit();
        });
    }
}