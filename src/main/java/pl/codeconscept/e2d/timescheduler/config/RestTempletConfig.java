package pl.codeconscept.e2d.timescheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTempletConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


}
