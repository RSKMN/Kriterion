package com.kriterion.event;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class KriterionEvent extends ApplicationEvent {
    private final Long userId;
    private final LocalDateTime eventTimestamp;

    protected KriterionEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
        this.eventTimestamp = LocalDateTime.now();
    }
}
