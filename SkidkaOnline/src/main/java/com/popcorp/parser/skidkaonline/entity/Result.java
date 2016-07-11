package com.popcorp.parser.skidkaonline.entity;

public class Result<T> {

    private boolean result;
    private String message;
    private T object;

    public Result(boolean result, String message, T object) {
        this.result = result;
        this.message = message;
        this.object = object;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
