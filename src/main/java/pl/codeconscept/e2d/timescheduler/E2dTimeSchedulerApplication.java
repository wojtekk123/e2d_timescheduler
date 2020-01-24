package pl.codeconscept.e2d.timescheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.codeconscept.e2d.timescheduler.service.event.Events;


@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableBinding(Events.class)
public class E2dTimeSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(E2dTimeSchedulerApplication.class, args);
    }
}