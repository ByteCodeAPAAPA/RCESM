package com.example.rces.dto;

public class RequestDataDTO {

    private long count;

    private Object data;

    public RequestDataDTO(Object data, long count) {
        this.data = data;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
