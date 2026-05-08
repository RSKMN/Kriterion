package com.kriterion.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KriterionEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(KriterionEvent event) {
        log.info("Publishing event: {} for user: {}", event.getClass().getSimpleName(), event.getUserId());
        applicationEventPublisher.publishEvent(event);
    }
}
