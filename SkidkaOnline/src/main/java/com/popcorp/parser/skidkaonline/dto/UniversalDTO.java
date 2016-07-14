package com.popcorp.parser.skidkaonline.dto;

public class UniversalDTO<T> {

    private boolean error;
    private String message;
    private T result;

    public UniversalDTO(boolean error, String message, T result) {
        this.error = error;
        this.message = message;
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}