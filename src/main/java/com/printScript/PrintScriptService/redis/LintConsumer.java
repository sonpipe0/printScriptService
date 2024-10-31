package com.printScript.PrintScriptService.redis;

import java.time.Duration;

import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
public class LintConsumer extends RedisStreamConsumer<String> {

    private static final Logger logger = LoggerFactory.getLogger(LintConsumer.class);

    public LintConsumer(RedisTemplate<String, String> redis, @Value("${stream.redis.stream.lint.key}") String streamKey,
            @Value("${stream.redis.consumer.group}") String consumerGroup) {
        super(streamKey, consumerGroup, redis);
    }

    @Override
    protected void onMessage(@NotNull ObjectRecord<String, String> objectRecord) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Received message: {}", objectRecord.getValue());
    }

    @NotNull
    @Override
    protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
        return StreamReceiver.StreamReceiverOptions.builder().pollTimeout(Duration.ofSeconds(5))
                .targetType(String.class).build();
    }
}
