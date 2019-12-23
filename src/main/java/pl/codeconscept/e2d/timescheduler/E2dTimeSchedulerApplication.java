package pl.codeconscept.e2d.timescheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableEurekaClient
public class E2dTimeSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(E2dTimeSchedulerApplication.class, args);
    }
}
