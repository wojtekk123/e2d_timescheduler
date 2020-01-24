package pl.codeconscept.e2d.timescheduler.service.queries;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;

@Component
@RequiredArgsConstructor
public class TemplateRestQueries {

    private final RestTemplate restTemplate;

    public UserId getInstructorByAuthId(String token, Long authId) throws NullPointerException {
        String uri = "http://e2dmasterdata:8080/ride/getInstructorByAuth/" + authId;
        return getUserId(token, uri);
    }

    public UserId getStudentByAuthId(String token, Long authId) throws NullPointerException {
        String uri = "http://e2dmasterdata:8080/ride/getStudentByAuth/" + authId;
        return getUserId(token, uri);
    }

    public UserId getStudentById(String token, Long id) throws NullPointerException {
        String uri = "http://e2dmasterdata:8080/ride/getStudentById/" + id;
        return getUserId(token, uri);
    }

    public UserId getInstructorById(String token, Long id) throws NullPointerException {
        String uri = "http://e2dmasterdata:8080/ride/getInstructorById/" + id;
        return getUserId(token, uri);
    }

    public UserId getSchoolByAuthI(String token, Long id) {
        String uri = "http://e2dmasterdata:8080/ride/getSchoolByAuthId/" + id;
        return getUserId(token, uri);

    }

    private UserId getUserId(String token, String uri) throws NullPointerException{
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(token);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserId> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<UserId> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, UserId.class);
        return responseEntity.getBody();
    }
}

