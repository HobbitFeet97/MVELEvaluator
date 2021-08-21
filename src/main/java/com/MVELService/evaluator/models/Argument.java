package com.MVELService.evaluator.models;

public class Argument {

    private final String id;
    private String argumentName;
    private String type;
    private String value;

    public Argument(String id, String argumentName, String type, String value) {
        this.id = id;
        this.argumentName = argumentName;
        this.type = type;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
