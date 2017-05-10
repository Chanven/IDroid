package com.andy.demo.jsonnet.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseResponse implements Serializable {
    public String message;
    public int status;
}
