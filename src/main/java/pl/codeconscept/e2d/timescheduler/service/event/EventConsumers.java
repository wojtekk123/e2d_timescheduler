package pl.codeconscept.e2d.timescheduler.service.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface EventConsumers {

    String TIMESCHEDULER_NOTIFICATION_CONSUMER = "timeschedulerNotificationConsumer";

    @Input(TIMESCHEDULER_NOTIFICATION_CONSUMER)
    MessageChannel  timeschedulerConsumer();
}