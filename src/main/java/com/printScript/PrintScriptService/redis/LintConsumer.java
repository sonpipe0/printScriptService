package com.printScript.PrintScriptService.redis;

import java.time.Duration;

import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.services.RunnerService;
import com.printScript.PrintScriptService.web.BucketRequestExecutor;

import events.ConfigPublishEvent;
import events.StatusPublishEvent;

@Component
public class LintConsumer extends RedisStreamConsumer<ConfigPublishEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LintConsumer.class);

    @Autowired
    RunnerService runnerService;

    @Autowired
    BucketRequestExecutor bucketRequestExecutor;

    @Autowired
    StatusProducerInterface statusProducer;

    public LintConsumer(RedisTemplate<String, String> redis, @Value("${stream.redis.stream.lint.key}") String streamKey,
            @Value("${stream.redis.consumer.group}") String consumerGroup) {
        super(streamKey, consumerGroup, redis);
    }

    @Override
    protected synchronized void onMessage(@NotNull ObjectRecord<String, ConfigPublishEvent> objectRecord) {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Response<String> code;
        try {
            code = bucketRequestExecutor.get("snippets/" + objectRecord.getValue().getSnippetId(), "");
        } catch (Exception e) {
            logger.error("Error getting snippet code", e);
            return;
        }
        boolean hasErrors = true;
        switch (objectRecord.getValue().getType()) {
            case FORMAT -> {
                Response<Void> formatFile = runnerService.formatFile(code.getData(), "1.1",
                        objectRecord.getValue().getUserId(), objectRecord.getValue().getSnippetId());
                hasErrors = formatFile.isError();
            }
            case LINT -> {
                Response<Void> getLintingErrors = runnerService.getLintingErrors(code.getData(), "1.1",
                        objectRecord.getValue().getUserId());
                hasErrors = getLintingErrors.isError();
            }
        }
        StatusPublishEvent statusPublishEvent = new StatusPublishEvent();
        statusPublishEvent.setSnippetId(objectRecord.getValue().getSnippetId());
        statusPublishEvent.setUserId(objectRecord.getValue().getUserId());
        statusPublishEvent.setStatus(
                hasErrors ? StatusPublishEvent.StatusType.NON_COMPLIANT : StatusPublishEvent.StatusType.COMPLIANT);
        statusPublishEvent.setType(objectRecord.getValue().getType());
        statusProducer.publishEvent(statusPublishEvent);
        logger.info("Published status event: {}", statusPublishEvent);
    }

    @NotNull
    @Override
    protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ConfigPublishEvent>> options() {
        return StreamReceiver.StreamReceiverOptions.builder().pollTimeout(Duration.ofSeconds(5))
                .targetType(ConfigPublishEvent.class).build();
    }
}
