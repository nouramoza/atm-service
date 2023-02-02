package com.egs.atmservice.web.dto;

public class GenericRestResponse<T> {

    public interface STATUS {
        int SUCCESS = 0;
        int FAILURE = 1;
    }

    public GenericRestResponse() {

    }

    public GenericRestResponse(int status, String message) {
        this.message = message;
        this.status = status;
    }

    public GenericRestResponse(int status, String message, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    private T data;
    private String message;
    private int status;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenericRestResponse{");
        sb.append("data=").append(data);
        sb.append(", message='").append(message).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
