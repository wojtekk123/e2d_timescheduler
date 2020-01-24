package pl.codeconscept.e2d.timescheduler.service.mail;

import lombok.Data;

import java.util.Map;

@Data
public class Mail {

    private String from;
    private String to;
    private String subject;
    private String content;
    private Map<String,Object> model;

}
