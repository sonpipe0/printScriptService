package com.printScript.PrintScriptService.redis;

import events.StatusPublishEvent;

public interface StatusProducerInterface {
    void publishEvent(StatusPublishEvent event);
}
