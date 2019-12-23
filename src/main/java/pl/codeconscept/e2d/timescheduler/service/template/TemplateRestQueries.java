package pl.codeconscept.e2d.timescheduler.service.template;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;

import java.rmi.ConnectIOException;

@Component
@RequiredArgsConstructor
public class TemplateRestQueries {

    private final RestTemplate restTemplate;

    public UserId getInstructorId(String token, Long authId) throws NullPointerException, ConnectIOException {
        String uri = "http://localhost:8080/ride/getInstructorByAuth/" + authId;
        return getUserId(token, uri);
    }

    public UserId getStudentId(String token, Long authId) throws NullPointerException, ConnectIOException {
        String uri = "http://localhost:8080/ride/getStudentByAuth/" + authId;
        return getUserId(token, uri);
    }

    public UserId getStudent(String token, Long id) throws NullPointerException, ConnectIOException {
        String uri = "http://localhost:8080/ride/getStudentById/" + id;
        return getUserId(token, uri);
    }

    private UserId getUserId(String token, String uri) throws ConnectIOException, NullPointerException{
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(token);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserId> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<UserId> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, UserId.class);
        return responseEntity.getBody();
    }
}

