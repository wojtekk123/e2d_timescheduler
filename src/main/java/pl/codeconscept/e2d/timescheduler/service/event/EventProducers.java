package pl.codeconscept.e2d.timescheduler.service.event;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EventProducers {

    String TIMESCHEDULER_NOTIFICATION_PRODUCER = "timeschedulerNotificationProducer";


    @Output( TIMESCHEDULER_NOTIFICATION_PRODUCER)
    MessageChannel timeschedulerNotificationProducer();

}