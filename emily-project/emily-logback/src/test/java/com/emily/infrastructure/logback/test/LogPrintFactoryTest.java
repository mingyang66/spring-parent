package com.emily.infrastructure.logback.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.factory.LogPrintFactory;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.concurrent.atomic.AtomicInteger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogPrintFactoryTest {
    private Logger requestLogger;
    private Logger thirdPartyLogger;
    private Logger platformLogger;
    private ListAppender<ILoggingEvent> requestAppender;
    private ListAppender<ILoggingEvent> thirdPartyAppender;
    private ListAppender<ILoggingEvent> platformAppender;

    @BeforeAll
    void setUp() {
        LogbackProperties properties = new LogbackProperties();
        properties.getAppender().setPath("target/log-print-factory-test");
        properties.getAsync().setEnabled(false);
        properties.getModule().setConsole(false);
        LogbackContextInitializer.initialize(properties);

        requestLogger = (Logger) LoggerFactory.getModuleLogger(LogPrintFactory.class, "request", "request");
        thirdPartyLogger = (Logger) LoggerFactory.getModuleLogger(LogPrintFactory.class, "thirdParty", "thirdParty");
        platformLogger = (Logger) LoggerFactory.getModuleLogger(LogPrintFactory.class, "platform", "platform");
        requestAppender = attachListAppender(requestLogger, "request-test");
        thirdPartyAppender = attachListAppender(thirdPartyLogger, "third-party-test");
        platformAppender = attachListAppender(platformLogger, "platform-test");
    }

    @AfterAll
    void tearDown() {
        LogbackContextInitializer.stopAndReset();
    }

    @BeforeEach
    void resetLoggers() {
        requestLogger.setLevel(Level.INFO);
        thirdPartyLogger.setLevel(Level.INFO);
        platformLogger.setLevel(Level.INFO);
        requestAppender.list.clear();
        thirdPartyAppender.list.clear();
        platformAppender.list.clear();
    }

    @Test
    void shouldRouteStringMessagesToDedicatedLoggers() {
        LogPrintFactory.printRequest("request-message");
        LogPrintFactory.printThirdParty("third-party-message");
        LogPrintFactory.printPlatform("platform-message");

        assertSingleMessage(requestAppender, "request-message");
        assertSingleMessage(thirdPartyAppender, "third-party-message");
        assertSingleMessage(platformAppender, "platform-message");
    }

    @Test
    void shouldEvaluateSuppliersWhenInfoLoggingIsEnabled() {
        LogPrintFactory.printRequest(() -> "request-supplier");
        LogPrintFactory.printThirdParty(() -> "third-party-supplier");
        LogPrintFactory.printPlatform(() -> "platform-supplier");

        Assertions.assertEquals("request-supplier", lastMessage(requestAppender));
        Assertions.assertEquals("third-party-supplier", lastMessage(thirdPartyAppender));
        Assertions.assertEquals("platform-supplier", lastMessage(platformAppender));
    }

    @Test
    void shouldNotEvaluateSuppliersWhenInfoLoggingIsDisabled() {
        AtomicInteger invocationCount = new AtomicInteger();
        requestLogger.setLevel(Level.ERROR);
        thirdPartyLogger.setLevel(Level.ERROR);
        platformLogger.setLevel(Level.ERROR);

        LogPrintFactory.printRequest(() -> message(invocationCount));
        LogPrintFactory.printThirdParty(() -> message(invocationCount));
        LogPrintFactory.printPlatform(() -> message(invocationCount));

        Assertions.assertEquals(0, invocationCount.get());
    }

    private ListAppender<ILoggingEvent> attachListAppender(Logger logger, String name) {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(logger.getLoggerContext());
        appender.setName(name);
        appender.start();
        logger.addAppender(appender);
        return appender;
    }

    private void assertSingleMessage(ListAppender<ILoggingEvent> appender, String expected) {
        Assertions.assertEquals(1, appender.list.size());
        Assertions.assertEquals(expected, lastMessage(appender));
    }

    private String lastMessage(ListAppender<ILoggingEvent> appender) {
        return appender.list.getLast().getFormattedMessage();
    }

    private String message(AtomicInteger invocationCount) {
        invocationCount.incrementAndGet();
        return "unused";
    }
}
