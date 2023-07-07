package com.example.loansservices.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.time.LocalDateTime;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class InstallmentsException extends  Exception{
    private final String reason;
    private final String methodName;
    private final Object className;

    public InstallmentsException(Object className,String reason, String methodName) {
        super(String.format("%s has occurred  for %s in %s",className,reason,methodName));
        this.className=className;
        this.reason=reason;
        this.methodName=methodName;
    }
}
