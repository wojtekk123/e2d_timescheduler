package pl.codeconscept.e2d.timescheduler.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class E2DIllegalArgument extends RuntimeException {

    private String id;

    public E2DIllegalArgument(String id) {
        super(String.format("access denied to take action for: %s", id));
        this.id = id;
        log.error("Wrong data problem");
    }

    public String getId() {
        return this.id;
    }

}