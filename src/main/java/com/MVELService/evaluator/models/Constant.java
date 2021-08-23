package com.MVELService.evaluator.models;

public class Constant {

    private final int id;
    private String code;
    private String data;

    public Constant(int id, String code, String data) {
        this.id = id;
        this.code = code;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
