package com.logback.test;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.factory.LogbackPropertiesValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogbackPropertiesValidatorTest {

    @Test
    void shouldAcceptDefaultProperties() {
        Assertions.assertDoesNotThrow(() -> LogbackPropertiesValidator.validate(new LogbackProperties()));
    }

    @Test
    void shouldRejectMissingLevelAndPattern() {
        LogbackProperties missingLevel = new LogbackProperties();
        missingLevel.getRoot().setLevel(null);
        assertInvalid(missingLevel, "spring.emily.logger.root.level");

        LogbackProperties blankPattern = new LogbackProperties();
        blankPattern.getGroup().setPattern(" ");
        assertInvalid(blankPattern, "spring.emily.logger.group.pattern");
    }

    @Test
    void shouldRejectInvalidAppenderConfiguration() {
        LogbackProperties blankPath = new LogbackProperties();
        blankPath.getAppender().setPath(" ");
        assertInvalid(blankPath, "spring.emily.logger.appender.path");

        LogbackProperties missingPolicy = new LogbackProperties();
        missingPolicy.getAppender().setRollingPolicyType(null);
        assertInvalid(missingPolicy, "spring.emily.logger.appender.rolling-policy-type");
    }

    @Test
    void shouldRejectInvalidRollingPolicyConfiguration() {
        LogbackProperties invalidSize = new LogbackProperties();
        invalidSize.getAppender().getSizeTimeRollingPolicy().setMaxFileSize("invalid");
        assertInvalid(invalidSize, "spring.emily.logger.appender.size-time-rolling-policy.max-file-size");

        LogbackProperties negativeHistory = new LogbackProperties();
        negativeHistory.getAppender().getTimeRollingPolicy().setMaxHistory(-1);
        assertInvalid(negativeHistory, "spring.emily.logger.appender.time-rolling-policy.max-history");

        LogbackProperties totalSmallerThanFile = new LogbackProperties();
        totalSmallerThanFile.getAppender().getSizeTimeRollingPolicy().setMaxFileSize("2GB");
        totalSmallerThanFile.getAppender().getSizeTimeRollingPolicy().setTotalSizeCap("1GB");
        assertInvalid(totalSmallerThanFile, "spring.emily.logger.appender.size-time-rolling-policy.total-size-cap");
    }

    @Test
    void shouldRejectInvalidAsyncConfiguration() {
        LogbackProperties invalidQueue = new LogbackProperties();
        invalidQueue.getAsync().setQueueSize(0);
        assertInvalid(invalidQueue, "spring.emily.logger.async.queue-size");

        LogbackProperties invalidThreshold = new LogbackProperties();
        invalidThreshold.getAsync().setDiscardingThreshold(invalidThreshold.getAsync().getQueueSize() + 1);
        assertInvalid(invalidThreshold, "spring.emily.logger.async.discarding-threshold");
    }

    private void assertInvalid(LogbackProperties properties, String propertyName) {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> LogbackPropertiesValidator.validate(properties));
        Assertions.assertTrue(exception.getMessage().contains(propertyName), exception.getMessage());
    }
}
