package pl.codeconscept.e2d.timescheduler.service.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;

import java.rmi.ConnectIOException;

@Component
public class  TemplateRestQueries  {

    @Autowired
    RestTemplate restTemplate;

    public Long getInstructorId(String token, Long authId) throws NullPointerException, ConnectIOException {

        String uri = "http://localhost:8080/ride/getInstructorId/" + authId;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(token);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserId> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<UserId> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, UserId.class);
        return responseEntity.getBody().getId();

    }

    public Long getStudentId(String token, Long authId) throws NullPointerException, ConnectIOException {

        String uri = "http://localhost:8080/ride/getStudentId/" + authId;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(token);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserId> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<UserId> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, UserId.class);
        return responseEntity.getBody().getId();

    }
}

