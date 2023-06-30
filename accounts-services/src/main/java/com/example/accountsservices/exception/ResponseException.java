package com.example.accountsservices.exception;

public class ResponseException extends RuntimeException{
    private final String reason;
    private final String methodName;
    private final Object className;
    public ResponseException(Object className,String reason, String methodName){
        super(String.format("%s has occurred  for %s in %s",className,reason,methodName));
        this.reason=reason;
        this.methodName=methodName;
        this.className=className;
    }
}
