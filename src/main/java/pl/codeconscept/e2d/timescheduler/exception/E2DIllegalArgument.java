package pl.codeconscept.e2d.timescheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class E2DIllegalArgument extends RuntimeException {

    private String id;

    public E2DIllegalArgument(String id) {
        super(String.format("access denied to take action for: %s", id));
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}