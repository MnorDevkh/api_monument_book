package com.example.monumentbook.utilities.response;

import com.example.monumentbook.model.responses.BookResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResponseObject extends Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    private Object data;


    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
